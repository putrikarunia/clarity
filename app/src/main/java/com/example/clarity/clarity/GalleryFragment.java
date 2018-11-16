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
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
    ImageView shadowView;
    RelativeLayout optionsContainer;
    TextView optionsContainerHeader;

    // Buttons
    ImageButton createFolder;
    ImageButton exitOptionsContainer;
    RelativeLayout deleteButton;
    RelativeLayout renameButton;
    Boolean deleteContainerUp = false;
    String currentFolder = null;


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

        // Options for delete and rename
        optionsContainer = v.findViewById(R.id.options_container);
        shadowView = v.findViewById(R.id.options_shadow_view);
        deleteButton = v.findViewById(R.id.options_container_delete);
        renameButton = v.findViewById(R.id.options_container_rename);
        optionsContainerHeader = v.findViewById(R.id.options_container_header);
        exitOptionsContainer = v.findViewById(R.id.exit_options_container);

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

        // 3. Show folder options
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                currentFolder = nameList.get(pos);
                optionsContainerHeader.setText(currentFolder);
                slideUp(optionsContainer);
                return true;
            }
        });

        // 4. Hide optoins container
        exitOptionsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideDown(optionsContainer);
                currentFolder = null;
            }
        });
        shadowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideDown(optionsContainer);
                currentFolder = null;
            }
        });

        // 5. Show confirmation to delete folder
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFolder != null) {
                    confirmDeleteFolder(currentFolder);
                } else {
                    Toast.makeText(context, "Unable to delete folder",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 6. Show dialog to rename folder
        renameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFolder != null) {
                    promptRenameFolder(currentFolder);
                } else {
                    Toast.makeText(context, "Unable to rename folder",
                            Toast.LENGTH_SHORT).show();
                }
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


    // DELETE FOLDER
    // slide the view from below itself to the current position
    public void slideUp(View view){
        shadowView.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(200);
        animate.setFillAfter(false);
        view.startAnimation(animate);
        deleteContainerUp = true;
    }

    // slide the view from its current position to below itself
    public void slideDown(View view){
        shadowView.setVisibility(View.GONE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(200);
        animate.setFillAfter(false);
        view.startAnimation(animate);
        optionsContainer.setVisibility(View.GONE);
        deleteContainerUp = false;
    }


    // Build dialog to confirm folder delete
    public void confirmDeleteFolder(final String folderName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Folder");
        builder.setMessage("Are you sure you want to delete folder " + folderName + " and all it's contents?");

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (deleteFolder(folderName)) {
                    slideDown(optionsContainer);
                    Toast.makeText(context, "Deleted folder " + folderName,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Unable to delete folder " + folderName,
                            Toast.LENGTH_SHORT).show();
                }
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

    // Delete folder
    public boolean deleteFolder(String folderName) {
        File galleryFolder = GalleryFragment.getGalleryFolder(context);
        File folder = new File(galleryFolder, folderName);
        if (folder.isDirectory())
        {
            // Delete all files in folder
            String[] children = folder.list();
            for (int i = 0; i < children.length; i++)
            {
                File file = new File(folder, children[i]);
                if (!file.delete()) {
                    return false;
                }
            }
            // Delete folder and update list
            if (contentList.indexOf(folder) == nameList.indexOf(folderName) && folder.delete()) {
                contentList.remove(folder);
                nameList.remove(folderName);
                galleryAdapter.notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }

    // Build dialog to ask rename folder
    public void promptRenameFolder(final String folderName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Rename File");

        // Set up the input
        final EditText input = new EditText(context);
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString();
                if (renameFolder(folderName, newName)) {
                    slideDown(optionsContainer);
                    Toast.makeText(context, "Renamed folder " + folderName + " to " + newName,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Unable to rename folder " + folderName,
                            Toast.LENGTH_SHORT).show();
                }
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

    // Delete folder
    public boolean renameFolder(String folderName, String newName) {
        File galleryFolder = GalleryFragment.getGalleryFolder(context);
        File oldFile = new File(galleryFolder, folderName);
        File newFile = new File(galleryFolder, newName);
        if (oldFile.exists()) {
            // update list
            if (contentList.indexOf(oldFile) == nameList.indexOf(folderName) && oldFile.renameTo(newFile)) {
                int index = nameList.indexOf(folderName);
                contentList.remove(oldFile);
                nameList.remove(folderName);
                contentList.add(index, newFile);
                nameList.add(index, newName);
                galleryAdapter.notifyDataSetChanged();
                return true;
            }

        }
        return false;
    }
}
