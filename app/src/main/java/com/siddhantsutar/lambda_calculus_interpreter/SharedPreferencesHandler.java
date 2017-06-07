package com.siddhantsutar.lambda_calculus_interpreter;

import android.content.SharedPreferences;

public class SharedPreferencesHandler {

    private SharedPreferences sharedPreferences;
    private static SharedPreferencesHandler sharedPreferencesHandler = new SharedPreferencesHandler();

    public static SharedPreferencesHandler getInstance() {
        return sharedPreferencesHandler;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

}