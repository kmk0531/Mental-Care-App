package com.example.tp;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ResultRecord.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ResultDao resultDao();
}