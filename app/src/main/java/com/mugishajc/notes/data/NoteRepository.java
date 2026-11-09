package com.mugishajc.notes.data;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * Single entry point for reading and writing notes. ViewModels depend on this
 * interface rather than on Room directly, which keeps them testable on the JVM.
 */
public interface NoteRepository {

    interface Callback<T> {
        void onResult(T value);
    }

    LiveData<List<Note>> observeNotes();

    void load(long id, Callback<Note> callback);

    /** Inserts the note when its id is 0, otherwise updates it. Returns the id. */
    void save(Note note, @Nullable Callback<Long> callback);

    void delete(Note note);

    void setPinned(long id, boolean pinned);
}
