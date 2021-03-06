package com.example.clarity.clarity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.speech.tts.TextToSpeech.Engine.KEY_PARAM_VOLUME;

public class TranslateFragment extends Fragment {

    // Preference file
    SharedPreferences sharedPrefs;

    // Variables
    private View v;
    private Context context;

    // Input variables
    private String input = "";            // Stores user input
    private TextView translation;         // TextView of translated input

    // Title variables
    private String title = "";            // Stores title if applicable (from saved file)
    private TextView translationTitle;

    // Tracker variables
    private Button trackUp, trackDown;
    private ImageView highlighter;        // Tracker highlighter
    private int shift;                    // highlight shift increment
    private int numOfLines = 0;           // # of lines in input TextView (sets bounds for tracker)
    private int currTracker = -1;         // Current line of tracker - default: starts at -1, not displayed
    int highlightColor;

    // Word-selection variables
    private String selectedWord = "";     // Selected word
    private String syllable = "";
    private Button playWord;

    // Text-to-Speech variables
    private Button playAll;
    private TextToSpeech tts;
    private boolean currentlyPlaying = false;

    // Saving text variables
    private Button saveText;
    private ImageButton exitFolderContainer;
    private ImageButton addFolderContainer;
    private RelativeLayout folderContainer;
    private ImageView shadowView;
    FolderListAdapter folderListAdapter;
    private ListView folderListView;
    List<File> folderList = new ArrayList<>();
    List<String> folderNameList = new ArrayList<>();

    // Delete file variables
    private String folderName = "";            // Stores title if applicable (from saved file)
    private String fileName = "";
    private Button deleteFile;
    private boolean newText = true;              // If new text, can save, otherwise can delete

    // Back button
    private Button back;

    // Word popup
    private WordPopup wordPopup;

    //coordinates of touch
    private float tapX = 0;
    private float tapY = 0;

    int defaultTextColor = R.color.textGray;
    int selectedTextColor = R.color.textGray;

    int defaultTextSize = 14;
    int selectedTextSize = 14;

    float defaultLineSpacing = 1.15f;
    float selectedLineSp = 1.15f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate fragment view
        v = inflater.inflate(R.layout.fragment_translate, container, false);
        context = container.getContext();
        sharedPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);


        // Retrieve user input
        input = getArguments().getString("input");

        // Set title if applicable
        translationTitle = v.findViewById(R.id.translate_header);
        title = getArguments().getString("title");
        if (title != null && !title.equals("")) {
            translationTitle.setText(title);
            newText = false;
        }

        // Set folder name and file name if applicable
        folderName = getArguments().getString("folderName");
        fileName = getArguments().getString("fileName");

        // Get text display settings from saved preferences

        String defaultFont = "fonts/OpenDyslexic-Regular.otf";
        String selectedFont = sharedPrefs.getString(getString(R.string.font_pref_key), defaultFont);

        defaultTextColor = R.color.textGray;
        selectedTextColor = sharedPrefs.getInt(
                getString(R.string.text_color_pref_key), defaultTextColor);

        defaultTextSize = 14;
        selectedTextSize = sharedPrefs.getInt(
                getString(R.string.text_size_pref_key), defaultTextSize);

        defaultLineSpacing = 1.15f;
        selectedLineSp = sharedPrefs.getFloat(
                getString(R.string.line_spacing_pref_key), defaultLineSpacing);


        // Update Translation TextView settings
        translation = (TextView) v.findViewById(R.id.text_translation);
        translation.setTypeface(Typeface.createFromAsset
                (context.getAssets(), selectedFont));
        translation.setText(input);                     // Store input in TextView
        translation.setTextColor(getResources().getColor(selectedTextColor));
        translation.setTextSize(selectedTextSize);
        translation.setLineSpacing(0, selectedLineSp);
        translation.post(new Runnable() {               // Retrieve & set num of lines in TV; Tracker's bounds
            @Override
            public void run() {
                numOfLines = translation.getLineCount();
            }
        });


        // Sync Buttons
        trackUp = (Button) v.findViewById(R.id.btn_up);
        trackDown = (Button) v.findViewById(R.id.btn_down);
        playAll = (Button) v.findViewById(R.id.btn_play_all);
        saveText = (Button) v.findViewById(R.id.btn_save_text);
        deleteFile = (Button) v.findViewById(R.id.btn_delete_file);
        back = (Button) v.findViewById(R.id.btn_back_translation);
        exitFolderContainer = v.findViewById(R.id.exit_folder_container);
        addFolderContainer = v.findViewById(R.id.add_folder_container);

        // Sync button save or delete
        if (newText) {
            saveText.setVisibility(View.VISIBLE);
            deleteFile.setVisibility(View.GONE);
        } else {
            saveText.setVisibility(View.GONE);
            deleteFile.setVisibility(View.VISIBLE);
        }


        // Sync Folder List
        shadowView = v.findViewById(R.id.save_shadow_view);
        folderContainer = v.findViewById(R.id.folder_container);
        folderListView = v.findViewById(R.id.folder_container_list);
        File[] folders = GalleryFragment.galleryFolders(context);
        String[] folderNames = GalleryFragment.galleryFolderNames(context);
        folderList = new ArrayList<>(Arrays.asList(folders));
        folderNameList = new ArrayList<>(Arrays.asList(folderNames));
        folderListAdapter = new FolderListAdapter(getActivity().getApplicationContext(), folderList, folderNameList, getActivity());
        folderListView.setAdapter(folderListAdapter);

        // Activate Button listeners
        activateListeners();

        // Enable Tracker highlight settings
        //highlighter = (ImageView) v.findViewById(R.id.highlighter);

        // Adjust shift value according to line height
        shift = (int) (translation.getLineHeight());

        // Update color of highlighter according to user preference
        int defaultHighlight = R.color.highlightOrange;
        highlightColor = sharedPrefs.getInt(getString(R.string.highlight_color_pref_key), defaultHighlight);
        //highlighter.setBackgroundColor(context.getResources().getColor(selectedHighlight));

        // Word Selection Settings
        trackWordSelection();       // Tracks word selection (highlights word when selected)

        // Create word popup
        wordPopup = new WordPopup(v, getActivity().getApplicationContext());



        v.findViewById(R.id.text_translation).setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                tapX = event.getX();
                tapY = event.getY();

                return false;
            }
        });


        return v;


    }

    /*-------------------------------- FUNCTIONS --------------------------------*/




    // Activates all Button listeners
    public void activateListeners() {

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
                if (!currentlyPlaying) {
                    currentlyPlaying = true;
                    speakOut(translation);
                } else {
                    currentlyPlaying = false;
                    onDestroy();
                }
            }
        });

        translation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordPopup.close();
            }
        });

//        translation.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
//
//            @Override
//            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//                // Remove the "select all" option
//                menu.removeItem(android.R.id.selectAll);
//                // Remove the "cut" option
//                menu.removeItem(android.R.id.cut);
//                // Remove the "copy all" option
//                //menu.removeItem(android.R.id.copy);
//                return true;
//            }
//
//            @Override
//            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//                // Called when action mode is first created
//
//                // Here is an example MenuItem
//                menu.add(0, 0, 0, "Definition").setIcon(R.drawable.color_circle);
//                return true;
//            }
//
//            @Override
//            public void onDestroyActionMode(ActionMode mode) {
//            }
//
//            @Override
//            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//                switch (item.getItemId()) {
//                    case 0:
//                        int min = 0;
//                        int max = translation.getText().length();
//                        if (translation.isFocused()) {
//                            final int selStart = translation.getSelectionStart();
//                            final int selEnd = translation.getSelectionEnd();
//
//                            min = Math.max(0, Math.min(selStart, selEnd));
//                            max = Math.max(0, Math.max(selStart, selEnd));
//                        }
//                        // Perform your definition lookup with the selected text
//                        final CharSequence selectedText = translation.getText().subSequence(min, max);
//                        Log.d("tapped on:", selectedText.toString());
//                        Toast.makeText(v.getContext(), selectedText, Toast.LENGTH_SHORT)
//                                .show();
//                        createPopup(selectedText.toString());
//                        // Finish and close the ActionMode
//                        mode.finish();
//                        return true;
//                    default:
//                        break;
//                }
//                return false;
//            }
//
//        });

        // 4. Save text file
        saveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideUp(folderContainer);
            }
        });

        // 5. Select Folder to save
        folderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                promptTitleName(folderNameList.get(position));
            }
        });

        // 6. Exit saving text
        exitFolderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideDown(folderContainer);
            }
        });
        shadowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideDown(folderContainer);
            }
        });

        // 7. Add new folder when saving text
        addFolderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptFolderName();
            }
        });

        // 8. Back to previous page
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        // 9. Delete text file
        deleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeleteFile(folderName, fileName);
            }
        });
    }


    // Controls Tracker -- valid parameter is either "up" or "down"
    public void tracker(String dir) {

        if (dir.equals("up")) {             // Move Up
            if (currTracker > 0) {
                currTracker -= 1;   // Decrement tracker line
                highlightLine();
            } else {
                Toast.makeText(v.getContext(), "Woops! Cannot track upwards", Toast.LENGTH_SHORT)
                        .show();
            }

        } else if (dir.equals("down")) {    // Move Down
            if (currTracker < numOfLines - 1) {
                currTracker += 1;   // Increment tracker line
                highlightLine();
            } else {
                Toast.makeText(v.getContext(), "Woops! Cannot track downwards", Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }

    public void createPopup(String word) {


        Toast.makeText(context, word, Toast.LENGTH_SHORT)
                .show();

        TextView parentTextView = (TextView) translation;

        Rect parentTextViewRect = new Rect();

        // Initialize values for the computing of clickedText position
        SpannableString completeText = (SpannableString)(parentTextView).getText();
        Layout textViewLayout = parentTextView.getLayout();

        double startOffsetOfClickedText = completeText.getSpanStart(this);
        double endOffsetOfClickedText = completeText.getSpanEnd(this);
        double startXCoordinatesOfClickedText = textViewLayout.getPrimaryHorizontal((int)startOffsetOfClickedText);
        double endXCoordinatesOfClickedText = textViewLayout.getPrimaryHorizontal((int)endOffsetOfClickedText);


        // Get the rectangle of the clicked text
        int currentLineStartOffset = textViewLayout.getLineForOffset((int)startOffsetOfClickedText);
        int currentLineEndOffset = textViewLayout.getLineForOffset((int)endOffsetOfClickedText);
        boolean keywordIsInMultiLine = currentLineStartOffset != currentLineEndOffset;
        textViewLayout.getLineBounds(currentLineStartOffset, parentTextViewRect);


        // Update the rectangle position to his real position on screen
        int[] parentTextViewLocation = {0,0};
        parentTextView.getLocationOnScreen(parentTextViewLocation);

        double parentTextViewTopAndBottomOffset = (
                parentTextViewLocation[1] -
                        parentTextView.getScrollY() +
                        parentTextView.getCompoundPaddingTop()
        );
        parentTextViewRect.top += parentTextViewTopAndBottomOffset;
        parentTextViewRect.bottom += parentTextViewTopAndBottomOffset;

        parentTextViewRect.left += (
                parentTextViewLocation[0] +
                        startXCoordinatesOfClickedText +
                        parentTextView.getCompoundPaddingLeft() -
                        parentTextView.getScrollX()
        );
        parentTextViewRect.right = (int) (
                parentTextViewRect.left +
                        endXCoordinatesOfClickedText -
                        startXCoordinatesOfClickedText
        );

        int x = (parentTextViewRect.left + parentTextViewRect.right) / 2;
        int y = parentTextViewRect.bottom;
        if (keywordIsInMultiLine) {
            x = parentTextViewRect.left;
        }


        /*if (Math.abs((tapY - (y - 430))) < 200 || Math.abs((tapY - (y - 430))) > 275) {
            y = -500;
        }*/



        if (Math.abs((tapY - (y - 430))) < 200 || Math.abs((tapY - (y - 430))) > 275) {
            y += 500;
        }

        wordPopup.loadWord(word, x, y - 430);
    }


    // Function grabs current line and uses Spannable to set the color appropriately
    public void highlightLine() {
        Layout layout = translation.getLayout();
        String text = translation.getText().toString();

        // Set start and end points of the line
        int start, end;

        if (currTracker == 0) {
            start = 0;
        } else {
            start = layout.getLineEnd(currTracker - 1);
        }

        end = layout.getLineEnd(currTracker);


        Toast.makeText(v.getContext(), start + " " + end, Toast.LENGTH_SHORT)
                .show();

        CharSequence line = translation.getText().subSequence(0, text.length());
        Spannable translateText = new SpannableString(line);

        translateText.setSpan(new BackgroundColorSpan(context.getResources().getColor(R.color.defaultBackground)),
                0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );

        translateText.setSpan(new BackgroundColorSpan(context.getResources().getColor(highlightColor)),
                start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );

        trackWordSelection();


        translation.setText(translateText);

    }




    /*-------------------- TEXT-TO-SPEECH --------------------*/


    // Takes in a TextView and converts it to audio
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

    // Destroys TTS
    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


    /*-------------------- WORD SELECTION --------------------*/

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

                TextView parentTextView = (TextView) widget;

                Rect parentTextViewRect = new Rect();

                // Initialize values for the computing of clickedText position
                SpannableString completeText = (SpannableString)(parentTextView).getText();
                Layout textViewLayout = parentTextView.getLayout();

                double startOffsetOfClickedText = completeText.getSpanStart(this);
                double endOffsetOfClickedText = completeText.getSpanEnd(this);
                double startXCoordinatesOfClickedText = textViewLayout.getPrimaryHorizontal((int)startOffsetOfClickedText);
                double endXCoordinatesOfClickedText = textViewLayout.getPrimaryHorizontal((int)endOffsetOfClickedText);


                // Get the rectangle of the clicked text
                int currentLineStartOffset = textViewLayout.getLineForOffset((int)startOffsetOfClickedText);
                int currentLineEndOffset = textViewLayout.getLineForOffset((int)endOffsetOfClickedText);
                boolean keywordIsInMultiLine = currentLineStartOffset != currentLineEndOffset;
                textViewLayout.getLineBounds(currentLineStartOffset, parentTextViewRect);


                // Update the rectangle position to his real position on screen
                int[] parentTextViewLocation = {0,0};
                parentTextView.getLocationOnScreen(parentTextViewLocation);

                double parentTextViewTopAndBottomOffset = (
                        parentTextViewLocation[1] -
                                parentTextView.getScrollY() +
                                parentTextView.getCompoundPaddingTop()
                );
                parentTextViewRect.top += parentTextViewTopAndBottomOffset;
                parentTextViewRect.bottom += parentTextViewTopAndBottomOffset;

                parentTextViewRect.left += (
                        parentTextViewLocation[0] +
                                startXCoordinatesOfClickedText +
                                parentTextView.getCompoundPaddingLeft() -
                                parentTextView.getScrollX()
                );
                parentTextViewRect.right = (int) (
                        parentTextViewRect.left +
                                endXCoordinatesOfClickedText -
                                startXCoordinatesOfClickedText
                );

                int x = (parentTextViewRect.left + parentTextViewRect.right) / 2;
                int y = parentTextViewRect.bottom;
                if (keywordIsInMultiLine) {
                    x = parentTextViewRect.left;
                }

                if (Math.abs((tapY - (y - 430))) < 200 || Math.abs((tapY - (y - 430))) > 275) {
                    y = -500;
                }

                wordPopup.loadWord(selectedWord, x, tapY + 2 * Math.max(dpToPx(selectedTextSize), dpToPx(40)) );

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setTypeface(Typeface.createFromAsset(context.getAssets(),  "fonts/OpenDyslexic-Bold.otf"));
                ds.setUnderlineText(false); // set to false to remove underline
                ds.setColor(getResources().getColor(selectedTextColor));
            }

        };
    }


    /*-------------------- SAVING FILES --------------------*/

    // slide the view from below itself to the current position
    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        shadowView.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(300);
        animate.setFillAfter(false);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view){
        shadowView.setVisibility(View.GONE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(300);
        animate.setFillAfter(false);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    // Build dialog to ask for title name
    public void promptTitleName(final String folderName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Title name");
        builder.setMessage("Please name your new document");

        // Set up the input
        final EditText inputText = new EditText(context);
        // Specify the type of input expected
        inputText.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(inputText);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = inputText.getText().toString();
                saveFile(context, folderName, input, title);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Save file to the folder name
    private void saveFile(Context ctx, String folderName, String data, String title) {
        File folder = FileListFragment.getFolder(ctx, folderName);
        File file = new File(folder, title);
        String fileName = title;
        data = title + "\n" + data;
        int i = 0;
        while (file.exists()) {
            fileName = fileName + "_" + i;
            file = new File(folder, fileName);
            i++;
        }

        FileOutputStream outstream;

        try{
            if(!file.exists()){
                file.createNewFile();
            }
            outstream = new FileOutputStream(file);
            outstream.write(data.getBytes());
            outstream.close();

        }catch(IOException e){
            e.printStackTrace();
        }

        slideDown(folderContainer);
        // update folder list
        folderListAdapter.notifyDataSetChanged();

        // set title
        this.title = title;
        translationTitle.setText(title);

        // change save icon to delete icon
        newText = false;
        saveText.setVisibility(View.GONE);
        deleteFile.setVisibility(View.VISIBLE);

        // update folder name and file name
        this.folderName = folderName;
        this.fileName = fileName;

        Toast.makeText(getActivity().getApplicationContext(), "Saved file " + title + " in " + folderName,
                Toast.LENGTH_SHORT).show();
    }

    // Build dialog to ask for new folder name
    public void promptFolderName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Folder name");

        // Set up the input
        final EditText input = new EditText(context);
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String folderName = input.getText().toString();
                createFolderInTranslate(folderName);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void createFolderInTranslate(String folderName) {
        File file = GalleryFragment.createFolder(context, folderName);
        if (file != null) {
            folderNameList.add(folderNameList.size(),folderName);
            folderList.add(folderList.size(),file);
            folderListAdapter.notifyDataSetChanged();

            Toast.makeText(getActivity().getApplicationContext(), "Created folder " + folderName,
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Unable to create folder " + folderName,
                    Toast.LENGTH_SHORT).show();
        }
    }

    // DELETE FILE
    // Build dialog to confirm file delete
    public void confirmDeleteFile(final String folderName, final String fileName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete File");
        builder.setMessage("Are you sure you want to delete this file?");

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (deleteTextFile(folderName, fileName)) {
                    Toast.makeText(context, "Deleted file " + fileName,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Unable to delete file " + fileName,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    // Delete file
    public boolean deleteTextFile(String folderName, String fileName) {
        if (folderName.equals("") || fileName.equals("")) return false;
        File galleryFolder = GalleryFragment.getGalleryFolder(context);
        File folder = new File(galleryFolder, folderName);
        if (!folder.exists()) return false;
        File file = new File(folder, fileName);
        if (file.exists() && file.delete()) {
            getFragmentManager().popBackStack();
            getFragmentManager().popBackStack();
            return true;
        }
        return false;
    }

    /*-------------------- HELPERS --------------------*/

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

}