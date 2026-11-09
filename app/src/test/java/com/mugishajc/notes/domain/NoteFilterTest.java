package com.mugishajc.notes.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.mugishajc.notes.data.Category;
import com.mugishajc.notes.data.Note;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class NoteFilterTest {

    private static Note note(long id, String title, String body, Category category, boolean pinned, long updatedAt) {
        return new Note(id, title, body, category, pinned, updatedAt, updatedAt);
    }

    private final Note groceries = note(1, "Groceries", "milk, eggs, bread", Category.TODO, false, 100);
    private final Note standup = note(2, "Standup notes", "Ship the release on Friday", Category.WORK, true, 50);
    private final Note app = note(3, "App idea", "A notes app with categories", Category.IDEAS, false, 300);
    private final Note trip = note(4, "Trip to Musanze", "Book the bus early", Category.PERSONAL, false, 200);

    private final List<Note> all = Arrays.asList(groceries, standup, app, trip);

    @Test
    public void nullOrEmptyInputGivesEmptyList() {
        assertTrue(NoteFilter.apply(null, "x", null).isEmpty());
        assertTrue(NoteFilter.apply(List.of(), null, Category.WORK).isEmpty());
    }

    @Test
    public void noFilterReturnsEverythingPinnedFirstThenNewest() {
        List<Note> result = NoteFilter.apply(all, null, null);
        assertEquals(Arrays.asList(standup, app, trip, groceries), result);
    }

    @Test
    public void queryMatchesTitleOrBodyIgnoringCase() {
        assertEquals(List.of(groceries), NoteFilter.apply(all, "EGGS", null));
        assertEquals(List.of(trip), NoteFilter.apply(all, "musanze", null));
    }

    @Test
    public void queryIsTrimmedAndBlankMeansNoFilter() {
        assertEquals(4, NoteFilter.apply(all, "   ", null).size());
        assertEquals(List.of(app), NoteFilter.apply(all, "  idea ", null));
    }

    @Test
    public void categoryFilterKeepsOnlyThatCategory() {
        assertEquals(List.of(standup), NoteFilter.apply(all, null, Category.WORK));
        assertTrue(NoteFilter.apply(all, null, Category.OTHER).isEmpty());
    }

    @Test
    public void queryAndCategoryAreCombined() {
        // "notes" appears in both standup (WORK) and app (IDEAS)
        assertEquals(2, NoteFilter.apply(all, "notes", null).size());
        assertEquals(List.of(app), NoteFilter.apply(all, "notes", Category.IDEAS));
    }

    @Test
    public void tieOnTimestampFallsBackToNewestId() {
        Note older = note(10, "a", "", Category.OTHER, false, 500);
        Note newer = note(11, "b", "", Category.OTHER, false, 500);
        assertEquals(Arrays.asList(newer, older), NoteFilter.apply(Arrays.asList(older, newer), null, null));
    }

    @Test
    public void inputListIsNotModified() {
        List<Note> input = Arrays.asList(groceries, standup);
        NoteFilter.apply(input, null, null);
        assertEquals(groceries, input.get(0));
    }
}
