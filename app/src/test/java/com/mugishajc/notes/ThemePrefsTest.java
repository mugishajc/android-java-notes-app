package com.mugishajc.notes;

import static org.junit.Assert.assertEquals;

import androidx.appcompat.app.AppCompatDelegate;

import org.junit.Test;

public class ThemePrefsTest {

    @Test
    public void mapsChoicesToNightModes() {
        assertEquals(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, ThemePrefs.toNightMode(ThemePrefs.FOLLOW_SYSTEM));
        assertEquals(AppCompatDelegate.MODE_NIGHT_NO, ThemePrefs.toNightMode(ThemePrefs.LIGHT));
        assertEquals(AppCompatDelegate.MODE_NIGHT_YES, ThemePrefs.toNightMode(ThemePrefs.DARK));
    }

    @Test
    public void unknownValueFollowsSystem() {
        assertEquals(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, ThemePrefs.toNightMode(99));
    }
}
