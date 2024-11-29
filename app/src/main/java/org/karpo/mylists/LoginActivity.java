package org.karpo.mylists;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private TextView usernameTakenWarning, usernameNotValid, usernameFieldIsEmpty, usernameDoesntExist, passwordFieldIsEmpty, passwordIsContainsSpaces, passwordIncorrect, statusLoggedIn;
    private EditText usernameEditTxt, passwordEditTxt;
    private Button loginButtonLoginMenu, logoutButtonLoginMenu, registerButtonLoginMenu;

    private DatabaseHelperUsers mDatabaseHelperUsers;

    // action bar
    private ImageView backArrowIcon;
    private TextView activityTitle;

    // todo: add the sharedPreferences thingy and save on it if the user is online or offline
    public final String IS_LOGGED_IN_FILE_NAME = "is_logged_in.txt";
    public boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initializing gui and other stuff
        init();

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

        // if the user presses the register button
        registerButtonLoginMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            register();
            }
        });

        // if the user presses the login button
        loginButtonLoginMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // if the user presses the logout button
        logoutButtonLoginMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoggedIn = false;
                logout();
            }
        });

        // if the user is logged in then show the logged in menu
        String textFromFile = loadLoggedInFile();
        if (textFromFile.contains(getResources().getString(R.string.online))) {
            isLoggedIn = true;
            loggedIn(textFromFile);
        }
    }

    /**
     * initializing all the gui of login activity & user db handler
     */
    private void init() {
        usernameTakenWarning = findViewById(R.id.usernameTakenWarning);
        usernameNotValid = findViewById(R.id.usernameNotValid);
        usernameFieldIsEmpty = findViewById(R.id.usernameFieldIsEmpty);
        usernameDoesntExist = findViewById(R.id.usernameDoesntExist);
        passwordFieldIsEmpty = findViewById(R.id.passwordFieldIsEmpty);
        passwordIsContainsSpaces = findViewById(R.id.passwordIsContainsSpaces);
        passwordIncorrect = findViewById(R.id.passwordIncorrect);

        usernameEditTxt = findViewById(R.id.usernameEditTxt);
        passwordEditTxt = findViewById(R.id.passwordEditTxt);

        loginButtonLoginMenu = findViewById(R.id.loginButtonLoginMenu);
        logoutButtonLoginMenu = findViewById(R.id.logoutButtonLoginMenu);
        registerButtonLoginMenu = findViewById(R.id.registerButtonLoginMenu);

        statusLoggedIn = findViewById(R.id.statusLoggedIn);

        backArrowIcon = findViewById(R.id.backArrowIcon);
        activityTitle = findViewById(R.id.activityTitle);

        mDatabaseHelperUsers = new DatabaseHelperUsers(this);
    }

    /**
     * trying to register a user to the users DB
     * @return true if user was created successfully, false if not
     */
    private boolean register() {
        // making the warning TextViews not visible
        makingWarningTextViewsNotVisible();

        String usernameString = usernameEditTxt.getText().toString();
        String passwordString = passwordEditTxt.getText().toString();

        // if the username contains any special characters return false
        if (checkForSpecialCharacters(usernameString)) {
            usernameNotValid.setVisibility(View.VISIBLE);
            return false;
        }

        // if the password contains a space return false
        if (passwordString.contains(" ")) {
            passwordIsContainsSpaces.setVisibility(View.VISIBLE);
        }

        // if the username field is empty return false
        if (usernameString.equals("")) {
            usernameFieldIsEmpty.setVisibility(View.VISIBLE);
            return false;
        }

        // if the password field is empty return false
        if (passwordString.equals("")) {
            passwordFieldIsEmpty.setVisibility(View.VISIBLE);
            return false;
        }

        // if the user doesn't exist add the user to the users db
        if (!mDatabaseHelperUsers.checkIfUserExists(usernameString)) {
            // adding the user to the users db
            mDatabaseHelperUsers.addNewUser(usernameString, passwordString);
            // toast message: 'username' created!
            Toast.makeText(LoginActivity.this, "'" + usernameEditTxt.getText().toString() + "' created!", Toast.LENGTH_SHORT).show();
            // clearing the login edit texts
            clearLoginEditTexts();
            return true;

        // if the user exists already then show the usernameTakenWarning
        } else {
            usernameTakenWarning.setVisibility(View.VISIBLE);
            return false;
        }
    }

    /**
     * @return true if the login was successful
     */
    @SuppressLint("SetTextI18n")
    private boolean login() {
        // making the warning TextViews not visible
        makingWarningTextViewsNotVisible();

        String usernameString = usernameEditTxt.getText().toString();
        String passwordString = passwordEditTxt.getText().toString();

        // if the username field is empty return false
        if (usernameString.equals("")) {
            usernameFieldIsEmpty.setVisibility(View.VISIBLE);
            return false;
        }

        // if the password field is empty return false
        if (passwordString.equals("")) {
            passwordFieldIsEmpty.setVisibility(View.VISIBLE);
            return false;
        }

        // creating a cursor data var containing the user's info
        Cursor data = mDatabaseHelperUsers.getUserInfo(usernameString);
        String hashedPassword = mDatabaseHelperUsers.hashString(passwordString);

        // true if the db contains anything
        if (data.moveToNext()) {
            // if the user doesn't exist then show the usernameDoesntExist textview and return false
            if (!data.getString(1).equals(usernameString)) {
                usernameDoesntExist.setVisibility(View.VISIBLE);
                return false;
            }

            // if the password matches the password that's in the db then login
            if (data.getString(2).equals(hashedPassword)) {
                Toast.makeText(LoginActivity.this, "'" + usernameString + "' logged in successfully!", Toast.LENGTH_SHORT).show();
                isLoggedIn = true;
                // saves to a file in the phone that the user is logged in
                saveLoggedInFile(getResources().getString(R.string.online) + usernameString);
                loggedIn(getResources().getString(R.string.online) + usernameString);
                return true;

            // if the password was incorrect
            } else {
                passwordIncorrect.setVisibility(View.VISIBLE);
                return false;
            }

        // if the database is empty return false
        } else {
            usernameDoesntExist.setVisibility(View.VISIBLE);
            return false;
        }
    }

    @SuppressLint("SetTextI18n")
    private void loggedIn(String textToSetInStatus) {
        statusLoggedIn.setText(textToSetInStatus);

        // making the username and password edit texts gone
        usernameEditTxt.setVisibility(View.GONE);
        passwordEditTxt.setVisibility(View.GONE);

        // making the logoutBtn visible and the loginBtn not visible and register button not visible
        registerButtonLoginMenu.setVisibility(View.GONE);
        loginButtonLoginMenu.setVisibility(View.GONE);
        logoutButtonLoginMenu.setVisibility(View.VISIBLE);
    }

    /**
     * logs out the user
     */
    private void logout() {
        // setting the action's bar text to 'Login'
        activityTitle.setText(getResources().getString(R.string.login));

        // making the username and password edit texts gone, and also the register button
        usernameEditTxt.setVisibility(View.VISIBLE);
        passwordEditTxt.setVisibility(View.VISIBLE);

        // making the logoutBtn visible and the loginBtn not visible
        registerButtonLoginMenu.setVisibility(View.VISIBLE);
        loginButtonLoginMenu.setVisibility(View.VISIBLE);
        logoutButtonLoginMenu.setVisibility(View.GONE);

        // setting the status's text to 'Status: Offline'
        statusLoggedIn.setText(getResources().getString(R.string.status_offline));

        // saving to the file on the phone 'Offline'
        saveLoggedInFile(getResources().getString(R.string.offline));
        // todo: check if it works
    }

    /**
     * clears the Login Activity's EditTexts
     */
    private void clearLoginEditTexts() {
        usernameEditTxt.setText("");
        passwordEditTxt.setText("");
    }

    /**
     * @param stringToCheck - the string that will be checked for special chars
     * @return - if true it means theres a special character in the string
     */
    private boolean checkForSpecialCharacters(String stringToCheck) {
        // returns true if string contains spaces
        if (stringToCheck.contains(" ")) {
            return true;
        }

        // code taken from stackoverflow to check for special characters
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(stringToCheck);
        return m.find();
    }

    /**
     * making the warning TextViews's setVisibility to GONE
     */
    private void makingWarningTextViewsNotVisible() {
        usernameNotValid.setVisibility(View.GONE);
        usernameFieldIsEmpty.setVisibility(View.GONE);
        usernameTakenWarning.setVisibility(View.GONE);
        usernameFieldIsEmpty.setVisibility(View.GONE);
        usernameDoesntExist.setVisibility(View.GONE);

        passwordFieldIsEmpty.setVisibility(View.GONE);
        passwordIsContainsSpaces.setVisibility(View.GONE);
        passwordFieldIsEmpty.setVisibility(View.GONE);
        passwordIncorrect.setVisibility(View.GONE);
    }

    /**
     * saves to phone data that the user is logged in
     */
    public void saveLoggedInFile(String textToSave) {
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(IS_LOGGED_IN_FILE_NAME, MODE_PRIVATE);

            // saves the text 'Online: ' + username into the phone directory using the logged_in_file_name
            fos.write(textToSave.getBytes());
            Toast.makeText(LoginActivity.this, "Saved to " + getFilesDir() + "/" + IS_LOGGED_IN_FILE_NAME, Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * loading the logged_in file data from the phone
     */
    public String loadLoggedInFile() {
        FileInputStream fis = null;
        String textFromFile = "";

        try {
            fis = openFileInput(IS_LOGGED_IN_FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text); // .append("\n");
            }

            textFromFile = sb.toString();
            // if textFromFile Contains 'Online: ' make isLoggedIn True, else make isLoggedIn False
            isLoggedIn = textFromFile.contains(getResources().getString(R.string.online));

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