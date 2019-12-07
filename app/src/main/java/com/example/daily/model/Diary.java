package com.example.daily.model;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName="diary")
public class Diary{

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    private int dayOfMonth;
    private int month;
    private int year;
    private byte[] img;
    private String context;

    public int getId(){
        return id;
    }

    public int getDayOfMonth(){ return dayOfMonth;}
    public int getMonth() {return month;}
    public int getYear(){ return year;}
    public String getContext(){
        return context;
    }
    public byte[] getImg(){
        return img;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setDayOfMonth(int dom){
        this.dayOfMonth = dom;
    }

    public void setMonth(int m){
        this.month = m;
    }

    public void setYear(int y){
        this.year = y;
    }

    public void setImg(byte[] img){
        this.img = img;
    }

    public void setContext(String context){
        this.context = context;
    }

}