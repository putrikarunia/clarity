package com.example.clarity.clarity;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;



public class WordPopup {

    // Noun Project credentials
    private final String nounProjectKey = "3bedecde76fc4e7685e9731301a9f1f6";
    private final String nounProjectSecret = "61f46de0c1db4610bc755a9b8dec1ea1";

    private String word;

    private Context context;

    private View view;

    private ArrayList<String> syllables;

    private TextToSpeech tts;

    private float xCoord = 0;
    private float yCoord = 0;

    private boolean syllablesFetched = false;
    private boolean iconFetched = false;

    private LinearLayout popup;

    public WordPopup (View v, Context c) {

        view = v;

        context = c;

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

        popup = view.findViewById(R.id.popup);

    }

    public void close() {
        popup.setVisibility(View.GONE);
    }

    public void loadWord(String w, float x, float y){

        xCoord = x;
        yCoord = y;

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
        popup.setY(yCoord);


        //show the popup
        popup.setVisibility(View.VISIBLE);


    }

    //call back for fetching syllables
    public void onSyllablesFetched(String response){

        if (!response.equals("[]")) {
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

    }

    //Fetch the syllables from merriam webster api
    public void fetchSyllables(){

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="https://www.dictionaryapi.com/api/v3/references/collegiate/json/"+word+"?key=d71dc42f-637f-4003-841a-3049f70d4c1f";
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
        int startIndex = response.indexOf("link\":") + 8;
        response = response.substring(startIndex);
        int endIndex = response.indexOf("\",");
        response = response.substring(0, endIndex);

        ImageView i = (ImageView) view.findViewById(R.id.icon);
        try {
            Picasso.get().load(response).into(i);
        } catch (Exception e) {
            // avoid crash if word doesn't have an image in search
        }


    }


    // Fetch the icon from the noun project api
    public void fetchIcon () {

        // Instantiate the RequestQueue.
        ArrayList<String> keys = new ArrayList<>();
        keys.add("AIzaSyBVc_ke1IJmuQMwj4o_HyjZ_PWqV0AqAng");
        keys.add("AIzaSyCdhW_u5T_PxGQ6vsCWC1kLSpnNA-eV8TI");
        keys.add("AIzaSyCczHqAWafzqOwqM8YZP6LuaEJxLfVIgEU");
        //keys.add("AIzaSyDB8zqKQzmMsUkx_cD5Y04wO0D6vQaPjR0");
        keys.add("AIzaSyBvwhx4LiSPWcgzNDtocEV7SIc7NaUDADA");
        keys.add("AIzaSyBQEoyE-VZQpI588FdRah3RfSoVHaaj9U4");

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://www.googleapis.com/customsearch/v1?" +
                "key=" + keys.get(3) +
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
