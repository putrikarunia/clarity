package com.example.clarity.clarity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.sql.SQLOutput;

public class SettingsFragment extends Fragment {


    View v;
    Context context;
    SharedPreferences sharedPrefs;


    ConstraintLayout fontOption;

    TextView fontSample;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_settings, container, false);
        context = container.getContext();

        // Load the shared preferences (user's settings)
        // and ensure values are updated here when changed by user
        sharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        sharedPrefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                updateSampleText();
            }
        });

        // Bind variables for UI elements
        fontOption = (ConstraintLayout) v.findViewById(R.id.fontOption);

        activateListeners();

        updateSampleText();



        return v;
    }

    // Activates all listeners for various settings
    public void activateListeners() {
        fontOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new SettingsFontFragment();
                getFragmentManager().beginTransaction()
                        .add(R.id.fragment_settings, newFragment)
                        .addToBackStack(null)
                        .commit();            }
        });
    }

    // Sync font sample to selected font
    private void updateSampleText() {
        String defaultValue = "fonts/OpenDyslexic-Regular.otf";
        String selectedFont = sharedPrefs.getString(getString(R.string.font_pref_key), defaultValue);
        fontSample = (TextView) v.findViewById(R.id.fontSample);
        fontSample.setTypeface(Typeface.createFromAsset
                (context.getAssets(), selectedFont));
        switch (selectedFont) {
            case "fonts/Arial-Regular.ttf":
                fontSample.setText(R.string.arial);
                break;
            case "fonts/Courier-Regular.ttf":
                fontSample.setText(R.string.courier);
                break;
            case "fonts/Helvetica-Regular.ttf":
                fontSample.setText(R.string.helvetica);
                break;
            case "fonts/OpenDyslexic-Regular.otf":
                fontSample.setText(R.string.open_dyslexic);
                break;
            case "fonts/Roboto-Regular.ttf":
                fontSample.setText(R.string.roboto);
                break;
            case "fonts/Verdana-Regular.ttf":
                fontSample.setText(R.string.verdana);
                break;
            default:
                fontSample.setText(R.string.open_dyslexic);
                break;
        }
    }
}
