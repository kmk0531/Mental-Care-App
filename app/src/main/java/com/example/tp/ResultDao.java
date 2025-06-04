package com.example.tp;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ResultDao {
    @Insert
    void insert(ResultRecord record);

    @Query("SELECT * FROM results ORDER BY timestamp DESC")
    List<ResultRecord> getAll();

    @Delete
    void delete(ResultRecord record); // ✅ 삭제용
}
