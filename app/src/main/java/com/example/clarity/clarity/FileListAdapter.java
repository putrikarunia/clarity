package com.example.clarity.clarity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class FileListAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> Titles;
    private List<String> SubTitles;
    LayoutInflater inflter;
    Activity activity;

    public FileListAdapter(Context c, List<String> titles, List<String> subTitles, Activity activity) {
        mContext = c;
        Titles = titles;
        SubTitles = subTitles;
        this.activity = activity;
        inflter = (LayoutInflater.from(c));
    }

    public int getCount() {
        return Titles.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.gallery_file_list, null);
        TextView title = convertView.findViewById(R.id.file_list_title);
        title.setText(Titles.get(position));
        TextView subTitle = convertView.findViewById(R.id.file_list_subtitle);
        subTitle.setText(SubTitles.get(position));
        return convertView;
    }


}