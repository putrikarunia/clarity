package com.example.clarity.clarity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import static android.speech.tts.TextToSpeech.Engine.KEY_PARAM_VOLUME;

public class TranslateFragment extends Fragment {

    View v;
    Context context;
    String input = "";
    TextView translation;
    Button trackUp, trackDown, playAll, saveText;
    ImageView highlighter;
    int shift;            // highlight shift
    private TextToSpeech tts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate fragment view
        v = inflater.inflate(R.layout.fragment_translate, container, false);
        context = container.getContext();


        // Retrieve user input
        input = getArguments().getString("input");


        // Update font to OpenDyslexic
        translation = (TextView) v.findViewById(R.id.text_translation);
        Typeface font = Typeface.createFromAsset(context.getAssets(),  "fonts/OpenDyslexic-Regular.otf");
        translation.setTypeface(font);
        translation.setText(input);


        // Enable highlighter settings
        highlighter = (ImageView) v.findViewById(R.id.highlighter);
        shift = dpToPx(25); // Change this depending on size of font


        // Sync buttons
        trackUp = (Button) v.findViewById(R.id.btn_up);
        trackDown = (Button) v.findViewById(R.id.btn_down);
        playAll = (Button) v.findViewById(R.id.btn_play_all);
        saveText = (Button) v.findViewById(R.id.btn_save_text);


        /*-------------------------------- BUTTON LISTENERS --------------------------------*/

        // 1. Tracker: Up
        trackUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tracker("up");
            }
        });

        // 2. Tracker: Down
        trackDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tracker("down");
            }
        });

        // 3. Text-to-speech: full text
        playAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOut(translation);

            }
        });

        // 4. Save text file (TO-DO)
        saveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // to do
            }
        });


        return v;

    }


    /*-------------------------------- FUNCTIONS --------------------------------*/


    // Controls Tracker -- valid parameter is either "up" or "down"
    public void tracker(String dir) {

        RelativeLayout.LayoutParams lp =
                (RelativeLayout.LayoutParams) highlighter.getLayoutParams();

        if (dir.equals("up")) {             // Move Up

            lp.setMargins(lp.leftMargin, lp.topMargin - shift, lp.rightMargin, lp.bottomMargin);
            highlighter.setLayoutParams(lp);


        } else if (dir.equals("down")) {    // Move Down

            lp.setMargins(lp.leftMargin, lp.topMargin + shift, lp.rightMargin, lp.bottomMargin);
            highlighter.setLayoutParams(lp);

        }

    }


    // Text-to-speech -- takes in a TextView and converts to speech
    @SuppressLint("NewApi")
    private void speakOut(TextView input) {

        final CharSequence text = input.getText();

        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {

                    // Speech settings
                    Bundle b = new Bundle();
                    b.putFloat(KEY_PARAM_VOLUME, 100f);
                    tts.speak(text, TextToSpeech.QUEUE_ADD, b, "1");

                } else {
                    Log.e("TTS", "Initilization Failed!");
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


    // Saves a text to a new / existing folder
    public void saveText(View v) {

        // to-do

    }


    // Helper functions
    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

}