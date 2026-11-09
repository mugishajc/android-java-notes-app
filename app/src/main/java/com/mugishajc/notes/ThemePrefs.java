package com.mugishajc.notes;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/** Remembers the light/dark choice and applies it through AppCompatDelegate. */
public final class ThemePrefs {

    public static final int FOLLOW_SYSTEM = 0;
    public static final int LIGHT = 1;
    public static final int DARK = 2;

    private static final String FILE = "settings";
    private static final String KEY_THEME = "theme";

    private ThemePrefs() {
    }

    public static int get(Context context) {
        return prefs(context).getInt(KEY_THEME, FOLLOW_SYSTEM);
    }

    public static void set(Context context, int theme) {
        prefs(context).edit().putInt(KEY_THEME, theme).apply();
        apply(theme);
    }

    public static void apply(int theme) {
        AppCompatDelegate.setDefaultNightMode(toNightMode(theme));
    }

    static int toNightMode(int theme) {
        switch (theme) {
            case LIGHT:
                return AppCompatDelegate.MODE_NIGHT_NO;
            case DARK:
                return AppCompatDelegate.MODE_NIGHT_YES;
            default:
                return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }
}
