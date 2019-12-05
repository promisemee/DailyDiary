package com.example.daily;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.daily.db.AppDatabase;
import com.example.daily.db.DiaryDAO;
import com.example.daily.model.Diary;

public class DetailDiaryActivity extends AppCompatActivity {

    public static String EXTRA_DIARY_ID = "diary_id";

    private DiaryDAO mDiaryDAO;
    private Diary diary;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_diary);
        mDiaryDAO = Room.databaseBuilder(this, AppDatabase.class, "db-diary")
                .allowMainThreadQueries()   //Allows room to do operation on main thread
                .build()
                .getDiaryDAO();

        ImageView img = findViewById(R.id.detailImage);
        TextView txt = findViewById(R.id.dateText);

        diary = mDiaryDAO.getDiaryWithId(getIntent().getIntExtra(EXTRA_DIARY_ID, -1));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu.menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.menu.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.update) {
            Intent intent = new Intent(DetailDiaryActivity.this, CalendarMainActivity.class);
            startActivity(intent);
        }

        if (id == R.id.delete) {
            mDiaryDAO.delete(diary);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
