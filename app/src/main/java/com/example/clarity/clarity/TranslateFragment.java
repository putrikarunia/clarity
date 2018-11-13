package com.example.clarity.clarity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import android.widget.Toast;

import java.text.BreakIterator;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.Engine.KEY_PARAM_VOLUME;

public class TranslateFragment extends Fragment {

    // Variables
    private View v;
    private Context context;

    // Input variables
    private String input = "";            // Stores user input
    private TextView translation;         // TextView of translated input

    // Tracker variables
    private Button trackUp, trackDown;
    private ImageView highlighter;        // Tracker highlighter
    private int shift;                    // highlight shift increment
    private int numOfLines = 0;           // # of lines in input TextView (sets bounds for tracker)
    private int currTracker = 1;          // Current line of tracker - default: starts at 0

    // Word-selection variables
    private String selectedWord = "";     // Selected word

    // Text-to-Speech variables
    private Button playAll;
    private TextToSpeech tts;

    // Saving text variables
    private Button saveText;


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

        // Update TextView to input
        translation.setText(input);
        //numOfLines = translation.getLineCount() + 1; // set number of lines for tracker's bounds

        translation.post(new Runnable() {
            @Override
            public void run() {
                numOfLines = translation.getLineCount();
            }
        });

        // Tracks word selection
        trackWordSelection();


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
            if (currTracker > 1) {
                lp.setMargins(lp.leftMargin, lp.topMargin - shift, lp.rightMargin, lp.bottomMargin);
                highlighter.setLayoutParams(lp);
                currTracker -= 1;   // Decrement tracker line
            }


        } else if (dir.equals("down")) {    // Move Down
            if (currTracker < numOfLines) {
                lp.setMargins(lp.leftMargin, lp.topMargin + shift, lp.rightMargin, lp.bottomMargin);
                highlighter.setLayoutParams(lp);
                currTracker += 1;   // Increment tracker line
            }
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


    // Tracks word selection
    private void trackWordSelection() {

        // Trim text down by words
        String input = translation.getText().toString().trim();

        // Set translate text view to text broken down into words
        TextView translateView = (TextView) v.findViewById(R.id.text_translation);
        translateView.setMovementMethod(LinkMovementMethod.getInstance());
        translateView.setText(input, TextView.BufferType.SPANNABLE);

        Spannable span = (Spannable) translateView.getText();
        BreakIterator iterator = BreakIterator.getWordInstance(Locale.US);
        iterator.setText(input);

        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator
                .next()) {


            String possibleWord = input.substring(start, end);
            if (Character.isLetterOrDigit(possibleWord.charAt(0))) {
                ClickableSpan clickSpan = getClickableSpan(possibleWord);
                span.setSpan(clickSpan, start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            }

        }


    }

    // Highlights selected word
    private ClickableSpan getClickableSpan(final String word) {
        return new ClickableSpan() {
            final String selected;
            {   selected = word;    }

            @Override
            public void onClick(View widget) {
                selectedWord = selected;
                Log.d("tapped on:", selected);
                Toast.makeText(widget.getContext(), selected, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                //super.updateDrawState(ds);
                //ds.setTypeface(Typeface.createFromAsset(context.getAssets(),  "fonts/OpenDyslexic-Bold.otf"));
            }

        };
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