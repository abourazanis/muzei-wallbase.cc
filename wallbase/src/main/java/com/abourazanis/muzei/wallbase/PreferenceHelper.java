/*
 * Copyright 2014 Anastasios Bourazanis (a.bourazanis@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.abourazanis.muzei.wallbase;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;

public class PreferenceHelper {

    //PURITY
    public static final int SAFE = 100;
    public static final int SKETCHY = 10;

    //CATEGORIES
    public static final int GENERAL = 100;
    public static final int MANGA = 10;
    public static final int PEOPLE = 1;


    public static final int MIN_FREQ_MILLIS = (int) (3 * DateUtils.HOUR_IN_MILLIS); // rotate every 3 hours

    private static final int DEFAULT_FREQ_MILLIS = (int)(24 * DateUtils.HOUR_IN_MILLIS); // rotate every 24 hours

    public static boolean getConfigWifiOnly(Context context) {
        SharedPreferences preferences = getPreferences(context);
        return preferences.getBoolean("conf_wifi", true);
    }

    public static void setConfigWifiOnly(Context context, boolean wifiOnly) {
        SharedPreferences preferences = getPreferences(context);
        preferences.edit().putBoolean("conf_wifi", wifiOnly).commit();
    }

    public static void setConfigFreq(Context context, int durationMillis) {
        SharedPreferences preferences = getPreferences(context);
        preferences.edit().putInt("conf_freq", durationMillis).commit();
    }

    public static int getConfigFreq(Context context) {
        SharedPreferences preferences = getPreferences(context);
        return preferences.getInt("conf_freq", DEFAULT_FREQ_MILLIS);
    }

    public static int getConfigPurity(Context context){
        SharedPreferences preferences = getPreferences(context);
        return preferences.getInt("conf_purity", SAFE);
    }

    public static void setConfigPurity(Context context, int purity){
        SharedPreferences preferences = getPreferences(context);
        preferences.edit().putInt("conf_purity", purity).commit();
    }

    public static int getConfigCategories(Context context){
        SharedPreferences preferences = getPreferences(context);
        return preferences.getInt("conf_categories", GENERAL+MANGA+PEOPLE);
    }

    public static void setConfigCategories(Context context, int categories){
        SharedPreferences preferences = getPreferences(context);
        preferences.edit().putInt("conf_categories", categories).commit();
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(WallhavenArtSource.SOURCE_NAME, Context.MODE_PRIVATE);
    }
}
