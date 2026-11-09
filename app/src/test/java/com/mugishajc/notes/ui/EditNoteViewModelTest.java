package com.mugishajc.notes.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.mugishajc.notes.data.Category;
import com.mugishajc.notes.data.Note;
import com.mugishajc.notes.testing.FakeNoteRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EditNoteViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutor = new InstantTaskExecutorRule();

    private FakeNoteRepository repository;
    private long now = 1_000;
    private EditNoteViewModel viewModel;

    @Before
    public void setUp() {
        repository = new FakeNoteRepository(
                new Note(5, "Call Aline", "About the venue", Category.PERSONAL, false, 100, 100));
        viewModel = new EditNoteViewModel(repository, () -> now);
    }

    @Test
    public void newNoteStartsBlankWithRequestedCategory() {
        viewModel.start(0, Category.WORK);
        Note note = viewModel.getNote().getValue();

        assertTrue(viewModel.isNewNote());
        assertEquals("", note.getTitle());
        assertEquals(Category.WORK, note.getCategory());
    }

    @Test
    public void savingNewNoteInsertsIt() {
        viewModel.start(0, null);
        now = 2_000;

        EditNoteViewModel.SaveResult result =
                viewModel.save("  Shopping ", "rice\nbeans", Category.TODO, true);

        assertEquals(EditNoteViewModel.SaveResult.SAVED, result);
        assertEquals(2, repository.size());
        Note saved = viewModel.getNote().getValue();
        assertEquals("Shopping", saved.getTitle());
        assertTrue(saved.isPinned());
        assertEquals(2_000, saved.getUpdatedAt());
        assertTrue(viewModel.isClosed().getValue());
    }

    @Test
    public void blankDraftIsDiscarded() {
        viewModel.start(0, null);

        assertEquals(EditNoteViewModel.SaveResult.DISCARDED_EMPTY,
                viewModel.save("   ", "\n ", Category.OTHER, false));
        assertEquals(0, repository.saveCount);
        assertTrue(viewModel.isClosed().getValue());
    }

    @Test
    public void blankTitleIsTakenFromFirstLineOfBody() {
        viewModel.start(0, null);
        viewModel.save("", "Pick up the parcel\nbefore 5pm", Category.TODO, false);

        assertEquals("Pick up the parcel", viewModel.getNote().getValue().getTitle());
    }

    @Test
    public void derivedTitleIsShortened() {
        String longLine = "This first line is definitely longer than forty characters";
        String title = EditNoteViewModel.deriveTitle(longLine);

        assertTrue(title.length() <= EditNoteViewModel.MAX_DERIVED_TITLE);
        assertTrue(title.endsWith("…"));
    }

    @Test
    public void editingExistingNoteKeepsIdAndCreatedAt() {
        viewModel.start(5, null);
        assertFalse(viewModel.isNewNote());
        now = 5_000;

        viewModel.save("Call Aline", "About the venue and the date", Category.PERSONAL, false);

        Note stored = repository.get(5);
        assertEquals("About the venue and the date", stored.getBody());
        assertEquals(100, stored.getCreatedAt());
        assertEquals(5_000, stored.getUpdatedAt());
        assertEquals(1, repository.size());
    }

    @Test
    public void unchangedExistingNoteIsNotRewritten() {
        viewModel.start(5, null);

        assertEquals(EditNoteViewModel.SaveResult.UNCHANGED,
                viewModel.save("Call Aline", "About the venue", Category.PERSONAL, false));
        assertEquals(0, repository.saveCount);
        assertEquals(100, repository.get(5).getUpdatedAt());
    }

    @Test
    public void startIsOnlyHonouredOnce() {
        viewModel.start(5, null);
        viewModel.start(0, Category.WORK);

        assertEquals(5, viewModel.getNote().getValue().getId());
    }

    @Test
    public void missingNoteClosesTheScreen() {
        viewModel.start(42, null);

        assertNull(viewModel.getNote().getValue());
        assertTrue(viewModel.isClosed().getValue());
    }

    @Test
    public void deleteRemovesExistingNote() {
        viewModel.start(5, null);
        viewModel.delete();

        assertNull(repository.get(5));
        assertTrue(viewModel.isClosed().getValue());
    }
}
