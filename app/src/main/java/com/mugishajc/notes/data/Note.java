package com.mugishajc.notes.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "notes", indices = {@Index("category"), @Index("pinned")})
public class Note {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String title;

    @NonNull
    private String body;

    @NonNull
    private Category category;

    private boolean pinned;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    public Note(long id, @NonNull String title, @NonNull String body, @NonNull Category category,
                boolean pinned, long createdAt, long updatedAt) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.category = category;
        this.pinned = pinned;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** Convenience constructor for a brand new, unsaved note. */
    @Ignore
    public Note(@NonNull String title, @NonNull String body, @NonNull Category category, long now) {
        this(0, title, body, category, false, now, now);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getBody() {
        return body;
    }

    public void setBody(@NonNull String body) {
        this.body = body;
    }

    @NonNull
    public Category getCategory() {
        return category;
    }

    public void setCategory(@NonNull Category category) {
        this.category = category;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    /** Returns a detached copy, so list items are never mutated in place. */
    public Note copy() {
        return new Note(id, title, body, category, pinned, createdAt, updatedAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Note)) return false;
        Note note = (Note) o;
        return id == note.id
                && pinned == note.pinned
                && createdAt == note.createdAt
                && updatedAt == note.updatedAt
                && title.equals(note.title)
                && body.equals(note.body)
                && category == note.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, body, category, pinned, createdAt, updatedAt);
    }

    @NonNull
    @Override
    public String toString() {
        return "Note{id=" + id + ", title='" + title + "', category=" + category + ", pinned=" + pinned + '}';
    }
}
