package com.example.clarity.clarity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileListFragment extends Fragment {

    View v;
    Context context;

    // Folder Variables
    private String galleryFolderName; // folder name for the main folder of "Gallery"
    ListView listView;
    // current folder
    private String folderName;
    private File folder;
    // file list in current folder
    private File[] fileList;
    private String[] fileNameList;
    // extracted title and subtitle for each file in current folder
    private List<String> titleList = new ArrayList<>();
    private List<String> subTitleList = new ArrayList<>();

    // Empty gallery variables
    ImageView emptyImage;
    boolean empty = true;

    // Buttons
    private Button back;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Sets fragment view
        v = inflater.inflate(R.layout.fragment_file_list, container, false);
        context = container.getContext();

        // get folder
        galleryFolderName = GalleryFragment.getGalleryFolderName();
        folderName = getArguments().getString("folder");
        emptyImage = v.findViewById(R.id.empty_folder_image);

        // sync button
        back = v.findViewById(R.id.btn_back_file_list);

        // Set Folder Header Name
        TextView header = v.findViewById(R.id.folder_header);
        header.setText(folderName);

        // Get files in the folder
        folder = getFolder(context, folderName);
        fileList = getFolderFiles(context, folderName);
        fileNameList = getFolderFileNames(context, folderName);

        if (fileList.length > 0) {
            if (empty) {
                emptyImage.setVisibility(View.GONE);
            }
            empty = false;

            Pair<List<String>, List<String>> titles = getTitlesAndSubtitles(context, folderName);
            titleList = titles.first;
            subTitleList = titles.second;

            // Setup list view for the contents of the folder
            listView = v.findViewById(R.id.file_list_view);
            FileListAdapter fileListAdapter = new FileListAdapter(context, titleList, subTitleList);
            listView.setAdapter(fileListAdapter);
            activateNonEmptyListeners();
        } else {
            emptyImage.setVisibility(View.VISIBLE);
            empty = true;
        }
        activateListeners();
        return v;
    }

    /*-------------------------------- HELPER FUNCTIONS --------------------------------*/
    // Get a Folder by name
    public static File getFolder(Context ctx, String folderName) {
        File directory = ctx.getFilesDir();
        File galleryFolder = new File(directory, GalleryFragment.getGalleryFolderName());
        File folder = new File(galleryFolder, folderName);
        return folder;
    }
    // Get list of files in this folder
    public static File[] getFolderFiles(Context ctx, String folderName) {
        File folder = getFolder(ctx, folderName);
        return folder.listFiles();
    }
    // Get list of file names in this folder
    public static String[] getFolderFileNames(Context ctx, String folderName) {
        File folder = getFolder(ctx, folderName);
        return folder.list();
    }


    /*-------------------------------- FUNCTIONS --------------------------------*/
    // apply listeners
    // Activates all Button listeners
    public void activateNonEmptyListeners() {
        // 1. Open clicked folder
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Pair<String, String> titleAndText = getText(v.getContext(), folderName, fileNameList[position]);

                // Prepares for next fragment
                Fragment newFragment = new TranslateFragment();
                Bundle args = new Bundle();                             // Use bundle to send info from fragment to fragment
                args.putString("input", titleAndText.second);           // Store user input into bundle
                args.putString("title", titleAndText.first);            // store title if applicable
                args.putString("folderName", folderName);               // Store folder name if applicable
                args.putString("fileName", fileNameList[position]);     // Store file name if applicable
                newFragment.setArguments(args);                         // Set next fragment's args to bundle

                // Switch fragment views
                getFragmentManager().beginTransaction()
                        .add(R.id.fragment_file_list, newFragment)
                        .addToBackStack(null)
                        .commit();

            }
        });
    }
    public void activateListeners() {
        // 1. Back to previous page
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    // Get titles and subtitle lists
    public static Pair<List<String>, List<String>> getTitlesAndSubtitles(Context ctx, String folderName) {
        File[] fileList = getFolderFiles(ctx, folderName);
        List<String> titleList = new ArrayList<>();
        List<String> subTitleList = new ArrayList<>();
        // Get content from each file (1st line = Title, rest = content)
        for (int i = 0; i < fileList.length; i++) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(fileList[i]));
                titleList.add(titleList.size(), br.readLine());
                char[] buf = new char[45];
                br.read(buf, 0, 45);
                subTitleList.add(subTitleList.size(), String.valueOf(buf) + "...");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return new Pair<>(titleList, subTitleList);
    }

    // get text from a file
    public static Pair<String, String> getText(Context ctx, String folderName, String fileName) {
        String title = "";
        StringBuilder text = new StringBuilder();

        File folder = getFolder(ctx, folderName);
        File file = new File(folder, fileName);
        if (!file.exists()) {
            Toast.makeText(ctx, "An error occurred, " + fileName + " doesn't exist.",
                    Toast.LENGTH_SHORT).show();
        }
        // Get content from each file (1st line = Title, rest = content)
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            title = br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ctx, "An error occurred while reading " + fileName,
                    Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return new Pair<>(title, text.toString());
    }
}
