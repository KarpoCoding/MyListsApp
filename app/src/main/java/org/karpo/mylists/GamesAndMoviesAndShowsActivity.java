package org.karpo.mylists;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GamesAndMoviesAndShowsActivity extends AppCompatActivity {
    // initializing the app GUI variables from the code side
    private Button gamesFinishedButton, gamesNeedToPlayButton, gamesNeedToPlayAgainButton, favoriteGamesButton, gamesSuggestionsButton, moviesWatchedButton, moviesNeedToWatchButton, moviesNeedToRewatchButton, favoriteMoviesButton, moviesSuggestionsButton, showsWatchedButton, showsNeedToWatchButton, showsNeedToRewatchButton, favoriteShowsButton, showsSuggestionsButton, showsNotOverYetButton;


    private final ArrayList<Button> gamesMenuButtons = new ArrayList<>();
    private final ArrayList<Button> moviesMenuButtons = new ArrayList<>();
    private final ArrayList<Button> showsMenuButtons = new ArrayList<>();

    private final ArrayList<Button> gamesAndMoviesAndShowsMenuButtons = new ArrayList<>();

    public static ArrayList<String> gamesMenuButtonStrings;
    public static ArrayList<String> moviesMenuButtonStrings;
    public static ArrayList<String> showsMenuButtonStrings;

    private ImageView backArrowIcon;
    private TextView activityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games_and_movies_and_shows);

        // initializing data
        initData();

        // messageFromActivity is the info we got from the Activity that transferred us here
        String messageFromActivity = getIntent().getStringExtra(getResources().getString(R.string.activity_string_name));

        // making the back_arrow visible and setting the actionBar title according the info we got from the previous activity
        backArrowIcon.setVisibility(View.VISIBLE);
        activityTitle.setText(MainActivity.replaceCharsInStringDisplayReady(messageFromActivity));

        // if the user presses the back_arrow button it executes the onBackPressed() func
        backArrowIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // getting the info from the intent var about which button was clicked in the main activity and changing current activity accordingly
        whichMenu(messageFromActivity);
    }

    /**
     * The function accepts a Button and an Intent.
     * According to the intent and button it will redirect the user to a different activity
     */
    private void switchActivity(ArrayList<Button> listOfCurMenuButtons, Intent intent) {
        for (Button b: listOfCurMenuButtons) {
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent.putExtra(getResources().getString(R.string.activity_string_name), b.getText().toString());
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * @param whichMenu - with this parameter the function will know which menu to show to the user.
     * also calling the switchActivity method according to the whichMenu parameter
     */
    private void whichMenu(String whichMenu) {
        // if the user picked the Games menu setting the text and visibility of the buttons accordingly.
        if (whichMenu.equals(getResources().getString(R.string.games))) {
            for (int i = 0; i < gamesMenuButtons.size(); i++) {
                gamesMenuButtons.get(i).setText(MainActivity.replaceCharsInStringDisplayReady(gamesMenuButtonStrings.get(i)));
                gamesMenuButtons.get(i).setVisibility(View.VISIBLE);
            }
            switchActivity(gamesMenuButtons, new Intent(GamesAndMoviesAndShowsActivity.this, RecyclerViewListActivity.class));
        }

        // if the user picked the Movies menu setting the text and visibility of the buttons accordingly.
        if (whichMenu.equals(getResources().getString(R.string.movies))) {
            for (int i = 0; i < moviesMenuButtons.size(); i++) {
                moviesMenuButtons.get(i).setText(MainActivity.replaceCharsInStringDisplayReady(moviesMenuButtonStrings.get(i)));
                moviesMenuButtons.get(i).setVisibility(View.VISIBLE);
            }
            switchActivity(moviesMenuButtons, new Intent(GamesAndMoviesAndShowsActivity.this, RecyclerViewListActivity.class));
        }

        // if the user picked the Shows menu setting the text and visibility of the buttons accordingly.
        if (whichMenu.equals(getResources().getString(R.string.shows))) {
            for (int i = 0; i < showsMenuButtons.size(); i++) {
                showsMenuButtons.get(i).setText(MainActivity.replaceCharsInStringDisplayReady(showsMenuButtonStrings.get(i)));
                showsMenuButtons.get(i).setVisibility(View.VISIBLE);
            }
            switchActivity(showsMenuButtons, new Intent(GamesAndMoviesAndShowsActivity.this, RecyclerViewListActivity.class));
        }
    }

    /**
     * Syncing between the app GUI variables such as Buttons, TextViews etc, with the code variables.
     * Creating a collection menu var from all the buttons named 'gamesAndMoviesAndShowsMenuButtons'
     */
    private void initData() {
        // initializing the games Menu buttons
        gamesFinishedButton = findViewById(R.id.gamesFinishedButton);
        gamesNeedToPlayButton = findViewById(R.id.gamesNeedToPlayButton);
        gamesNeedToPlayAgainButton = findViewById(R.id.gamesNeedToPlayAgainButton);
        favoriteGamesButton = findViewById(R.id.favoriteGamesButton);
        gamesSuggestionsButton = findViewById(R.id.gamesSuggestionsButton);

        // initializing the movies Menu buttons
        moviesWatchedButton = findViewById(R.id.moviesWatchedButton);
        moviesNeedToWatchButton = findViewById(R.id.moviesNeedToWatchButton);
        moviesNeedToRewatchButton = findViewById(R.id.moviesNeedToRewatchButton);
        favoriteMoviesButton = findViewById(R.id.favoriteMoviesButton);
        moviesSuggestionsButton = findViewById(R.id.moviesSuggestionsButton);

        // initializing the shows Menu buttons
        showsWatchedButton = findViewById(R.id.showsWatchedButton);
        showsNeedToWatchButton = findViewById(R.id.showsNeedToWatchButton);
        showsNeedToRewatchButton = findViewById(R.id.showsNeedToRewatchButton);
        favoriteShowsButton = findViewById(R.id.favoriteShowsButton);
        showsSuggestionsButton = findViewById(R.id.showsSuggestionsButton);
        showsNotOverYetButton = findViewById(R.id.showsNotOverYetButton);

        // adding all of the games menu buttons into the gamesMenuButtons list
        Collections.addAll(gamesMenuButtons, gamesFinishedButton, gamesNeedToPlayButton, gamesNeedToPlayAgainButton, favoriteGamesButton, gamesSuggestionsButton);
        // adding all of the movies menu buttons into the moviesMenuButtons list
        Collections.addAll(moviesMenuButtons, moviesWatchedButton, moviesNeedToWatchButton, moviesNeedToRewatchButton, favoriteMoviesButton, moviesSuggestionsButton);
        // adding all of the shows menu buttons into the showsMenuButtons list
        Collections.addAll(showsMenuButtons, showsWatchedButton, showsNeedToWatchButton, showsNeedToRewatchButton, favoriteShowsButton, showsSuggestionsButton, showsNotOverYetButton);

        // adding to gamesAndMoviesAndShowsMenuButtons all of the buttons
        gamesAndMoviesAndShowsMenuButtons.addAll(gamesMenuButtons);
        gamesAndMoviesAndShowsMenuButtons.addAll(moviesMenuButtons);
        gamesAndMoviesAndShowsMenuButtons.addAll(showsMenuButtons);

        // adding all of the menu buttons strings into a list
        gamesMenuButtonStrings = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.games_menu_button_strings)));
        moviesMenuButtonStrings = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.movies_menu_button_strings)));
        showsMenuButtonStrings = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.shows_menu_button_strings)));

        // initializing the actionBar's icons and text
        backArrowIcon = findViewById(R.id.backArrowIcon);
        activityTitle = findViewById(R.id.activityTitle);
    }
}