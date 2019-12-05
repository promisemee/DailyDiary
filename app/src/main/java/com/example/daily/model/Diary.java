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

    private String date;
    private byte[] img;
    private String context;

    public int getId(){
        return id;
    }

    public String getDate() {
        return date;
    }
    public String getContext(){
        return context;
    }
    public byte[] getImg(){
        return img;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setImg(byte[] img){
        this.img = img;
    }

    public void setContext(String context){
        this.context = context;
    }

}