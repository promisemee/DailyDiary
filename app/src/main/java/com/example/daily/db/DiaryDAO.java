package com.example.daily.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.daily.model.Diary;

import java.util.List;

@Dao
public interface DiaryDAO {
    @Insert
    public void insert(Diary... diary);
    @Update
    public void update(Diary... diary);
    @Delete
    public void delete(Diary... diary);

    @Query("Select * FROM diary")
    public List<Diary> getDiaryList();

    @Query("Select * FROM diary where id = :dId")
    public Diary getDiaryWithId(int dId);

    @Query ("Select * FROM diary where dayOfMonth = :d and month =:m and year = :y")
    public List<Diary> getDiaryListWithDate(int d, int m, int y);

    @Query("Delete FROM diary")
    public void deleteAll();
}

