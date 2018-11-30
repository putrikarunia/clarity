package com.example.clarity.clarity;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import android.content.*;

import java.util.ArrayList;

import android.speech.tts.TextToSpeech;
import android.view.*;
import android.widget.TextView;
import java.util.Locale;

import android.widget.*;
import com.android.volley.Request;


import com.squareup.picasso.*;



public class WordPopup {

    // Noun Project credentials
    private final String nounProjectKey = "3bedecde76fc4e7685e9731301a9f1f6";
    private final String nounProjectSecret = "61f46de0c1db4610bc755a9b8dec1ea1";

    private String word;

    private Context context;

    private View view;

    private ArrayList<String> syllables;

    private TextToSpeech tts;

    private float yCoord = 0;

    private boolean syllablesFetched = false;
    private boolean iconFetched = false;

    public WordPopup (View v, Context c, float y) {

        view = v;

        context = c;

        yCoord = y;

        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        });
        tts.setLanguage(Locale.US);

        ImageButton button = (ImageButton) v.findViewById(R.id.audioButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tts.speak(word, TextToSpeech.QUEUE_ADD, null);
            }
        });


    }

    public void loadWord(String w){

        word = w;

        fetchSyllables();

        fetchIcon();


    }

    private void formatBox(){

        //change the text
        String text = "";
        for (String s: syllables) {
            text = text + s + " ● ";
        }
        text = text.substring(0, text.length() - 2);
        ((TextView) view.findViewById(R.id.syllablesText)).setText(text);

        //position the popup
        view.findViewById(R.id.popup).setY(yCoord);


        //show the popup
        view.findViewById(R.id.popup).setVisibility(View.VISIBLE);




    }

    //call back for fetching syllables
    public void onSyllablesFetched(String response){

        System.out.println(response);

        //Get the word broken into syllables with * characters
        int startIndex = response.indexOf("hw\":\"") + 5;
        response = response.substring(startIndex);
        int endIndex = response.indexOf("\",");
        response = response.substring(0, endIndex);

        int syllableStartIndex = 0;
        int syllableEndIndex = 0;

        syllables = new ArrayList<>();

        while (response.contains("*")) {

            syllableEndIndex = response.indexOf("*");
            String syllable = response.substring(syllableStartIndex, syllableEndIndex);
            syllables.add(syllable);

            response = response.substring(syllableEndIndex + 1, response.length());

        }

        syllableEndIndex = response.length();
        String syllable = response.substring(syllableStartIndex, syllableEndIndex);
        syllables.add(syllable);

        syllablesFetched = true;

        if (syllablesFetched && iconFetched || true) {

            formatBox();

        }

    }

    //Fetch the syllables from merriam webster api
    public void fetchSyllables(){

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="https://www.dictionaryapi.com/api/v3/references/collegiate/json/"+word+"?key=d71dc42f-637f-4003-841a-3049f70d4c1f";
        System.out.println(url);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        onSyllablesFetched(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    public void onIconFetched(String response){

        //Parse out the image link
        System.out.println("IN");
        int startIndex = response.indexOf("link\":") + 8;
        response = response.substring(startIndex);
        int endIndex = response.indexOf("\",");
        response = response.substring(0, endIndex);
        System.out.println(response);

        ImageView i = (ImageView) view.findViewById(R.id.icon);
        Picasso.get().load(response).into(i);


    }


    // Fetch the icon from the noun project api
    public void fetchIcon () {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://www.googleapis.com/customsearch/v1?" +
                "key=AIzaSyBVc_ke1IJmuQMwj4o_HyjZ_PWqV0AqAng" +
                "&num=1" +
                "&q=" + word + "%20icon" +
                "&imgSize=medium" +
                "&searchType=image" +
                "&filetype=png" +
                "&cx=017207249476109118120%3Ajz24zawggai";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println("WOW");
                        System.out.println(response);
                        onIconFetched(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


}
