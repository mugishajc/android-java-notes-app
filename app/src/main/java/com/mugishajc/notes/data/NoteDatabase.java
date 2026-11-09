package com.mugishajc.notes.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Note.class}, version = 1, exportSchema = true)
@TypeConverters(Converters.class)
public abstract class NoteDatabase extends RoomDatabase {

    private static final String NAME = "notes.db";

    private static volatile NoteDatabase instance;

    public abstract NoteDao noteDao();

    public static NoteDatabase get(Context context) {
        if (instance == null) {
            synchronized (NoteDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), NoteDatabase.class, NAME)
                            .build();
                }
            }
        }
        return instance;
    }
}
