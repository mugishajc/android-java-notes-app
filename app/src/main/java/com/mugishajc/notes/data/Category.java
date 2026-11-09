package com.mugishajc.notes.data;

/**
 * Fixed set of categories a note can belong to. Stored by name in the
 * database, so renaming a constant needs a migration.
 */
public enum Category {
    PERSONAL,
    WORK,
    IDEAS,
    TODO,
    OTHER;

    /** Lenient lookup used when reading persisted values. */
    public static Category fromName(String name) {
        if (name == null) {
            return OTHER;
        }
        for (Category category : values()) {
            if (category.name().equalsIgnoreCase(name)) {
                return category;
            }
        }
        return OTHER;
    }
}
