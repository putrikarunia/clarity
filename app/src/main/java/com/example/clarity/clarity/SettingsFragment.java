package com.example.clarity.clarity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsFragment extends Fragment {


    View v;
    Context context;
    SharedPreferences sharedPrefs;

    static final List<Integer> FONT_SIZES = Arrays.asList(8, 9, 10, 11, 12, 14, 18, 24, 30, 36, 48);
    static final List<Float> LINE_SPACING =  new ArrayList<>(Arrays.asList(0.8f, 1f, 1.15f, 1.5f, 2f, 3f));

    ImageView orangeHighlight;
    ImageView yellowHighlight;
    ImageView blueHighlight;
    ImageView greenHighlight;
    ImageView redHighlight;
    ImageView highlightCheck;

    ImageView navyText;
    ImageView grayText;
    ImageView blackText;
    ImageView textColorCheck;

    TextView currentFontSize;
    ImageView fontSizeUp;
    ImageView fontSizeDown;

    TextView currentLineSpacing;
    ImageView lineSpacingUp;
    ImageView lineSpacingDown;


    ConstraintLayout fontOption;
    ConstraintLayout resetSettings;

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
        resetSettings = (ConstraintLayout) v.findViewById(R.id.resetSettingsOption);

        orangeHighlight = (ImageView) v.findViewById(R.id.orangeHighlight);
        yellowHighlight = (ImageView) v.findViewById(R.id.yellowHighlight);
        blueHighlight = (ImageView) v.findViewById(R.id.blueHighlight);
        greenHighlight = (ImageView) v.findViewById(R.id.greenHighlight);
        redHighlight = (ImageView) v.findViewById(R.id.redHighlight);
        highlightCheck = (ImageView) v.findViewById(R.id.highlightCheck);

        navyText = (ImageView) v.findViewById(R.id.navyText);
        grayText = (ImageView) v.findViewById(R.id.grayText);
        blackText = (ImageView) v.findViewById(R.id.blackText);
        textColorCheck = (ImageView) v.findViewById(R.id.textColorCheck);

        currentLineSpacing = (TextView) v.findViewById(R.id.currentLineSpacing);
        lineSpacingDown = (ImageView) v.findViewById(R.id.lineSpacingDown);
        lineSpacingUp = (ImageView) v.findViewById(R.id.lineSpacingUp);

        currentFontSize = (TextView) v.findViewById(R.id.currentTextSize);
        fontSizeDown = (ImageView) v.findViewById(R.id.textSizeDown);
        fontSizeUp = (ImageView) v.findViewById(R.id.textSizeUp);

        activateListeners();

        // When fragment is loaded, make sure displayed values are updated to their current values
        updateSampleText();

        int defaultFontSize = 14;
        int currFontSize = sharedPrefs.getInt(getString(R.string.text_size_pref_key), defaultFontSize);
        currentFontSize.setText(Integer.toString(currFontSize));

        float defaultLineSpacing = 1.15f;
        float currLineSpacing = sharedPrefs.getFloat(getString(R.string.line_spacing_pref_key), defaultLineSpacing);
        currentLineSpacing.setText(Float.toString(currLineSpacing));

        int defaultHighlightColor = R.color.highlightOrange;
        int currentHighlightColor = sharedPrefs.getInt(getString(R.string.highlight_color_pref_key), defaultHighlightColor);
        switch (currentHighlightColor) {
            case R.color.highlightOrange:
                updateHighlightCheck(orangeHighlight);
                break;
            case R.color.highlightYellow:
                updateHighlightCheck(yellowHighlight);
                break;
            case R.color.highlightBlue:
                updateHighlightCheck(blueHighlight);
                break;
            case R.color.highlightGreen:
                updateHighlightCheck(greenHighlight);
                break;
            case R.color.highlightRed:
                updateHighlightCheck(redHighlight);
                break;
        }

        int defaultTextColor = R.color.textGray;
        int currentTextColor = sharedPrefs.getInt(
                getString(R.string.text_color_pref_key), defaultTextColor);
        switch (currentTextColor) {
            case R.color.textGray:
                updateTextColorCheck(grayText);
                break;
            case R.color.textBlack:
                updateTextColorCheck(blackText);
                break;
            case R.color.textNavy:
                updateTextColorCheck(navyText);
                break;
        }



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

        resetSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetToDefaultSettings();
            }
        });

        lineSpacingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float defaultLineSpacing = 1.15f;
                float currLineSpacing = sharedPrefs.getFloat(
                        getString(R.string.line_spacing_pref_key), defaultLineSpacing);

                int i = LINE_SPACING.indexOf(currLineSpacing);
                if (i != -1) {
                    i = Math.min(i + 1, LINE_SPACING.size() - 1);
                    float newLineSpacing = LINE_SPACING.get(i);
                    updateLineSpacing(newLineSpacing);
                }

            }
        });

        lineSpacingDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float defaultLineSpacing = 1.15f;
                float currLineSpacing = sharedPrefs.getFloat(
                        getString(R.string.line_spacing_pref_key), defaultLineSpacing);

                int i = LINE_SPACING.indexOf(currLineSpacing);
                if (i != -1) {
                    i = Math.max(i - 1, 0);
                    float newLineSpacing = LINE_SPACING.get(i);
                    updateLineSpacing(newLineSpacing);
                }
            }
        });

        fontSizeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int defaultFontSize = 14;
                int currFontSize = sharedPrefs.getInt(
                        getString(R.string.text_size_pref_key), defaultFontSize);

                // Change to the next larger font size, unless already max size
                int fontIndex = FONT_SIZES.indexOf(currFontSize);
                if (fontIndex != -1) {
                    fontIndex = Math.min(fontIndex + 1, FONT_SIZES.size() - 1);
                    int newFontSize = FONT_SIZES.get(fontIndex);
                    updateFontSize(newFontSize);
                }
            }
        });

        fontSizeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int defaultFontSize = 14;
                int currFontSize = sharedPrefs.getInt(
                        getString(R.string.text_size_pref_key), defaultFontSize);

                // Change t0 the next smaller font size, unless already min size
                int fontIndex = FONT_SIZES.indexOf(currFontSize);
                if (fontIndex != -1) {
                    fontIndex = Math.max(fontIndex - 1, 0);
                    int newFontSize = FONT_SIZES.get(fontIndex);
                    updateFontSize(newFontSize);
                }
            }
        });

        highlightColorListeners();
        textColorListeners();
    }

    private void highlightColorListeners() {
        orangeHighlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putInt(getString(R.string.highlight_color_pref_key), R.color.highlightOrange);
                editor.apply();
                updateHighlightCheck(orangeHighlight);
            }
        });

        yellowHighlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putInt(getString(R.string.highlight_color_pref_key), R.color.highlightYellow);
                editor.apply();
                updateHighlightCheck(yellowHighlight);
            }
        });

        blueHighlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putInt(getString(R.string.highlight_color_pref_key), R.color.highlightBlue);
                editor.apply();
                updateHighlightCheck(blueHighlight);
            }
        });

        greenHighlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putInt(getString(R.string.highlight_color_pref_key), R.color.highlightGreen);
                editor.apply();
                updateHighlightCheck(greenHighlight);
            }
        });

        redHighlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putInt(getString(R.string.highlight_color_pref_key), R.color.highlightRed);
                editor.apply();
                updateHighlightCheck(redHighlight);
            }
        });


    }

    private void textColorListeners() {
        navyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putInt(getString(R.string.text_color_pref_key), R.color.textNavy);
                editor.apply();
                updateTextColorCheck(navyText);
            }
        });

        grayText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putInt(getString(R.string.text_color_pref_key), R.color.textGray);
                editor.apply();
                updateTextColorCheck(grayText);
            }
        });

        blackText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putInt(getString(R.string.text_color_pref_key), R.color.textBlack);
                editor.apply();
                updateTextColorCheck(blackText);
            }
        });
    }

    private void updateFontSize(int fontSize) {
        currentFontSize.setText(String.valueOf(fontSize));
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(getString(R.string.text_size_pref_key), fontSize);
        editor.apply();
    }

    private void updateLineSpacing(float lineSpacing) {
        currentLineSpacing.setText(String.valueOf(lineSpacing));
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putFloat(getString(R.string.line_spacing_pref_key), lineSpacing);
        editor.apply();

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

    private void resetToDefaultSettings() {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(getString(R.string.font_pref_key), getString(R.string.open_dyslexic_dir));
        editor.putInt(getString(R.string.highlight_color_pref_key), R.color.highlightOrange);
        editor.putInt(getString(R.string.text_color_pref_key), R.color.textGray);
        editor.apply();

        updateSampleText();
        updateHighlightCheck(orangeHighlight);
        updateTextColorCheck(grayText);
        updateFontSize(14);
        updateLineSpacing(1.15f);
    }

    // Updates position of checkmark by constraining to colored circle
    private void updateHighlightCheck(ImageView highlightColor) {
        ConstraintLayout highlightLayout = (ConstraintLayout) v.findViewById(R.id.highlightColorOption);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(highlightLayout);

        constraintSet.connect(highlightCheck.getId(), ConstraintSet.START, highlightColor.getId(), ConstraintSet.START);
        constraintSet.connect(highlightCheck.getId(), ConstraintSet.END, highlightColor.getId(), ConstraintSet.END);
        constraintSet.applyTo(highlightLayout);
    }

    // Updates position of checkmark by constraining to colored circle
    private void updateTextColorCheck(ImageView fontColor) {
        ConstraintLayout textColorLayout = (ConstraintLayout) v.findViewById(R.id.textColorOption);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(textColorLayout);

        constraintSet.connect(textColorCheck.getId(), ConstraintSet.START, fontColor.getId(), ConstraintSet.START);
        constraintSet.connect(textColorCheck.getId(), ConstraintSet.END, fontColor.getId(), ConstraintSet.END);
        constraintSet.applyTo(textColorLayout);
    }

    private int dpToPx(int dp) {
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp, getResources().getDisplayMetrics());
        return px;
    }
}
