package com.example.clarity.clarity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

public class SettingsFontFragment extends Fragment {

    Context context;

    // Buttons
    private Button back;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings_font, container, false);
        context = container.getContext();

        activateListeners();        // Activate Button listeners


        return v;
    }

    /*-------------------------------- FUNCTIONS --------------------------------*/
    // Activates all Button listeners
    public void activateListeners() {

        // Back to main settings page
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }
}
