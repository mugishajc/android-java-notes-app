package com.mugishajc.notes.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mugishajc.notes.data.Category;
import com.mugishajc.notes.data.Note;
import com.mugishajc.notes.data.NoteRepository;

import java.util.function.LongSupplier;

/** Loads a single note for editing and decides what, if anything, gets saved. */
public class EditNoteViewModel extends ViewModel {

    public enum SaveResult {
        /** Note was written to the repository. */
        SAVED,
        /** Nothing changed on an existing note, so no write was needed. */
        UNCHANGED,
        /** Title and body were both blank, the draft is dropped. */
        DISCARDED_EMPTY
    }

    static final int MAX_DERIVED_TITLE = 40;

    private final NoteRepository repository;
    private final LongSupplier clock;

    private final MutableLiveData<Note> note = new MutableLiveData<>();
    private final MutableLiveData<Boolean> closed = new MutableLiveData<>(false);
    private boolean started;

    public EditNoteViewModel(NoteRepository repository, LongSupplier clock) {
        this.repository = repository;
        this.clock = clock;
    }

    /** Safe to call on every onCreate; only the first call loads anything. */
    public void start(long noteId, @Nullable Category defaultCategory) {
        if (started) {
            return;
        }
        started = true;
        if (noteId <= 0) {
            Category category = defaultCategory != null ? defaultCategory : Category.PERSONAL;
            note.setValue(new Note("", "", category, clock.getAsLong()));
        } else {
            repository.load(noteId, loaded -> {
                if (loaded == null) {
                    // Deleted elsewhere while we were opening it.
                    closed.setValue(true);
                } else {
                    note.setValue(loaded);
                }
            });
        }
    }

    public LiveData<Note> getNote() {
        return note;
    }

    /** Becomes true once the screen should finish. */
    public LiveData<Boolean> isClosed() {
        return closed;
    }

    public boolean isNewNote() {
        Note current = note.getValue();
        return current == null || current.getId() == 0;
    }

    public SaveResult save(@NonNull String title, @NonNull String body, @NonNull Category category, boolean pinned) {
        Note current = note.getValue();
        if (current == null) {
            // Still loading; treat as nothing to save.
            closed.setValue(true);
            return SaveResult.UNCHANGED;
        }

        String cleanTitle = title.trim();
        String cleanBody = body.trim();
        if (cleanTitle.isEmpty() && cleanBody.isEmpty()) {
            closed.setValue(true);
            return SaveResult.DISCARDED_EMPTY;
        }
        if (cleanTitle.isEmpty()) {
            cleanTitle = deriveTitle(cleanBody);
        }

        boolean changed = !cleanTitle.equals(current.getTitle())
                || !cleanBody.equals(current.getBody())
                || category != current.getCategory()
                || pinned != current.isPinned();
        if (!changed && current.getId() != 0) {
            closed.setValue(true);
            return SaveResult.UNCHANGED;
        }

        Note updated = current.copy();
        updated.setTitle(cleanTitle);
        updated.setBody(cleanBody);
        updated.setCategory(category);
        updated.setPinned(pinned);
        updated.setUpdatedAt(clock.getAsLong());

        repository.save(updated, id -> {
            updated.setId(id);
            note.setValue(updated);
            closed.setValue(true);
        });
        return SaveResult.SAVED;
    }

    public void delete() {
        Note current = note.getValue();
        if (current != null && current.getId() != 0) {
            repository.delete(current);
        }
        closed.setValue(true);
    }

    /** First line of the body, shortened, used when the title is left blank. */
    static String deriveTitle(String body) {
        String firstLine = body.split("\\R", 2)[0].trim();
        if (firstLine.length() <= MAX_DERIVED_TITLE) {
            return firstLine;
        }
        return firstLine.substring(0, MAX_DERIVED_TITLE - 1).trim() + "…";
    }
}
