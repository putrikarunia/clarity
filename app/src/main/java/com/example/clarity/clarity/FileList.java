package com.example.clarity.clarity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileList extends AppCompatActivity {

    private String folderName;
    private String galleryFolderName;
    private File folder;
    private File[] fileList;
    private String[] fileNameList;
    private List<String> titleList = new ArrayList<>();
    private List<String> subTitleList = new ArrayList<>();
    ImageView emptyImage;
    boolean empty = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        galleryFolderName = getIntent().getStringExtra("galleryFolderName");
        folderName = getIntent().getStringExtra("folder");
        emptyImage = findViewById(R.id.empty_folder_image);

        // Set Folder Header Name
        TextView header = findViewById(R.id.folder_header);
        header.setText(folderName);

        // Get files in the folder
        File directory = this.getFilesDir();
        File galleryFolder = new File(directory, galleryFolderName);
        folder = new File(galleryFolder, folderName);

        fileList = folder.listFiles();
        fileNameList = folder.list();

        if (fileList.length > 0) {
            if (empty) {
                emptyImage.setVisibility(View.GONE);
            }
            empty = false;

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

            // Setup list view for the contents of the folder
            ListView listView = findViewById(R.id.file_list_view);
            FileListAdapter fileListAdapter = new FileListAdapter(this.getApplicationContext(), titleList, subTitleList, this);
            listView.setAdapter(fileListAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
//                Intent intent = new Intent(v.getContext(), FileList.class);
//                intent.putExtra("folder", folderName);
//                intent.putExtra("fileName", fileNameList[position]);
//                getActivity().startActivityForResult(intent, position);
                }
            });
        } else {
            emptyImage.setVisibility(View.VISIBLE);
            empty = true;
        }

    }

    private void writeToFile(String data, String title) {
        File file = new File(folder, title);
        data = title + "\n" + data;
        int i = 0;
        while (file.exists()) {
            title = title + "_" + i;
            file = new File(folder, title);
            i++;
        }

        FileOutputStream outstream;

        try{
            if(!file.exists()){
                file.createNewFile();
            }
            outstream = new FileOutputStream(file);
            outstream.write(data.getBytes());
            outstream.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
