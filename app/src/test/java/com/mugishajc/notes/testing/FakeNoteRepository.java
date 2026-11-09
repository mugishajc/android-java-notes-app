package com.mugishajc.notes.testing;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mugishajc.notes.data.Note;
import com.mugishajc.notes.data.NoteRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** In-memory repository that completes every call synchronously. */
public class FakeNoteRepository implements NoteRepository {

    private final Map<Long, Note> notes = new LinkedHashMap<>();
    private final MutableLiveData<List<Note>> live = new MutableLiveData<>(new ArrayList<>());
    private long nextId = 1;
    public int saveCount;

    public FakeNoteRepository(Note... initial) {
        for (Note note : initial) {
            notes.put(note.getId(), note.copy());
            nextId = Math.max(nextId, note.getId() + 1);
        }
        publish();
    }

    private void publish() {
        List<Note> copies = new ArrayList<>();
        for (Note note : notes.values()) {
            copies.add(note.copy());
        }
        live.setValue(copies);
    }

    @Nullable
    public Note get(long id) {
        Note note = notes.get(id);
        return note == null ? null : note.copy();
    }

    public int size() {
        return notes.size();
    }

    @Override
    public LiveData<List<Note>> observeNotes() {
        return live;
    }

    @Override
    public void load(long id, Callback<Note> callback) {
        callback.onResult(get(id));
    }

    @Override
    public void save(Note note, @Nullable Callback<Long> callback) {
        saveCount++;
        Note copy = note.copy();
        if (copy.getId() == 0) {
            copy.setId(nextId++);
        }
        notes.put(copy.getId(), copy);
        publish();
        if (callback != null) {
            callback.onResult(copy.getId());
        }
    }

    @Override
    public void delete(Note note) {
        notes.remove(note.getId());
        publish();
    }

    @Override
    public void restore(Note note) {
        notes.put(note.getId(), note.copy());
        publish();
    }

    @Override
    public void setPinned(long id, boolean pinned) {
        Note note = notes.get(id);
        if (note != null) {
            note.setPinned(pinned);
            publish();
        }
    }
}
