package com.mugishajc.notes.data;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/** Room-backed repository. All writes run on a single background thread. */
public class RoomNoteRepository implements NoteRepository {

    private final NoteDao dao;
    private final Executor io;
    private final Handler mainThread = new Handler(Looper.getMainLooper());

    public RoomNoteRepository(NoteDao dao) {
        this(dao, Executors.newSingleThreadExecutor());
    }

    public RoomNoteRepository(NoteDao dao, Executor io) {
        this.dao = dao;
        this.io = io;
    }

    @Override
    public LiveData<List<Note>> observeNotes() {
        return dao.observeAll();
    }

    @Override
    public void load(long id, Callback<Note> callback) {
        io.execute(() -> {
            Note note = dao.findById(id);
            mainThread.post(() -> callback.onResult(note));
        });
    }

    @Override
    public void save(Note note, @Nullable Callback<Long> callback) {
        // Work on a copy so the caller can keep editing its own instance.
        Note toSave = note.copy();
        io.execute(() -> {
            long id;
            if (toSave.getId() == 0) {
                id = dao.insert(toSave);
            } else {
                dao.update(toSave);
                id = toSave.getId();
            }
            if (callback != null) {
                mainThread.post(() -> callback.onResult(id));
            }
        });
    }

    @Override
    public void delete(Note note) {
        Note toDelete = note.copy();
        io.execute(() -> dao.delete(toDelete));
    }

    @Override
    public void restore(Note note) {
        Note toRestore = note.copy();
        io.execute(() -> dao.insert(toRestore));
    }

    @Override
    public void setPinned(long id, boolean pinned) {
        io.execute(() -> dao.setPinned(id, pinned));
    }
}
