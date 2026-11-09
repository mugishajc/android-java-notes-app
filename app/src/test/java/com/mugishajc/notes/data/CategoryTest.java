package com.mugishajc.notes.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class CategoryTest {

    @Test
    public void fromNameIsCaseInsensitive() {
        assertEquals(Category.WORK, Category.fromName("work"));
        assertEquals(Category.IDEAS, Category.fromName("Ideas"));
    }

    @Test
    public void unknownOrNullFallsBackToOther() {
        assertEquals(Category.OTHER, Category.fromName("shopping"));
        assertEquals(Category.OTHER, Category.fromName(null));
    }

    @Test
    public void converterRoundTrips() {
        for (Category category : Category.values()) {
            assertEquals(category, Converters.toCategory(Converters.fromCategory(category)));
        }
        assertNull(Converters.fromCategory(null));
    }
}
