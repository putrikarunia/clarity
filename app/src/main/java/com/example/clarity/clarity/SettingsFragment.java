package com.example.clarity.clarity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SettingsFragment extends Fragment {


    Context context;
    SharedPreferences sharedPrefs;

    Button fontSelect;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        context = container.getContext();

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);


        // Bind variables for UI elements
        fontSelect = (Button) v.findViewById(R.id.goToFontSelect);

        activateListeners();

        return v;
    }

    // Activates all listeners for various settings
    public void activateListeners() {
        fontSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new SettingsFontFragment();
                getFragmentManager().beginTransaction()
                        .add(R.id.fragment_settings, newFragment)
                        .addToBackStack(null)
                        .commit();            }
        });
    }

    // Move to font selection screen
    private void switchToFontFragment() {

    }
}
