package com.example.daily;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteAbortException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.example.daily.db.AppDatabase;
import com.example.daily.db.DiaryDAO;
import com.example.daily.model.Diary;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddDiaryActivity extends DiaryActivity{


    private DiaryDAO mDiaryDAO;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_diary);

        mDiaryDAO = Room.databaseBuilder(this, AppDatabase.class, "db-diary")
                .allowMainThreadQueries()   //Allows room to do operation on main thread
                .build()
                .getDiaryDAO();

        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        edit = findViewById(R.id.txt_edit);
        edit.requestFocus();
        imageView = findViewById(R.id.imageView);

        Button getPhoto = findViewById(R.id.gallery);
        Button getCamera = findViewById(R.id.camera);
        Button getDate = findViewById(R.id.date);
        Button getDraw = findViewById(R.id.draw);

        //set date int title (today)
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd.MMM.yy");
        date = df.format(c);

        setTitle(date);


        //OnClick Listeners
        getPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ActivityCompat.checkSelfPermission(AddDiaryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(AddDiaryActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_ALBUM);
                }
                getFromAlbum();
            }

        });
        getCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ContextCompat.checkSelfPermission(AddDiaryActivity.this, Manifest.permission.CAMERA);
                if(permissionCheck==PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(AddDiaryActivity.this, new String[]{Manifest.permission.CAMERA},PICK_FROM_CAMERA);
                }
                getFromCamera();
            }

        });

        getDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDrawing();
            }

        });

        getDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DatePickerDialog dialog = new DatePickerDialog(AddDiaryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

                        mYear = year;
                        mMonth = month+1;
                        mDate = dayOfMonth;
                        date = String.format("%d 년 %d 월 %d 일", year, month+1, dayOfMonth);
                        Toast.makeText(AddDiaryActivity.this, date, Toast.LENGTH_SHORT).show();
                        setTitle(date);
                    }
                }, mYear, mMonth-1, mDate );

                dialog.show();

            }
        });

        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final String[] options = new String[]{getString(R.string.delete),getString(R.string.crop)};

                AlertDialog.Builder dialog = new AlertDialog.Builder(AddDiaryActivity.this);

                dialog  .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch(options[i]){
                            case "delete":
                                imageView.setImageBitmap(null);
                                sign = 0;
                                break;
                            case "crop":
                                break;
                        }
                    }
                })
                        .show();
            }
        });

    }



    //toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu.menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.menu.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_x) {
            Intent replyIntent = new Intent();
            setResult(RESULT_CANCELED, replyIntent);
            finish();
        }

        if (id == R.id.action_check) {
            // Create a new Intent for the reply.
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(edit.getText())) {
                // No word was entered, set the result accordingly.
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                // Get the new word that the user entered.
                String context = edit.getText().toString();
                byte[] image = null;
                if (bitmap!=null){
                    image = bitmapToByte(bitmap);
                }
                String date = this.date;

                //Input
                Diary diary = new Diary();
                diary.setContext(context);
                diary.setYear(mYear);
                diary.setMonth(mMonth);
                diary.setDayOfMonth(mDate);

                diary.setImg(image);
                try{
                    mDiaryDAO.insert(diary);
                }catch (SQLiteAbortException e){
                    Toast.makeText(AddDiaryActivity.this, getString(R.string.sthwrong), Toast.LENGTH_SHORT).show();
                }

                // Set the result status to indicate success.
                setResult(RESULT_OK, replyIntent);
            }
            finish();
        }


        return super.onOptionsItemSelected(item);
    }




}
