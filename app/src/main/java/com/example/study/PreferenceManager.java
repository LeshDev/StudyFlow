package com.example.study;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String PREF_NAME = "StudyFlowPrefs";
    private static final String KEY_USERNAME = "username";
    private final SharedPreferences sharedPreferences;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUsername(String username) {
        sharedPreferences.edit()
                .putString(KEY_USERNAME, username)
                .apply();
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "Гость");
    }

    public void saveRole(String role) {
        sharedPreferences.edit().putString("user_role", role).apply();
    }

    public String getRole() {
        return sharedPreferences.getString("user_role", "student"); // student по умолчанию
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}