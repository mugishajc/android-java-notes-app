package com.mugishajc.notes.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mugishajc.notes.data.NoteRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final NoteRepository repository;

    public ViewModelFactory(NoteRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NotesViewModel.class)) {
            return (T) new NotesViewModel(repository);
        }
        if (modelClass.isAssignableFrom(EditNoteViewModel.class)) {
            return (T) new EditNoteViewModel(repository, System::currentTimeMillis);
        }
        throw new IllegalArgumentException("Unknown ViewModel " + modelClass.getName());
    }
}
