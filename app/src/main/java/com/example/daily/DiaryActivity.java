package com.example.daily;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

public class DiaryActivity extends AppCompatActivity {

    public static final int PICK_FROM_CAMERA= 0;
    public static final int PICK_FROM_ALBUM = 1;


    InputMethodManager imm;
    Bitmap bitmap;
    EditText edit;
    ImageView imageView;
    File tempFile;


    protected void getFromAlbum(){
        Intent intent = new Intent();
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    protected void getFromCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    protected Bitmap byteToBitmap(byte[] byteArray){
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        return bitmap;
    }

    protected byte[] bitmapToByte(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return stream.toByteArray();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch(requestCode){
            case PICK_FROM_CAMERA:
                break;
            case PICK_FROM_ALBUM:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode==PICK_FROM_CAMERA)&&(resultCode==RESULT_OK)){
            bitmap = (Bitmap) data.getExtras().get("data");
            if (bitmap!=null){
                imageView.setImageBitmap(bitmap);
            }
        }

        if ((requestCode==PICK_FROM_ALBUM)&&(resultCode==RESULT_OK)){
            try{
                InputStream in = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(in);
                in.close();
                imageView.setImageBitmap(bitmap);
            }catch(Exception e){
                Toast.makeText(this, "Sorry sth wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed(){
        setResult(RESULT_CANCELED);
        finish();
    }

}
