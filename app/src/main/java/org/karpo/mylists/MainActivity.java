package org.karpo.mylists;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    // initializing the app GUI variables from the code side
    private Button mainMenuGamesBtn, mainMenuMoviesBtn, mainMenuSeriesBtn, mainMenuExitBtn, mainMenuLoginBtn, mainMenuAppInformationBtn;
    private final LoginActivity mLoginContext = new LoginActivity();
//    private final ArrayList<Button> listOfMainMenuButtons = new ArrayList<>();

    //TODO: whats left to do in this app:
    // *change the openDialog func parameters so that only the first parameter is a MUST and others are optional - check online how you do that - meh
    // *write a code that solves the problem where you have a certain series and you want them to be in the order of release, and not alphabetically
    // *make the app also compatible with adding hebrew items
    // *sometimes when you tried to edit an item it didn't edit it, if it keeps happening check why its happening
    // *maybe just maybe also add another button that is "About" that will explain a bit about the features of the application for people who never used it before
    // and has no clue what it does or what extra features it might have
    // *it's important that all the data you'll have on this app will be backed up otherwise whats the point, you know?.. so make sure to take care of that
    // before you start using the app for realsies
    // *maybe add dates of things like how long you've been playing Guitar and drums or when you've started to code with python and stuff like that
    // and it will also update with the years and always write how much time passed since you started
    // example: You've been drumming for 6 years. (and when another year will pass it will show you you've been drumming for 7 years) and stuff like that
    // could be very helpful when updating your resume and such
    // *idea on how to compact the code a bit, you can maybe put all of the button strings into string arrays and then put a for loop on the string arrays
    // that will go through them and give them their id, making the code a bit more 'automatic' and shorter, though make sure you have a proper way to do this
    // before you try doing it, you can back up the project incase something goes wrong or something idk
    // *check where is the database file located at, you need it in order to backup all of the info online or on your computer.
    // *consider removing the clearAll button since it probably wont be such a good idea to have on the final version - or instead of removing it
    // Dor suggested you'd need to write 'delete' in order to clear the list - could be nice, also make it delete all of the series, and notice the user
    // about it before doing it.
    // *leave the birthday stuff for a different app
    // *maybe add Birthdays dates list section, and make the app remind you when it's somebody's birthday
    // *https://www.youtube.com/watch?v=WYY6qIUUOAw watch this vid for backing up data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();

        // todo: fix this crap
        //  seems to be working now but you had to copy and paste the whole function which is bad, for some reason it didnt work when using the context
        //  also the login button's text on the main menu doesn't change quite correctly yet
        // if textFromFile Contains 'Online: ' make isLoggedIn True, else make isLoggedIn False
        String textFromLoggedInFile = loadLoggedInFile();
        mLoginContext.isLoggedIn = textFromLoggedInFile.contains(getResources().getString(R.string.online));

        // if the user is logged in then show the 'Online: username' on the button, else the text will stay 'Login'
        if (mLoginContext.isLoggedIn) {
            mainMenuLoginBtn.setText(textFromLoggedInFile);
        }
//        else {
//            mainMenuLoginBtn.setText(getResources().getString(R.string.login));
//        }

        // when pressing buttons on main menu it will switch to another activity accordingly
        switchActivity(mainMenuGamesBtn, new Intent(MainActivity.this, GamesAndMoviesAndShowsActivity.class));
        switchActivity(mainMenuMoviesBtn, new Intent(MainActivity.this, GamesAndMoviesAndShowsActivity.class));
        switchActivity(mainMenuSeriesBtn, new Intent(MainActivity.this, GamesAndMoviesAndShowsActivity.class));

        switchActivity(mainMenuLoginBtn, new Intent(MainActivity.this, LoginActivity.class));

        // when pressing the App Information button
        mainMenuAppInformationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo: write functionality
            }
        });

        // quitting the application if the EXIT button is pressed.
        mainMenuExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });
    }

    /**
     * Syncing between the app GUI variables such as Buttons, TextViews etc, with the code variables.
     */
    private void initData() {
        mainMenuGamesBtn = findViewById(R.id.mainMenuGamesBtn);
        mainMenuMoviesBtn = findViewById(R.id.mainMenuMoviesBtn);
        mainMenuSeriesBtn = findViewById(R.id.mainMenuShowsBtn);
        mainMenuLoginBtn = findViewById(R.id.mainMenuLoginBtn);
        mainMenuAppInformationBtn = findViewById(R.id.mainMenuAppInformationBtn);
        mainMenuExitBtn = findViewById(R.id.mainMenuExitButton);
    }

    /**
     * The function accepts a Button and an Intent.
     * According to the intent and button it will redirect the user to a different activity
     */
    private void switchActivity(Button btn, Intent intent) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra(getResources().getString(R.string.activity_string_name), btn.getText().toString());
                startActivity(intent);
            }
        });
    }

    /**
     * this function will replace all of the "_" with " " and all of the "$" with "'"
     */
    public static String replaceCharsInStringDisplayReady(String string) {
        if (string.contains("_")) {
            string = string.replace("_", " ");
        }

        if (string.contains("$")) {
            string = string.replace("$", "'");
        }

        return string;
    }

    /**
     * this function will replace all of the " " with "_" and all of the "'" with "$"
     */
    public static String replaceCharsInStringDatabaseReady(String string) {
        if (string.contains(" ")) {
            string = string.replace(" ", "_");
        }
        if (string.contains("'")) {
            string = string.replace("'", "$");
        }
        return string;
    }

    /**
     * loading the logged_in file data from the phone
     */
    public String loadLoggedInFile() {
        FileInputStream fis = null;
        String textFromFile = "";

        try {
            fis = openFileInput(mLoginContext.IS_LOGGED_IN_FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text); // .append("\n");
            }

            textFromFile = sb.toString();
            // if textFromFile Contains 'Online: ' make isLoggedIn True, else make isLoggedIn False
            mLoginContext.isLoggedIn = textFromFile.contains(getResources().getString(R.string.online));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

            // closing the fileInputStream
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return textFromFile;
    }
}