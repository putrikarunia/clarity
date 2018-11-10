package com.example.clarity.clarity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class FileList extends AppCompatActivity {

    private String folderName;
    private File folder;
    private File[] fileList;
    private List<String> titleList;
    private List<String> subTitleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        folderName = getIntent().getStringExtra("folder");

        File directory = this.getFilesDir();
        folder = new File(directory, folderName);

        fileList = folder.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(fileList[i]));
                titleList.add(titleList.size(), br.readLine());
                subTitleList.add(subTitleList.size(), br.readLine());
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
    }
}
