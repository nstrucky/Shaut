package com.ventoray.shaut.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Nick on 2/5/2018.
 */

public class PreferenceHelper {

    public static final String GENERAL_PREFS = "GeneralPrefs";

    public static final String PREF_SELECTED_CITY_ID = "selectedCityId";


    /**
     * Saves the preference value to file given the desired key
     * @param context
     * @param key
     * @param value
     */
    public static void savePreference(Context context, String key, String value) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(GENERAL_PREFS, MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(key, value)
                .apply();
    }


    /**
     * Checks if the preference exists using the key specified and returns the value
     * @param context
     * @param key
     * @return
     */
    public static Object getPreferenceValue(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(GENERAL_PREFS, MODE_PRIVATE);
        if (preferences == null) return null;
        if (!preferences.contains(key)) return null;
        Map<String, ?> values = preferences.getAll();
        return values.get(key);
    }

}
