package com.example.clarity.clarity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.Button;
import android.widget.ImageButton;


public class AddFragment extends Fragment implements View.OnClickListener {

    View v;
    Context context;
    Fragment fragment;
    private FragmentManager fragmentManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_add, container, false);
        context = container.getContext();

        ((Button) v.findViewById(R.id.paste_text)).setOnClickListener(this);
        ((ImageButton) v.findViewById(R.id.use_cam)).setOnClickListener(this);

        return v;

    }

    @Override
    public void onClick(View v) {

        Fragment newFragment = null;

        switch (v.getId()) {

            // 1. User selects to input text
            case R.id.paste_text:
                newFragment = new PasteFragment();
                break;

            // 2. User selects to take a photo
            case R.id.use_cam:

                if (checkCamPermission()) {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivity(intent);

                } else { requestCamPermission(); }

                break;

        }

        getFragmentManager().beginTransaction().replace(R.id.fragment_add, newFragment).commit();
    }


    // Check if Camera permissions are granted
    public boolean checkCamPermission() {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }

    }

    // Request for Camera permissions
    public void requestCamPermission() {

        ActivityCompat.requestPermissions(getActivity(),
                new String[] {Manifest.permission.CAMERA},
                100);

    }

}