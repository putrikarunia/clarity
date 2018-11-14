package com.example.clarity.clarity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GalleryFragment extends Fragment {

    // View variables
    Context context;
    GalleryAdapter galleryAdapter;

    // Folders variables
    File galleryFolder;
    List<File> contentList;
    List<String> nameList;
    static String galleryFolderName = "Gallery";
    Boolean empty = true;

    // Gallery views
    ImageView emptyImage;
    GridView gridview;

    // Buttons
    ImageButton createFolder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_gallery, container, false);
        context = container.getContext();

        // get gallery folder
        galleryFolder = getGalleryFolder(context);
        // List out folders into galleryContent to determine if empty
        File[] galleryContent = galleryFolders(context);
        String[] galleryNames = galleryFolderNames(context);
        if (galleryContent.length > 0) {
            empty = false;
        } else {
            emptyImage = v.findViewById(R.id.empty_gallery_image);
            emptyImage.setVisibility(View.VISIBLE);
        }

        // Handle "create folder" button
        createFolder = v.findViewById(R.id.create_folder);

        // Setup gridview for folders
        gridview = v.findViewById(R.id.gridview);
        contentList = new ArrayList<File>(Arrays.asList(galleryContent));
        nameList = new ArrayList<String>(Arrays.asList(galleryNames));
        galleryAdapter = new GalleryAdapter(getActivity().getApplicationContext(), contentList, nameList, getActivity());
        gridview.setAdapter(galleryAdapter);
        activateListeners();


        return v;
    }

    /*-------------------------------- HELPER FUNCTIONS --------------------------------*/
    // Get Gallery Folder
    public static File getGalleryFolder(Context ctx) {
        File directory = ctx.getFilesDir();
        File galleryFolder = new File(directory, galleryFolderName);
        // Check if directory "Gallery" exists inside the app's folder
        if (!galleryFolder.exists()) {
            galleryFolder.mkdirs();
        }
        return  galleryFolder;
    }
    // Get Gallery folder nmae
    public static String getGalleryFolderName() {
        return galleryFolderName;
    }
    // Get list of folders in gallery
    public static File[] galleryFolders(Context ctx) {
        File galleryFolder = getGalleryFolder(ctx);
        return galleryFolder.listFiles();
    }
    // Get list of folder names in gallery
    public static String[] galleryFolderNames(Context ctx) {
        File galleryFolder = getGalleryFolder(ctx);
        return galleryFolder.list();
    }

    /*-------------------------------- FUNCTIONS --------------------------------*/

    // Activates all Button listeners
    public void activateListeners() {

        // 1. Create Folder
        createFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptFolderName();
            }
        });

        // 2. Select Folder
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // Prepares for next fragment
                Fragment newFragment = new FileListFragment();
                Bundle args = new Bundle();                             // Use bundle to send info from fragment to fragment
                args.putString("folder", nameList.get(position));       // Store folder to open
                newFragment.setArguments(args);                         // Set next fragment's args to bundle

                // Switch fragment views
                getFragmentManager().beginTransaction()
                        .add(R.id.fragment_gallery, newFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

    // CREATE FOLDER
    // Build dialog to ask for folder name
    public void promptFolderName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Folder name");

        // Set up the input
        final EditText input = new EditText(context);
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String folderName = input.getText().toString();
                createFolderInGallery(folderName);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    // For creating new folder
    public void createFolderInGallery(String folderName) {
        File file = createFolder(context, folderName);
        if (file != null) {
            nameList.add(nameList.size(),folderName);
            contentList.add(contentList.size(),file);
            galleryAdapter.notifyDataSetChanged();

            Toast.makeText(getActivity().getApplicationContext(), "Created folder " + folderName,
                    Toast.LENGTH_SHORT).show();

            if (empty) {
                empty = false;
                emptyImage.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Unable to create folder " + folderName,
                    Toast.LENGTH_SHORT).show();
        }
    }

    // For creating new folder
    public static File createFolder(Context context, String folderName) {
        File galleryFolder = getGalleryFolder(context);
        File file = new File(galleryFolder, folderName);
        int i = 0;
        while (file.exists()) {
            folderName = folderName + "_" + i;
            file = new File(galleryFolder, folderName);
            i++;
        }

        if (file.mkdirs()) {
            return file;
        }
        return null;
    }

    // DELETE ALL FOLDERS
    // JUST FOR DEVELOPMENT!!
    public void emptyAll(String[] galleryNames) {
        for (int i=0; i<galleryNames.length; i++) {
            File myFile = new File(galleryFolder, galleryNames[i]);
            myFile.delete();
        }
    }
}
