package com.example.tp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "results")
public class ResultRecord {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String testType;      // "PHQ-9", "GAD-7" 등
    public int score;
    public String description;
    public long timestamp;       // 저장 시 시간 (millis)
}
