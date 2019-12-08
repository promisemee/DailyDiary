package com.example.daily;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.daily.db.AppDatabase;
import com.example.daily.db.DiaryDAO;
import com.example.daily.model.Diary;

public class DetailDiaryActivity extends DiaryActivity {

    public static String EXTRA_DIARY_ID;

    public static final int UPDATE_DIARY_ACTIVITY_REQUEST_CODE = 0;

    private DiaryDAO mDiaryDAO;
    private Diary mCurrent;
    TextView txt;
    ImageView img;

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_diary);
        mDiaryDAO = Room.databaseBuilder(this, AppDatabase.class, "db-diary")
                .allowMainThreadQueries()   //Allows room to do operation on main thread
                .build()
                .getDiaryDAO();

        position = getIntent().getIntExtra(EXTRA_DIARY_ID, 0);
        img = findViewById(R.id.detailImage);
        txt = findViewById(R.id.detailText);

        loadDetail();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // really just refreshing the screen after data changes
        if ((requestCode == UPDATE_DIARY_ACTIVITY_REQUEST_CODE) && (resultCode == RESULT_OK)) {
            loadDetail();
        } else{
            Toast.makeText(getApplicationContext(), getString(R.string.sthwrong),Toast.LENGTH_SHORT).show();
        }
        loadDetail();
    }

    private void loadDetail(){
        mCurrent = mDiaryDAO.getDiaryWithId(position);
        if (mCurrent==null){
            setResult(RESULT_CANCELED);
        }else {
            if(mCurrent.getImg()!=null)
                img.setImageBitmap(byteToBitmap(mCurrent.getImg()));
            else {
                img.setImageBitmap(null);
                img .setVisibility(View.GONE);
            }
            txt.setText(mCurrent.getContext());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu.menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.update) {
            Intent intent = new Intent(DetailDiaryActivity.this, UpdateDiaryActivity.class);
            intent.putExtra(UpdateDiaryActivity.EXTRA_DATA_ID, position);
            startActivityForResult(intent,UPDATE_DIARY_ACTIVITY_REQUEST_CODE);
        }

        if (id == R.id.delete) {
            mDiaryDAO.delete(mCurrent);
            setResult(RESULT_OK);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        setResult(RESULT_OK);
        finish();
    }
}
