package com.example.daily;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteAbortException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;
import androidx.room.Update;

import com.example.daily.db.AppDatabase;
import com.example.daily.db.DiaryDAO;
import com.example.daily.model.Diary;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UpdateDiaryActivity extends DiaryActivity {


    public static String EXTRA_DATA_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_diary);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM. dd. yyyy ");
        date = dateFormat.format(calendar.getTime());
        setTitle(date);

        mDiaryDAO = Room.databaseBuilder(this, AppDatabase.class, "db-diary")
                .allowMainThreadQueries()   //Allows room to do operation on main thread
                .build()
                .getDiaryDAO();

        setPage();
    }

    private void setPage(){
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        edit = (EditText) findViewById(R.id.txt_edit);
        edit.requestFocus();

        imageView = findViewById(R.id.imageView);

        Button getPhoto = findViewById(R.id.gallery);
        Button getCamera = findViewById(R.id.camera);
        Button getDate = findViewById(R.id.date);
        Button getDraw = findViewById(R.id.draw);

        int id = getIntent().getIntExtra(EXTRA_DATA_ID, 0);
        mCurrent = mDiaryDAO.getDiaryWithId(id);

        if (mCurrent==null){
            setResult(RESULT_CANCELED);
        }else {
            if (mCurrent.getImg()!=null)
                bitImg = byteToBitmap(mCurrent.getImg());
            edit.setText(mCurrent.getContext());
            imageView.setImageBitmap(bitImg);
        }


        //OnClick Listeners
        getPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ActivityCompat.checkSelfPermission(UpdateDiaryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(UpdateDiaryActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_ALBUM);
                }
                getFromAlbum();
            }

        });
        getCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ContextCompat.checkSelfPermission(UpdateDiaryActivity.this, Manifest.permission.CAMERA);
                if(permissionCheck==PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(UpdateDiaryActivity.this, new String[]{Manifest.permission.CAMERA},PICK_FROM_CAMERA);
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
               DatePickerDialog dialog = new DatePickerDialog(UpdateDiaryActivity.this, new DatePickerDialog.OnDateSetListener() {
                   @Override
                   public void onDateSet(DatePicker datePicker, int year, int month, int d) {

                       date = String.format("%d 년 %d 월 %d 일", year, month+1, d);
                       Toast.makeText(UpdateDiaryActivity.this, date, Toast.LENGTH_SHORT).show();
                       setTitle(date);
                   }
               }, mYear, mMonth, mDate);

               dialog.show();

            }
        });

        getDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDrawing();
            }

        });

        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final String[] options = new String[]{getString(R.string.delete),getString(R.string.crop)};

                AlertDialog.Builder dialog = new AlertDialog.Builder(UpdateDiaryActivity.this);

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
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(edit.getText())&&(image == null)) {
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                String context = edit.getText().toString();

                String date = this.date;

                //Input
                mCurrent.setContext(context);
                mCurrent.setYear(mYear);
                mCurrent.setMonth(mMonth);
                mCurrent.setDayOfMonth(mDate);

                Drawable drawable = imageView.getDrawable();
                if (sign==1){
                    Bitmap bmp = ((BitmapDrawable)drawable).getBitmap();
                    if (bmp == null) {
                        mCurrent.setImg(null);
                    }else{
                        byte[] img = bitmapToByte(bmp);
                        mCurrent.setImg(img);
                    }

                }else{
                    mCurrent.setImg(null);
                }
                try{
                    mDiaryDAO.update(mCurrent);
                }catch (SQLiteAbortException e){
                    Toast.makeText(UpdateDiaryActivity.this, getString(R.string.sthwrong), Toast.LENGTH_SHORT).show();
                }

                // Set the result status to indicate success.
                setResult(RESULT_OK);
            }
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
