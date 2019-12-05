package com.example.daily.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.daily.db.DiaryDAO;
import com.example.daily.model.Diary;

@Database(entities = {Diary.class}, version=1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DiaryDAO getDiaryDAO();
}
