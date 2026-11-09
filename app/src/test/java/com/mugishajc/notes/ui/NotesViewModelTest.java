package com.mugishajc.notes.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.mugishajc.notes.data.Category;
import com.mugishajc.notes.data.Note;
import com.mugishajc.notes.testing.FakeNoteRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class NotesViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutor = new InstantTaskExecutorRule();

    private FakeNoteRepository repository;
    private NotesViewModel viewModel;
    private List<Note> latest = new ArrayList<>();
    private final Observer<List<Note>> observer = notes -> latest = notes;

    @Before
    public void setUp() {
        repository = new FakeNoteRepository(
                new Note(1, "Budget", "Rent and transport", Category.PERSONAL, false, 10, 10),
                new Note(2, "Sprint review", "Demo the search feature", Category.WORK, false, 20, 20),
                new Note(3, "Podcast", "Episode about Kigali startups", Category.IDEAS, false, 30, 30));
        viewModel = new NotesViewModel(repository);
        viewModel.getNotes().observeForever(observer);
    }

    @After
    public void tearDown() {
        viewModel.getNotes().removeObserver(observer);
    }

    private List<Long> ids() {
        List<Long> ids = new ArrayList<>();
        for (Note note : latest) {
            ids.add(note.getId());
        }
        return ids;
    }

    @Test
    public void showsAllNotesNewestFirst() {
        assertEquals(List.of(3L, 2L, 1L), ids());
        assertFalse(viewModel.isFiltering());
    }

    @Test
    public void searchNarrowsTheList() {
        viewModel.setQuery("search");
        assertEquals(List.of(2L), ids());
        assertTrue(viewModel.isFiltering());

        viewModel.setQuery("");
        assertEquals(3, latest.size());
    }

    @Test
    public void categoryNarrowsTheListAndNullClearsIt() {
        viewModel.selectCategory(Category.IDEAS);
        assertEquals(List.of(3L), ids());

        viewModel.selectCategory(null);
        assertEquals(3, latest.size());
    }

    @Test
    public void pinningMovesNoteToTheTop() {
        Note budget = repository.get(1);
        viewModel.togglePin(budget);

        assertEquals(Long.valueOf(1), ids().get(0));
        assertTrue(latest.get(0).isPinned());

        viewModel.togglePin(latest.get(0));
        assertFalse(repository.get(1).isPinned());
    }

    @Test
    public void listUpdatesWhenRepositoryChanges() {
        repository.save(new Note("Late addition", "", Category.TODO, 99), null);
        assertEquals(4, latest.size());
        assertEquals("Late addition", latest.get(0).getTitle());
    }

    @Test
    public void deleteThenUndoRestoresTheSameNote() {
        Note podcast = repository.get(3);
        viewModel.delete(podcast);
        assertEquals(List.of(2L, 1L), ids());

        assertTrue(viewModel.undoDelete());
        assertEquals(List.of(3L, 2L, 1L), ids());
        assertEquals(podcast, repository.get(3));

        // Undo is single-shot.
        assertFalse(viewModel.undoDelete());
    }
}
