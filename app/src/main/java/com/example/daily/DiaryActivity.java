package com.example.daily;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.daily.db.DiaryDAO;
import com.example.daily.model.Diary;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;

public class DiaryActivity extends AppCompatActivity {

    public static final int PICK_FROM_CAMERA= 0;
    public static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_DRAWING = 2 ;

    InputMethodManager imm;
    Bitmap bitmap;
    EditText edit;
    ImageView imageView;
    protected DiaryDAO mDiaryDAO;
    Diary mCurrent;
    protected String date;
    protected Bitmap bitImg;
    byte[] image;
    int sign = 0;

    protected Calendar calendar = Calendar.getInstance();
    int mYear = calendar.get(Calendar.YEAR);
    int mMonth = calendar.get(Calendar.MONTH)+1;
    int mDate = calendar.get(Calendar.DAY_OF_MONTH);

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

    protected void startDrawing(){
        Intent intent = new Intent(this, DrawActivity.class);
        startActivityForResult(intent, PICK_FROM_DRAWING);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]permissions, @NonNull int[]grantResults){
        if (requestCode==PICK_FROM_CAMERA){
            if(grantResults[0]==0){
                Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "FAIL", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode==PICK_FROM_ALBUM){
            if(grantResults[0]==0){
                Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "FAIL", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode==PICK_FROM_CAMERA)&&(resultCode==RESULT_OK)){
            if (data.getExtras()!=null) {
                bitmap = (Bitmap) data.getExtras().get("data");
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
            sign = 1;
        }

        if ((requestCode==PICK_FROM_ALBUM)&&(resultCode==RESULT_OK)){
            try{
                InputStream in = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(in);
                in.close();
                imageView.setImageBitmap(bitmap);
            }catch(Exception e){
                Toast.makeText(this, getString(R.string.sthwrong), Toast.LENGTH_SHORT).show();
            }
            sign = 1;
        }

        if((requestCode==PICK_FROM_DRAWING)&&(resultCode==RESULT_OK)){
            if (data.getExtras()!=null) {
                byte[] temp = data.getByteArrayExtra("drawing");
                if (temp != null) {
                    bitmap = byteToBitmap(temp);
                    imageView.setImageBitmap(bitmap);
                }
            }
            sign = 1;
        }
    }

    @Override
    public void onBackPressed(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(getString(R.string.cancel));

        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id){}
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

}
