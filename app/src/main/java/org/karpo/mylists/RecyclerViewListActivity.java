package org.karpo.mylists;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RecyclerViewListActivity extends AppCompatActivity implements DialogPopUp.DialogPopUpListener {
    private final ArrayList<String> gamesFinishedList = new ArrayList<>(), gamesNeedToPlayList = new ArrayList<>(), gamesNeedToPlayAgainList = new ArrayList<>(), favoriteGamesList = new ArrayList<>(), gamesSuggestionsList = new ArrayList<>(),
            moviesWatchedList = new ArrayList<>(), moviesNeedToWatchList = new ArrayList<>(), moviesNeedToRewatchList = new ArrayList<>(), favoriteMoviesList = new ArrayList<>(), moviesSuggestionsList = new ArrayList<>(),
            showsFinishedList = new ArrayList<>(), showsNeedToWatchList = new ArrayList<>(), showsNeedToRewatchList = new ArrayList<>(), favoriteShowsList = new ArrayList<>(), showsSuggestionsList = new ArrayList<>(), showsAreNotOverYetList = new ArrayList<>();

    public static ArrayList<String> gamesMenuButtonStrings = GamesAndMoviesAndShowsActivity.gamesMenuButtonStrings;
    public static ArrayList<String> moviesMenuButtonStrings = GamesAndMoviesAndShowsActivity.moviesMenuButtonStrings;
    public static ArrayList<String> showsMenuButtonStrings = GamesAndMoviesAndShowsActivity.showsMenuButtonStrings;

    private final ArrayList<String> gamesAndMoviesAndShowsMenuButtonStrings = new ArrayList<>();

    private final ArrayList<ArrayList<String>> listOfAllGamesLists = new ArrayList<>();
    private final ArrayList<ArrayList<String>> listOfAllMoviesLists = new ArrayList<>();
    private final ArrayList<ArrayList<String>> listOfAllShowsLists = new ArrayList<>();

    private final Map<String, ArrayList<String>> dictOfAllLists = new HashMap<>();

    public final ArrayList<String> seriesList = new ArrayList<>();
    public final ArrayList<String> seriesEditItemList = new ArrayList<>();

    private MyRecyclerViewAdapter myRecyclerViewAdapter;

    private String curListName;
    private ImageView backArrowIcon, addItemIcon, addSeriesIcon, clearAllIcon;
    private TextView activityTitle;

    private final char[] alphabet = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};

    // db initializing stuff
    private static final String TAG = "RecyclerViewListActivity";
    private DatabaseHelperLists mDatabaseHelperLists;
    private DatabaseHelperSeries mDatabaseHelperSeries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_list);

        // initializing everything
        initDataAndInitRecViewAdapter();

        // taking info from the previous activity's intent in order to find out the name of the button pressed and changing the label accordingly
        activityTitle.setText(MainActivity.replaceCharsInStringDisplayReady(curListName));

        // making the Action Bar variables visible
        backArrowIcon.setVisibility(View.VISIBLE);
        addItemIcon.setVisibility(View.VISIBLE);
        addSeriesIcon.setVisibility(View.VISIBLE);
        clearAllIcon.setVisibility(View.VISIBLE);

        // if the user presses the back_arrow button it executes the onBackPressed() func
        backArrowIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // if the user presses the add_button icon
        addItemIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(getResources().getString(R.string.add_item_dialog), null, 0);
            }
        });

        // if the user pressed the add_series button
        addSeriesIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog(getResources().getString(R.string.add_series_dialog), null, 0);
            }
        });

        // if the user presses the clear_all button
        clearAllIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog(getResources().getString(R.string.clear_all_dialog), null, 0);
            }
        });
    }

    /**
     * Displaying a dialog according to the parameters received.
     * @param whichDialog - will determine which dialog should be displayed.
     * @param curItemInList - with this we will know which item is currently selected, if there is one.
     * @param position - with this we will know what is the position of the item we're currently selecting, if there's an item selected.
     */
    public void openDialog(String whichDialog, String curItemInList, int position) {
        DialogPopUp dialogPopUp = new DialogPopUp(this, whichDialog, curItemInList, position);
        dialogPopUp.show(getSupportFragmentManager(), "dialog pop up");
    }

    /**
     *
     * @param itemToAdd is the item the user is inputting to add to the list
     */
    @Override
    public void addItem(String itemToAdd) {
        // making sure the itemToAdd var equals to something before proceeding to add it to the list
        if (!itemToAdd.equals("")) {
            // if its multiple lines string then split the lines and add the strings separately
            if (itemToAdd.contains("\n")) {
                String[] itemsToAdd = itemToAdd.split("\n");

                for (String s : itemsToAdd) {
                    // adding Leading Alphabet characters to the list of items if it isn't already there
                    addLeadingAlphabetCharactersAccordingToString(s);

                    // adding multiple items to the database through the for loops
                    addDataToListsDB(s, getResources().getString(R.string.none), 0);
                }
                // toast message: Added multiple items
                Toast.makeText(RecyclerViewListActivity.this, "Added multiple items", Toast.LENGTH_SHORT).show();

            } else {
                // adding Leading Alphabet characters to the list of items if it isn't already there
                addLeadingAlphabetCharactersAccordingToString(itemToAdd);

                // adding a *single* item to the database
                if (addDataToListsDB(itemToAdd, getResources().getString(R.string.none), 0)) {
                    // toast message "item added to cur_list"
                    Toast.makeText(RecyclerViewListActivity.this,"'" +  MainActivity.replaceCharsInStringDisplayReady(itemToAdd) + "' Added to " + MainActivity.replaceCharsInStringDisplayReady(curListName), Toast.LENGTH_SHORT).show();

                    // adding the item to the list
                    Objects.requireNonNull(dictOfAllLists.get(curListName)).add(itemToAdd);
                } else {
                    // toast message "This is already in the list!"
                    Toast.makeText(RecyclerViewListActivity.this, "This is already in the list!", Toast.LENGTH_SHORT).show();
                }
            }

            // todo: if you want that it will be less heavy executions what you can do is instead of
            //  deleting the list, just add the item and then open the activity again
            //  (you'll need to delete the activity that comes beforehand that is no longer needed)
            // deleting the list entirely and creating it anew with the correct order ,adding the item to the list alphabetically
            Objects.requireNonNull(dictOfAllLists.get(curListName)).clear();
            Objects.requireNonNull(dictOfAllLists.get(curListName)).addAll(getDataFromListsDB(curListName));

            //todo: try to check this way of updating the list, with going to the same activity but deleting the activity that is present
            // so that there will be no Duplicate activities
//            Intent intent = new Intent(RecyclerViewListActivity.this, RecyclerViewListActivity.class);
//            startActivity(intent);
//            finish();

        // refreshing the adapter
        myRecyclerViewAdapter.notifyDataSetChanged();

        } else {
            // toast message if the name field is empty
            Toast.makeText(RecyclerViewListActivity.this, "The field is empty! Try again.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param seriesToAdd - the series it will add to the seriesDB and series list
     */
    @Override
    public void addSeries(String seriesToAdd) {
        // adding the series to the series List, seriesEditItemList and to the seriesDB
        seriesList.add(seriesToAdd);
        seriesEditItemList.add(seriesToAdd);
        addDataToSeriesDB(seriesToAdd);
    }

    /**
     * @param whichDialog - tells the program which dialog should be executed
     * @param curSeriesInList - sending the current series selected
     */
    @Override
    public void areYouSureYouWantToDelete(String whichDialog, String curSeriesInList) {
        // makes sure the item selected IS NOT 'Select A Series'
        if (curSeriesInList.equals(getResources().getString(R.string.series_list_placeholder))) {
            Toast.makeText(RecyclerViewListActivity.this, "You can't do that", Toast.LENGTH_SHORT).show();
        // if it isn't then delete the series
        } else {
            openDialog(whichDialog, curSeriesInList, 0);
        }
    }

    /**
     * deleting all of the data from the selected table
     */
    @Override
    public void listToClear() {
        // deleting all of the data from the table in the database
        deleteAllDataFromListsDBTable(curListName);

        // clearing the list and then
        Objects.requireNonNull(dictOfAllLists.get(curListName)).clear();

        // refreshing the adapter
        myRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * editing the selected item
     * @param position is the position of the item on the list
     */
    @Override
    public void editItem(String curItemInList, String newItemValue, int position) {
        // deleting item from the list
        Objects.requireNonNull(dictOfAllLists.get(curListName)).remove(curItemInList);

        // adding Leading Alphabet characters to the list of items if it isn't already there
        addLeadingAlphabetCharactersAccordingToString(newItemValue);

        // editing the item in the DB
        editItemFromListsDBTable(curListName, curItemInList, newItemValue);

        // adding the item to the list
        Objects.requireNonNull(dictOfAllLists.get(curListName)).add(newItemValue);

//        // deleting the list entirely and creating it anew with the correct order ,adding the item to the list alphabetically
//        Objects.requireNonNull(dictOfAllLists.get(curListName)).clear();
//        Objects.requireNonNull(dictOfAllLists.get(curListName)).addAll(getDataFromListsDB(curListName));
//
////        // Toast Message
////        Toast.makeText(RecyclerViewListActivity.this, "Item edited", Toast.LENGTH_SHORT).show();
//
//        // refreshing the adapter
//        myRecyclerViewAdapter.notifyDataSetChanged();
        refreshItemsInList();
    }

    /**
     * applies the items series info
     * @param curItemInList - the item
     * @param seriesItBelongsTo - the series the item belongs to
     * @param seriesIndex - the item's index in the series
     */
    @Override
    public void applyItemsSeriesInfoInDB(String curItemInList, String seriesItBelongsTo, int seriesIndex) {
        mDatabaseHelperLists.editItemsSeriesInfo(curListName, curItemInList, seriesItBelongsTo, seriesIndex);
    }

    /**
     * deleting the selected item.
     */
    @Override
    public void deleteItem(String itemToDelete, int position) {
        // deleting item from the database
        deleteItemFromListsDBTable(itemToDelete);

        // deleting item from the list
        Objects.requireNonNull(dictOfAllLists.get(curListName)).remove(itemToDelete);

        // toast message
        Toast.makeText(RecyclerViewListActivity.this, MainActivity.replaceCharsInStringDisplayReady(itemToDelete) + " deleted", Toast.LENGTH_SHORT).show();

        // notifying the adapter an item was removed
        myRecyclerViewAdapter.notifyItemRemoved(position);

        // refreshing the adapter
        myRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * this func deletes the series given from the DB and from seriesList & seriesEditItemList
     * @param seriesToDelete - the series we wish to delete
     */
    @Override
    public void deleteSeries(String seriesToDelete) {
        // toast message 'itemToDelete' deleted
        Toast.makeText(RecyclerViewListActivity.this, "'" + seriesToDelete + "' deleted", Toast.LENGTH_SHORT).show();
        // deletes the series from the series DB and from seriesList & seriesEditItemList
        deleteItemFromSeriesDBTable(seriesToDelete);
        seriesList.remove(seriesToDelete);
        seriesEditItemList.remove(seriesToDelete);
    }

    /**
     *
     * @param curItemInList - the item currently selected
     * @return the series the item belongs to and its index in the series
     */
    @Override
    public ArrayList<String> getItemSeriesInfo(String curItemInList) {
        Cursor data = mDatabaseHelperLists.getData(curListName);
        ArrayList<String> dataToReturn = new ArrayList<>();

        while (data.moveToNext()) {
            // searches for the item in the DB, if it finds it then set 'seriesItBelongsTo' & 'seriesIndex' values accordingly, and then return them in an arraylist
            if (data.getString(1).equals(MainActivity.replaceCharsInStringDatabaseReady(curItemInList))) {
                String seriesItBelongsTo = MainActivity.replaceCharsInStringDisplayReady(data.getString(2));
                int seriesIndex = data.getInt(3);
                dataToReturn.add(seriesItBelongsTo);
                dataToReturn.add(Integer.toString(seriesIndex));
                return dataToReturn;
            }
        }
        // if it didn't find anything then return 'None' and 0 as the function's elements
        dataToReturn.add(getResources().getString(R.string.none));
        dataToReturn.add("0");
        return dataToReturn;
    }

    /**
     *
     * @param selectedSeries - the series currently selected
     * @return an int value of how much items there are in the item's series
     */
    @Override
    public int howManyItemsInSeries(String selectedSeries) {
        int howManyInSelectedSeries = 0;
        Cursor data = mDatabaseHelperLists.getData(curListName);

        // going through all the items in the DB and checking which belong to 'selectedSeries', each time it finds one add +1 to 'howMany'
        while (data.moveToNext()) {
            if (data.getString(2).equals(MainActivity.replaceCharsInStringDatabaseReady(selectedSeries))) {
                howManyInSelectedSeries++;
            }
        }
        // if there's no items in the series then return 1
        if (howManyInSelectedSeries == 0) {
            return 1;
        }
        // else return how many items there are in the series
        return howManyInSelectedSeries + 1;
    }

    @Override
    public void refreshItemsInList() {
        // deleting the list entirely and creating it anew with the correct order ,adding the item to the list alphabetically
        Objects.requireNonNull(dictOfAllLists.get(curListName)).clear();
        Objects.requireNonNull(dictOfAllLists.get(curListName)).addAll(getDataFromListsDB(curListName));

        // refreshing the adapter
        myRecyclerViewAdapter.notifyDataSetChanged();
    }

    private boolean addDataToListsDB(String item, String seriesTheItemBelongsTo, int seriesIndex) {
        // todo: delete the code here if it isn't necessary, probably isn't
//        boolean insertData = mDatabaseHelperLists.addData(item, seriesTheItemBelongsTo, seriesIndex, curListName);
//
//        // it will pop up the toast message if the insertion of the item didn't succeed AND if it's first char isn't "\n"
//        if (!insertData && !Character.toString(item.charAt(0)).equals("\n")) {
//
//        }
//        return insertData;

        return mDatabaseHelperLists.addData(item, seriesTheItemBelongsTo, seriesIndex, curListName);
    }

    private boolean addDataToSeriesDB(String series) {
        // adding series to the seriesDB
        boolean insertData = mDatabaseHelperSeries.addData(series, curListName);

        if (!insertData) {
            Toast.makeText(RecyclerViewListActivity.this, "This is already in the list!", Toast.LENGTH_SHORT).show();
        }
        return insertData;
    }

    private ArrayList<String> getDataFromListsDB(String tableName) {
        // get the data and put it in a list
        Cursor data = mDatabaseHelperLists.getData(tableName);
        ArrayList<String> listOfData = new ArrayList<>();

        while (data.moveToNext()) {
            // get the value from the database in column 1, then add it to the arraylist
            String test = data.getString(1);
            listOfData.add(MainActivity.replaceCharsInStringDisplayReady(data.getString(1)));
        }
        return listOfData;
    }

    private ArrayList<String> getDataFromSeriesDB(String tableName) {
        // get the data and put it in a list
        Cursor data = mDatabaseHelperSeries.getData(tableName);
        ArrayList<String> listOfData = new ArrayList<>();

        while (data.moveToNext()) {
            // get the value from the database in column 1, then add it to the arraylist
            String test = data.getString(1);
            listOfData.add(MainActivity.replaceCharsInStringDisplayReady(data.getString(1)));
        }

        return listOfData;
    }

    // todo: move the code inside this function to the clearAll function if it stays only one line when the app is finished
    private void deleteAllDataFromListsDBTable(String tableName) {
        mDatabaseHelperLists.deleteAllData(tableName);
    }

    // todo: move the code inside this function to the deleteItem function if it stays only one line when the app is finished
    private void deleteItemFromListsDBTable(String itemToDelete) {
        mDatabaseHelperLists.deleteItem(curListName, itemToDelete);
    }

    private void deleteItemFromSeriesDBTable(String seriesToDelete) {
        mDatabaseHelperSeries.deleteItem(curListName, seriesToDelete);
    }

    //todo: move the code inside this function to the editItem function if it stays only one line when the app is finished
    // or delete if its not used
    private void editItemFromListsDBTable(String tableName, String itemToEdit, String newValue) {
        mDatabaseHelperLists.editItem(tableName, itemToEdit, newValue);
    }

    /**
     * @param stringToCheck It will add a Leading Character according to the stringToCheck value.
     */
    private void addLeadingAlphabetCharactersAccordingToString(String stringToCheck) {
        // checks if stringToCheck starts with an english character or something else
        char firstChar = stringToCheck.toUpperCase().charAt(0);
        // if IT IS a character then proceed to check it
        if (firstChar >= 'A' && firstChar <= 'Z') {
            // checking if the string starts with "the " and if it does it will remove "The " from the string
            if (stringToCheck.length() >= 4) {
                if (stringToCheck.substring(0, 4).equalsIgnoreCase("The ")) {
                    // deleting the first 4 characters of the updatedItemName var - ("the ")
                    stringToCheck = stringToCheck.substring(4);
                }
            }

            // a for loop that is looping through the alphabet letters
            for (char c : alphabet) {
                // if the first character of the item name equals to one of the alphabet letters add it as a Leading character, if not already added
                if (Character.toString(stringToCheck.charAt(0)).equalsIgnoreCase(Character.toString(c).toUpperCase())) {
                    // checks if the leading character already exists in the list
                    // makes sure the table in the DB isn't empty
                    if (getDataFromListsDB(curListName).size() > 0) {
                        // if the string starts with 'A'
                        if (Character.toString(c).equalsIgnoreCase("A")) {
                            // Adding 'A' to the list
                            addDataToListsDB(Character.toString(c).toUpperCase() + ":\n", "", 0);
                            // if the string DOES NOT start with 'A'
                        } else {
                            // if the string starts with a letter that IS NOT 'A'
                            addDataToListsDB("\n" + Character.toString(c).toUpperCase() + ":\n", "", 0);
                        }
                        // breaks the loop after it already added a leading character
                        break;

                    // if the table in the DB is empty:
                    } else {
                        // if stringToCheck starts with 'A'
                        if (Character.toString(c).equalsIgnoreCase("A")) {
                            addDataToListsDB(Character.toString(c).toUpperCase() + ":\n", "", 0);
                        // if stringToCheck DOES NOT start with 'A'
                        } else {
                            addDataToListsDB("\n" + Character.toString(c).toUpperCase() + ":\n", "", 0);
                        }
                    }
                }
                //todo: add the numbers and signs section, and make sure it starts AFTER the letters, not before, you might have to change something
                // in the database function for that so keep that in mind
                // if the first char of the string isn't an alphabet letter, it means starts with a number or a symbol
                // todo: in here you will also the hebrew leading section
            }
        // if it's NOT a letter
        } else {
            final String numsAndSymbolsLeadingCharacter = "\n1234-~#:\n";
            addDataToListsDB(numsAndSymbolsLeadingCharacter, "", 0);
        }
    }

    /**
     * Syncing between the app GUI variables such as Buttons, TextViews etc, with the code variables.
     * Initializing the RecyclerView's Adapter.
     */
    private void initDataAndInitRecViewAdapter() {
        // initializing gamesAndMoviesAndShowsMenuButtonStrings arraylist
        gamesAndMoviesAndShowsMenuButtonStrings.addAll(gamesMenuButtonStrings);
        gamesAndMoviesAndShowsMenuButtonStrings.addAll(moviesMenuButtonStrings);
        gamesAndMoviesAndShowsMenuButtonStrings.addAll(showsMenuButtonStrings);

        // initializing dbs
        mDatabaseHelperLists = new DatabaseHelperLists(this, gamesAndMoviesAndShowsMenuButtonStrings);
        mDatabaseHelperSeries = new DatabaseHelperSeries(this, gamesAndMoviesAndShowsMenuButtonStrings);

        // initializing the RecyclerView
        RecyclerView recyclerViewList = findViewById(R.id.recyclerViewList);

        // initializing the actionBar's icons and text
        backArrowIcon = findViewById(R.id.backArrowIcon);
        addItemIcon = findViewById(R.id.addItemIcon);
        addSeriesIcon = findViewById(R.id.addSeriesIcon);
        clearAllIcon = findViewById(R.id.clearAllIcon);

        // adding the 'Select Series' item that will be the default one - to the series List
        seriesList.add(getResources().getString(R.string.series_list_placeholder));

        // adding the 'None' item that will be the default one - to the editItem series
        seriesEditItemList.add(getResources().getString(R.string.series_edit_item_list_placeholder));

        // initializing the activity's Title var
        activityTitle = findViewById(R.id.activityTitle);

        // getting curListName's info
        curListName = MainActivity.replaceCharsInStringDatabaseReady(getIntent().getStringExtra(getResources().getString(R.string.activity_string_name)));

        // adding all games lists to the listOfAllGamesLists variable
        Collections.addAll(listOfAllGamesLists, gamesFinishedList, gamesNeedToPlayList, gamesNeedToPlayAgainList, favoriteGamesList, gamesSuggestionsList);
        // adding all movies lists to the listOfAllMoviesLists variable
        Collections.addAll(listOfAllMoviesLists, moviesWatchedList, moviesNeedToWatchList, moviesNeedToRewatchList, favoriteMoviesList, moviesSuggestionsList);
        // adding all shows lists to the listOfAllShowsLists variable
        Collections.addAll(listOfAllShowsLists, showsFinishedList, showsNeedToWatchList, showsNeedToRewatchList, favoriteShowsList, showsSuggestionsList, showsAreNotOverYetList);

        // adding all the lists and their string names into the dictOfAllLists dictionary
        for (int l_index = 0; l_index < listOfAllGamesLists.size(); l_index++) {
            dictOfAllLists.put(gamesMenuButtonStrings.get(l_index), listOfAllGamesLists.get(l_index));
        }
        for (int l_index = 0; l_index < listOfAllMoviesLists.size(); l_index++) {
            dictOfAllLists.put(moviesMenuButtonStrings.get(l_index), listOfAllMoviesLists.get(l_index));
        }
        for (int l_index = 0; l_index < listOfAllShowsLists.size(); l_index++) {
            dictOfAllLists.put(showsMenuButtonStrings.get(l_index), listOfAllShowsLists.get(l_index));
        }

        // getting the items from the DB according to current list
        Objects.requireNonNull(dictOfAllLists.get(curListName)).addAll(getDataFromListsDB(curListName));

        // getting the series from the DB according to current list
        seriesList.addAll(getDataFromSeriesDB(curListName));

        // getting the series from the DB according to the current list
        seriesEditItemList.addAll(getDataFromSeriesDB(curListName));

        // checking which button was pressed in GamesAndMoviesAndShowsActivity
        String whichListToGoTo = MainActivity.replaceCharsInStringDatabaseReady(getIntent().getStringExtra(getResources().getString(R.string.activity_string_name)));

        // initializing my RecyclerView's Adapter
        myRecyclerViewAdapter = new MyRecyclerViewAdapter(this, dictOfAllLists, whichListToGoTo);
        recyclerViewList.setAdapter(myRecyclerViewAdapter);
        recyclerViewList.setLayoutManager(new LinearLayoutManager(this));
    }
}