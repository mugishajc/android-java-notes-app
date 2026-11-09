package com.mugishajc.notes.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY pinned DESC, updated_at DESC")
    LiveData<List<Note>> observeAll();

    @Query("SELECT * FROM notes WHERE id = :id")
    Note findById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Note note);

    @Update
    int update(Note note);

    @Delete
    int delete(Note note);

    @Query("UPDATE notes SET pinned = :pinned WHERE id = :id")
    void setPinned(long id, boolean pinned);
}
