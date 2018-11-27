package com.example.clarity.clarity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingsFontFragment extends Fragment {

    View v;
    Context context;

    // Buttons
    private Button back;

    private ImageView checkmark;

    private ConstraintLayout arialOption;
    private ConstraintLayout courierOption;
    private ConstraintLayout helveticaOption;
    private ConstraintLayout openDyslexicOption;
    private ConstraintLayout robotoOption;
    private ConstraintLayout verdanaOption;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_settings_font, container, false);
        context = container.getContext();

        // Bind variables for UI elements
        arialOption = (ConstraintLayout) v.findViewById(R.id.arialOption);
        courierOption = (ConstraintLayout) v.findViewById(R.id.courierOption);
        helveticaOption = (ConstraintLayout) v.findViewById(R.id.helveticaOption);
        openDyslexicOption = (ConstraintLayout) v.findViewById(R.id.openDyslexicOption);
        robotoOption = (ConstraintLayout) v.findViewById(R.id.robotoOption);
        verdanaOption = (ConstraintLayout) v.findViewById(R.id.verdanaOption);

        back = (Button) v.findViewById(R.id.btn_back_settings_font);

        checkmark = (ImageView) v.findViewById(R.id.selectionCheck);

        activateListeners();        // Activate Button listeners


        // Update the position of the selection check mark to the currently selected font
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String defaultValue = "fonts/OpenDyslexic-Regular.otf";
        String currentFont = sharedPref.getString(getString(R.string.font_pref_key), defaultValue);
        switch (currentFont) {
            case "fonts/Arial-Regular.ttf":
                updateCheckMark(0);
                break;
            case "fonts/Courier-Regular.ttf":
                updateCheckMark(1);
                break;
            case "fonts/Helvetica-Regular.ttf":
                updateCheckMark(2);
                break;
            case "fonts/OpenDyslexic-Regular.otf":
                updateCheckMark(3);
                break;
            case "fonts/Roboto-Regular.ttf":
                updateCheckMark(4);
                break;
            case "fonts/Verdana-Regular.ttf":
                updateCheckMark(5);
                break;
            default:
                updateCheckMark(3);
                break;
        }

        return v;
    }

    /*-------------------------------- FUNCTIONS --------------------------------*/
    // Activates all Button listeners
    private void activateListeners() {

        // Back to main settings page

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });


        // Set each font selection to update settings (and move check mark accordingly)

        arialOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.font_pref_key), "fonts/Arial-Regular.ttf");
                editor.apply();
                updateCheckMark(0);
            }
        });

        courierOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.font_pref_key), "fonts/Courier-Regular.ttf");
                editor.apply();
                updateCheckMark(1);
            }
        });

        helveticaOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.font_pref_key), "fonts/Helvetica-Regular.ttf");
                editor.apply();
                updateCheckMark(2);
            }
        });

        openDyslexicOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.font_pref_key), "fonts/OpenDyslexic-Regular.otf");
                editor.apply();
                updateCheckMark(3);
            }
        });

        robotoOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.font_pref_key), "fonts/Roboto-Regular.ttf");
                editor.apply();
                updateCheckMark(4);
            }
        });

        verdanaOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.font_pref_key), "fonts/Verdana-Regular.ttf");
                editor.apply();
                updateCheckMark(5);
            }
        });
    }

    private void updateCheckMark(int row) {

        // Converts dp into pixels and calculates offset of check mark
        int top = dpToPx(66 + 49 * row);
        int right = dpToPx(8);

        // Updates position of check
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) checkmark.getLayoutParams();
        lp.setMargins(0, top, right, 0);
        checkmark.setLayoutParams(lp);

    }

    private int dpToPx(int dp) {
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp, getResources().getDisplayMetrics());
        return px;
    }
}
