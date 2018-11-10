package com.example.clarity.clarity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
    File galleryFolder;
    List<File> contentList;
    List<String> nameList;
    String galleryFolderName = "Gallery";
    Boolean empty = true;
    Context context;
    ImageView emptyImage;
    GalleryAdapter galleryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_gallery, container, false);
        context = container.getContext();

        // Check if directory "Gallery" exists inside the app's folder
        File directory = context.getFilesDir();
        galleryFolder = new File(directory, galleryFolderName);
        if (!galleryFolder.exists()) {
            galleryFolder.mkdirs();
        }

        // List out folders into galleryContent to determine if empty
        File[] galleryContent = galleryFolder.listFiles();
        String[] galleryNames = galleryFolder.list();
        if (galleryContent.length > 0) {
            empty = false;
        } else {
            emptyImage = v.findViewById(R.id.empty_gallery_image);
            emptyImage.setVisibility(View.VISIBLE);
        }

        // Handle "create folder" button
        ImageButton createFolder = v.findViewById(R.id.create_folder);
        createFolder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                promptFolderName();
                // Update the GridView
            }
        });

        // Setup gridview for folders
        GridView gridview = v.findViewById(R.id.gridview);
        contentList = new ArrayList<File>(Arrays.asList(galleryContent));
        nameList = new ArrayList<String>(Arrays.asList(galleryNames));
        galleryAdapter = new GalleryAdapter(getActivity().getApplicationContext(), contentList, nameList, getActivity());
        gridview.setAdapter(galleryAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(v.getContext(), FileList.class);
                intent.putExtra("folder", nameList.get(position));
                intent.putExtra("galleryFolderName", galleryFolderName);
                getActivity().startActivityForResult(intent, position);
            }
        });


        return v;
    }

    // Build dialog to ask for folder name
    public void promptFolderName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Title");

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
                createFolder(folderName);
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
    public void createFolder(String folderName) {
        File file = new File(galleryFolder, folderName);
        int i = 0;
        while (file.exists()) {
            folderName = folderName + "_" + i;
            file = new File(galleryFolder, folderName);
            i++;
        }

        if (file.mkdirs()) {
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

    // JUST FOR DEVELOPMENT!!
    public void emptyAll(String[] galleryNames) {
        for (int i=0; i<galleryNames.length; i++) {
            File myFile = new File(galleryFolder, galleryNames[i]);
            myFile.delete();
        }
    }
}
