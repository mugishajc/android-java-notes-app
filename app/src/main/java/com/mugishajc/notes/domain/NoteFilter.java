package com.mugishajc.notes.domain;

import androidx.annotation.Nullable;

import com.mugishajc.notes.data.Category;
import com.mugishajc.notes.data.Note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Pure filtering and ordering rules for the notes list. Kept free of Android
 * types so it can be unit tested directly.
 */
public final class NoteFilter {

    /** Pinned notes first, then most recently edited, then newest id as a tie-breaker. */
    public static final Comparator<Note> DISPLAY_ORDER = Comparator
            .comparing(Note::isPinned).reversed()
            .thenComparing(Comparator.comparingLong(Note::getUpdatedAt).reversed())
            .thenComparing(Comparator.comparingLong(Note::getId).reversed());

    private NoteFilter() {
    }

    /**
     * @param query    free text matched against title and body, ignoring case; blank means no filter
     * @param category only keep notes in this category; null means all categories
     */
    public static List<Note> apply(@Nullable List<Note> notes, @Nullable String query, @Nullable Category category) {
        if (notes == null || notes.isEmpty()) {
            return Collections.emptyList();
        }
        String needle = normalise(query);
        List<Note> result = new ArrayList<>();
        for (Note note : notes) {
            if (category != null && note.getCategory() != category) {
                continue;
            }
            if (!needle.isEmpty() && !matches(note, needle)) {
                continue;
            }
            result.add(note);
        }
        result.sort(DISPLAY_ORDER);
        return result;
    }

    static boolean matches(Note note, String needle) {
        return note.getTitle().toLowerCase(Locale.ROOT).contains(needle)
                || note.getBody().toLowerCase(Locale.ROOT).contains(needle);
    }

    private static String normalise(@Nullable String query) {
        return query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
    }
}
