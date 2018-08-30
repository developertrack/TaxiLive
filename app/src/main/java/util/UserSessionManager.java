package util;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import taxilive.taxt.africa.RegisterLoginActivity;


public class UserSessionManager {

    // Email address (make variable public to access from outside)
    public static final String KEY_USER_TYPE = "usertype";
    // Email address (make variable public to access from outside)
    public static final String KEY_USERID = "userid";
    // Email address (make variable public to access from outside)
    public static final String KEY_NUMBER = "number";


    public static final String KEY_INDUSTRY = "number";
    // Sharedpref file name
    private static final String PREFER_NAME = "Trkus";
    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
    // Shared Preferences reference
    SharedPreferences pref;
    // Editor reference for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public UserSessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //Create login session
    public void createUserLoginSession(String usertype, String userid, String number) {
        // Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, true);


        // Storing email in pref
        editor.putString(KEY_USER_TYPE, usertype);

        // Storing email in pref
        editor.putString(KEY_USERID, userid);

        // Storing email in pref
        editor.putString(KEY_NUMBER, number);

        // commit changes
        editor.commit();
    }

    public void createIndustrySession(String industry_type) {
        // Storing login value as TRUE
        editor.putBoolean(IS_USER_LOGIN, true);

        editor.putString(KEY_INDUSTRY, industry_type);


        // commit changes
        editor.commit();
    }

    public String getIndustry() {

        HashMap<String, String> user = getUserDetails();

        String userid = user.get(KEY_INDUSTRY);

        return userid;
    }


    public boolean checkLogin() {
        // Check login status
        if (!this.isUserLoggedIn()) {

            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, RegisterLoginActivity.class);

            // Closing all the Activities from stack
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);

            return true;
        }
        return false;
    }


    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {

        //Use hashmap to store user credentials
        HashMap<String, String> user = new HashMap<String, String>();

        // user name
        user.put(KEY_USER_TYPE, pref.getString(KEY_USER_TYPE, null));


        // user name
        user.put(KEY_USERID, pref.getString(KEY_USERID, null));

        // user email id
        user.put(KEY_NUMBER, pref.getString(KEY_NUMBER, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     */

    public String getKeyUserid() {

        HashMap<String, String> user = getUserDetails();

        String userid = user.get(KEY_USERID);

        return userid;
    }

    public String getKeyUserType() {

        HashMap<String, String> user = getUserDetails();

        String emailid = user.get(KEY_USER_TYPE);

        return emailid;
    }


    public String getKeyNumber() {

        HashMap<String, String> user = getUserDetails();

        String number = user.get(KEY_NUMBER);

        return number;
    }


    public void logoutUser() {

        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, RegisterLoginActivity.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);

        ((Activity) _context).finish();
    }


    // Check for login
    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }
}