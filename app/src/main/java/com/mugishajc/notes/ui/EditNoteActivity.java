package com.mugishajc.notes.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mugishajc.notes.NotesApp;
import com.mugishajc.notes.R;
import com.mugishajc.notes.data.Category;
import com.mugishajc.notes.data.Note;
import com.mugishajc.notes.databinding.ActivityEditNoteBinding;

import java.util.EnumMap;
import java.util.Map;

public class EditNoteActivity extends AppCompatActivity {

    private static final String EXTRA_NOTE_ID = "note_id";
    private static final String EXTRA_CATEGORY = "category";
    private static final String STATE_POPULATED = "populated";
    private static final String STATE_PINNED = "pinned";

    public static Intent newNote(Context context, @Nullable Category category) {
        Intent intent = new Intent(context, EditNoteActivity.class);
        if (category != null) {
            intent.putExtra(EXTRA_CATEGORY, category.name());
        }
        return intent;
    }

    public static Intent editNote(Context context, long noteId) {
        return new Intent(context, EditNoteActivity.class).putExtra(EXTRA_NOTE_ID, noteId);
    }

    private ActivityEditNoteBinding binding;
    private EditNoteViewModel viewModel;
    private final Map<Category, Chip> chips = new EnumMap<>(Category.class);

    // The fields are filled from the note once; after that the views own the text.
    private boolean populated;
    private boolean pinned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> saveAndClose());

        if (savedInstanceState != null) {
            populated = savedInstanceState.getBoolean(STATE_POPULATED);
            pinned = savedInstanceState.getBoolean(STATE_PINNED);
        }

        buildCategoryChips();

        NotesApp app = (NotesApp) getApplication();
        viewModel = new ViewModelProvider(this, new ViewModelFactory(app.getRepository()))
                .get(EditNoteViewModel.class);

        long noteId = getIntent().getLongExtra(EXTRA_NOTE_ID, 0);
        String category = getIntent().getStringExtra(EXTRA_CATEGORY);
        viewModel.start(noteId, category == null ? null : Category.fromName(category));
        setTitle(noteId > 0 ? R.string.edit_note : R.string.new_note);

        viewModel.getNote().observe(this, this::populate);
        viewModel.isClosed().observe(this, closed -> {
            if (Boolean.TRUE.equals(closed)) {
                finish();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                saveAndClose();
            }
        });
    }

    private void buildCategoryChips() {
        for (Category category : Category.values()) {
            Chip chip = new Chip(this, null, com.google.android.material.R.attr.chipStyle);
            chip.setId(android.view.View.generateViewId());
            chip.setText(CategoryLabels.of(category));
            chip.setCheckable(true);
            chip.setCheckedIconVisible(true);
            binding.categoryGroup.addView(chip);
            chips.put(category, chip);
        }
    }

    private void populate(@Nullable Note note) {
        if (note == null || populated) {
            return;
        }
        populated = true;
        binding.titleInput.setText(note.getTitle());
        binding.bodyInput.setText(note.getBody());
        Chip chip = chips.get(note.getCategory());
        if (chip != null) {
            chip.setChecked(true);
        }
        pinned = note.isPinned();
        invalidateOptionsMenu();
        if (note.getId() == 0) {
            binding.titleInput.requestFocus();
        }
    }

    private Category selectedCategory() {
        int checkedId = binding.categoryGroup.getCheckedChipId();
        for (Map.Entry<Category, Chip> entry : chips.entrySet()) {
            if (entry.getValue().getId() == checkedId) {
                return entry.getKey();
            }
        }
        return Category.OTHER;
    }

    private void saveAndClose() {
        EditNoteViewModel.SaveResult result = viewModel.save(
                String.valueOf(binding.titleInput.getText()),
                String.valueOf(binding.bodyInput.getText()),
                selectedCategory(),
                pinned);
        if (result == EditNoteViewModel.SaveResult.DISCARDED_EMPTY && !viewModel.isNewNote()) {
            // An existing note emptied out is kept as is, so tell the user nothing happened.
            Toast.makeText(this, R.string.draft_discarded, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem pin = menu.findItem(R.id.action_pin);
        pin.setTitle(pinned ? R.string.unpin : R.string.pin);
        if (pin.getIcon() != null) {
            pin.getIcon().mutate().setAlpha(pinned ? 255 : 110);
        }
        menu.findItem(R.id.action_delete).setVisible(!viewModel.isNewNote());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_pin) {
            pinned = !pinned;
            invalidateOptionsMenu();
            return true;
        }
        if (id == R.id.action_delete) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.delete_confirm_title)
                    .setMessage(R.string.delete_confirm_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.delete, (dialog, which) -> viewModel.delete())
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_POPULATED, populated);
        outState.putBoolean(STATE_PINNED, pinned);
    }
}
