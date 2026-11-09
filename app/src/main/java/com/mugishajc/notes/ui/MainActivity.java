package com.mugishajc.notes.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mugishajc.notes.NotesApp;
import com.mugishajc.notes.data.Note;
import com.mugishajc.notes.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements NoteAdapter.Listener {

    private ActivityMainBinding binding;
    private NotesViewModel viewModel;
    private NoteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        NotesApp app = (NotesApp) getApplication();
        viewModel = new ViewModelProvider(this, new ViewModelFactory(app.getRepository()))
                .get(NotesViewModel.class);

        adapter = new NoteAdapter(this);
        binding.notesList.setLayoutManager(new LinearLayoutManager(this));
        binding.notesList.setAdapter(adapter);

        binding.newNoteButton.setOnClickListener(v ->
                startActivity(EditNoteActivity.newNote(this, viewModel.getSelectedCategory().getValue())));

        viewModel.getNotes().observe(this, notes -> {
            adapter.submitList(notes);
            binding.emptyView.setVisibility(notes.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onNoteClicked(Note note) {
        startActivity(EditNoteActivity.editNote(this, note.getId()));
    }

    @Override
    public void onPinClicked(Note note) {
        viewModel.togglePin(note);
    }
}
