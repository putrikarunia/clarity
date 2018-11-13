package com.example.clarity.clarity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PasteFragment extends Fragment {

    View v;
    Context context;
    Button btn_translate;
    EditText input;
    TextView wordCount;
    int maxWordCount;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Sets fragment view
        v = inflater.inflate(R.layout.fragment_paste, container, false);
        context = container.getContext();


        // Syncs user input
        input = (EditText) v.findViewById(R.id.paste_input);


        // Updates word count every time input changes
        maxWordCount = 1000;      // Temporary
        wordCount = (TextView) v.findViewById(R.id.paste_input_count);
        input.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                updateWordCount();

            }

        });


        // Translates user input
        btn_translate = (Button) v.findViewById(R.id.button_translate);
        btn_translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateText();
            }

        });


        return v;

    }


    // Takes user's input text and switches to Translate view
    public void translateText() {

        // Check if textbox is empty
        if (input.getText().toString().matches("")) {

            Toast.makeText(context, "Enter some text to translate", Toast.LENGTH_SHORT).show();

        } else {

            // Prepares for next fragment
            Fragment newFragment = new TranslateFragment();
            Bundle args = new Bundle();                             // Use bundle to send info from fragment to fragment
            args.putString("input", input.getText().toString());    // Store user input into bundle
            newFragment.setArguments(args);                         // Set next fragment's args to bundle

            // Switch fragment views
            getFragmentManager().beginTransaction().replace(R.id.fragment_paste, newFragment).commit();

        }

    }


    // Updates user's input word count
    public void updateWordCount() {

        String[] count = input.getText().toString().split("\\s+");
        wordCount.setText(count.length + "/" + maxWordCount);

    }


}
