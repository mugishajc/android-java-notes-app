package com.mugishajc.notes.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.mugishajc.notes.NotesApp;
import com.mugishajc.notes.R;
import com.mugishajc.notes.data.Category;
import com.mugishajc.notes.data.Note;
import com.mugishajc.notes.databinding.ActivityMainBinding;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NoteAdapter.Listener {

    private ActivityMainBinding binding;
    private NotesViewModel viewModel;
    private NoteAdapter adapter;

    /** Chip view id to category; the "All" chip maps to null. */
    private final Map<Integer, Category> chipCategories = new HashMap<>();

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

        buildFilterChips();

        binding.newNoteButton.setOnClickListener(v ->
                startActivity(EditNoteActivity.newNote(this, viewModel.getSelectedCategory().getValue())));

        viewModel.getNotes().observe(this, notes -> {
            adapter.submitList(notes);
            binding.emptyView.setVisibility(notes.isEmpty() ? View.VISIBLE : View.GONE);
            binding.emptyView.setText(viewModel.isFiltering() ? R.string.empty_filtered : R.string.empty_notes);
        });
    }

    private void buildFilterChips() {
        addFilterChip(getString(R.string.category_all), null);
        for (Category category : Category.values()) {
            addFilterChip(getString(CategoryLabels.of(category)), category);
        }

        // Restore the selection the ViewModel kept across rotation.
        Category current = viewModel.getSelectedCategory().getValue();
        for (Map.Entry<Integer, Category> entry : chipCategories.entrySet()) {
            if (entry.getValue() == current) {
                binding.filterGroup.check(entry.getKey());
            }
        }

        binding.filterGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                viewModel.selectCategory(chipCategories.get(checkedIds.get(0)));
            }
        });
    }

    private void addFilterChip(String label, @Nullable Category category) {
        Chip chip = new Chip(this, null, com.google.android.material.R.attr.chipStyle);
        chip.setId(View.generateViewId());
        chip.setText(label);
        chip.setCheckable(true);
        chip.setCheckedIconVisible(false);
        binding.filterGroup.addView(chip);
        chipCategories.put(chip.getId(), category);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null) {
            searchView.setQueryHint(getString(R.string.search_hint));
            searchView.setMaxWidth(Integer.MAX_VALUE);

            String query = viewModel.getQuery().getValue();
            if (query != null && !query.isEmpty()) {
                searchItem.expandActionView();
                searchView.setQuery(query, false);
            }

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String text) {
                    searchView.clearFocus();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String text) {
                    viewModel.setQuery(text);
                    return true;
                }
            });
        }
        return true;
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
