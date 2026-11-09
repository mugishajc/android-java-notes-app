package com.mugishajc.notes;

import android.app.Application;

import com.mugishajc.notes.data.NoteDatabase;
import com.mugishajc.notes.data.NoteRepository;
import com.mugishajc.notes.data.RoomNoteRepository;

/** Holds the app-wide dependencies. Small enough that a DI framework is not worth it. */
public class NotesApp extends Application {

    private NoteRepository repository;

    @Override
    public void onCreate() {
        super.onCreate();
        repository = new RoomNoteRepository(NoteDatabase.get(this).noteDao());
    }

    public NoteRepository getRepository() {
        return repository;
    }
}
