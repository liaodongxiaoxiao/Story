package com.ldxx.story.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wangzhuo-neu on 2016/10/24.
 */

public class SharedPreferencesUtils {
    private static final String FONT_SIZE = "font_size";
    private static final String IS_DESC = "is_desc";
    private SharedPreferences preferences;

    public SharedPreferencesUtils(Context context) {
        preferences = context.getSharedPreferences("story", Activity.MODE_PRIVATE);
    }

    public float getFontSize() {
        return preferences.getFloat(FONT_SIZE, 16);
    }

    public void saveFontSize(float fontSize) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(FONT_SIZE, fontSize);
        editor.apply();
    }

    public boolean isDesc() {
        return preferences.getBoolean(IS_DESC, true);
    }

    public void saveIsDesc(boolean isDesc) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_DESC, isDesc);
        editor.apply();
    }
}
