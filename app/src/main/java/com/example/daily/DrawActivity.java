package com.example.daily;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.Output;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DrawActivity extends AppCompatActivity {
    public static final String EXTRA_OUTPUT = "draw_output";

    DrawView draw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        draw = new DrawView(this);
        setContentView(draw);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu.menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.menu.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_x) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(getString(R.string.cancel));

            builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            });

            builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
        if (id == R.id.action_check){
            //save drawing
            Intent intent = getIntent();
            String mCurrentPhotoPath = intent.getStringExtra(EXTRA_OUTPUT);
            if (mCurrentPhotoPath!=null) {

                File file = new File(mCurrentPhotoPath);
                Bitmap bitmap = draw.canvasBitmap;

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 80 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

                //write the bytes in file
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                setResult(RESULT_OK, intent);
            }
            else{
                Log.i("#TEST", "tt");
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if (draw.getListSize()<=0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(getString(R.string.cancel));

            builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            });

            builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
        draw.onClickUndo();

    }

}
