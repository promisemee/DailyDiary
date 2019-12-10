package com.example.daily;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.daily.db.DiaryDAO;
import com.example.daily.model.Diary;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import static com.example.daily.DrawActivity.EXTRA_OUTPUT;

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
    Uri photoUri;
    String filepath;
    String mCurrentPhotoPath;
    File file;


    protected Calendar calendar = Calendar.getInstance();
    int mYear = calendar.get(Calendar.YEAR);
    int mMonth = calendar.get(Calendar.MONTH)+1;
    int mDate = calendar.get(Calendar.DAY_OF_MONTH);
    SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy");

    protected void getFromAlbum(){
        Intent intent = new Intent();
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    protected void getFromCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager())!=null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            }catch(IOException e){
                Log.i("#TEST", e.toString());
            }

            if (photoFile!=null){
                photoUri = FileProvider.getUriForFile(this, getPackageName(),photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            }
        }
    }

    protected void getFromDrawing(){
        Intent intent = new Intent(this, DrawActivity.class);
        if (intent.resolveActivity(getPackageManager())!=null){
            try{
                file = createImageFile();
                filepath = file.getAbsolutePath();
                intent.putExtra(EXTRA_OUTPUT, filepath);
                startActivityForResult(intent, PICK_FROM_DRAWING);
            }catch(IOException e){};

        }
    }

    protected File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "JPEG_"+timeStamp+"_";
        File imageFile = null;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(!storageDir.exists()){
            storageDir.mkdirs();
        }
        imageFile = File.createTempFile(fileName, ".jpg", storageDir);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode==PICK_FROM_CAMERA)&&(resultCode==RESULT_OK)){
            try{
                imageView.setImageURI(photoUri);
            }catch(Exception e){Log.i("TEST", e.toString());}
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
            Bitmap bitmap = BitmapFactory.decodeFile(filepath);
            imageView.setImageBitmap(bitmap);
            sign = 1;
        }
    }

    protected Bitmap byteToBitmap(byte[] byteArray){
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        return bitmap;
    }

    protected byte[] bitmapToByte(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 60, stream);
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
