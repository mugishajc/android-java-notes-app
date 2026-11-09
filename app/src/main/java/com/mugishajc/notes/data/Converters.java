package com.mugishajc.notes.data;

import androidx.room.TypeConverter;

public final class Converters {

    private Converters() {
    }

    @TypeConverter
    public static String fromCategory(Category category) {
        return category == null ? null : category.name();
    }

    @TypeConverter
    public static Category toCategory(String value) {
        return Category.fromName(value);
    }
}
