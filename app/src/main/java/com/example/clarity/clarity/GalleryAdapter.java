package com.example.clarity.clarity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class GalleryAdapter extends BaseAdapter {
    private Context mContext;
    private List<File> content;
    private List<String> names;
    LayoutInflater inflter;
    Activity activity;

    public GalleryAdapter(Context c, List<File> galleryContent, List<String> galleryNames, Activity activity) {
        mContext = c;
        content = galleryContent;
        names = galleryNames;
        this.activity = activity;
        inflter = (LayoutInflater.from(c));
    }

    public int getCount() {
        return content.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.gallery_folder_list, null);
        TextView folderName = convertView.findViewById(R.id.folder_name);
        folderName.setText(names.get(position));
        return convertView;
    }


}