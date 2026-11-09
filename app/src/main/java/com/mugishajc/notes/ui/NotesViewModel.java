package com.mugishajc.notes.ui;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mugishajc.notes.data.Category;
import com.mugishajc.notes.data.Note;
import com.mugishajc.notes.data.NoteRepository;
import com.mugishajc.notes.domain.NoteFilter;

import java.util.List;

/** State holder for the notes list: search text, category chip and the filtered result. */
public class NotesViewModel extends ViewModel {

    private final NoteRepository repository;
    private final LiveData<List<Note>> allNotes;

    private final MutableLiveData<String> query = new MutableLiveData<>("");
    private final MutableLiveData<Category> category = new MutableLiveData<>(null);
    private final MediatorLiveData<List<Note>> visibleNotes = new MediatorLiveData<>();

    @Nullable
    private Note lastDeleted;

    public NotesViewModel(NoteRepository repository) {
        this.repository = repository;
        this.allNotes = repository.observeNotes();

        visibleNotes.addSource(allNotes, notes -> refresh());
        visibleNotes.addSource(query, q -> refresh());
        visibleNotes.addSource(category, c -> refresh());
    }

    private void refresh() {
        visibleNotes.setValue(NoteFilter.apply(allNotes.getValue(), query.getValue(), category.getValue()));
    }

    public LiveData<List<Note>> getNotes() {
        return visibleNotes;
    }

    public LiveData<String> getQuery() {
        return query;
    }

    public LiveData<Category> getSelectedCategory() {
        return category;
    }

    public void setQuery(@Nullable String text) {
        String value = text == null ? "" : text;
        if (!value.equals(query.getValue())) {
            query.setValue(value);
        }
    }

    /** @param selected the category to show, or null for all notes */
    public void selectCategory(@Nullable Category selected) {
        if (selected != category.getValue()) {
            category.setValue(selected);
        }
    }

    public boolean isFiltering() {
        String q = query.getValue();
        return category.getValue() != null || (q != null && !q.trim().isEmpty());
    }

    public void togglePin(Note note) {
        repository.setPinned(note.getId(), !note.isPinned());
    }

    public void delete(Note note) {
        lastDeleted = note.copy();
        repository.delete(note);
    }

    /** @return true if there was something to undo */
    public boolean undoDelete() {
        if (lastDeleted == null) {
            return false;
        }
        repository.restore(lastDeleted);
        lastDeleted = null;
        return true;
    }
}
