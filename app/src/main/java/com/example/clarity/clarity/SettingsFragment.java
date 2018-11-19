package com.example.clarity.clarity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends Fragment {

    Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        context = container.getContext();

        return v;
    }

    // Activates all listeners for various settings
    public void activateListeners() {

    }

    // Move to font selection screen
    private void switchToFontFragment() {
        Fragment newFragment = new SettingsFontFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragment_settings_font, newFragment).commit();
    }
}
