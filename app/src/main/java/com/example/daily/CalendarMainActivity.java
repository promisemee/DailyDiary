package com.example.daily;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.daily.db.AppDatabase;
import com.example.daily.db.DiaryDAO;
import com.example.daily.model.Diary;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedList;
import java.util.List;

import static com.example.daily.DetailDiaryActivity.EXTRA_DIARY_ID;

public class CalendarMainActivity extends AppCompatActivity{

    public static final int NEW_DIARY_ACTIVITY_REQUEST_CODE = 1;
    public static final int DETAIL_DIARY_ACTIVITY_REQUEST_CODE = 2;

    private DiaryDAO mDiaryDAO;

    RecyclerView mRecyclerView;
    DiaryListAdapter mDiaryAdapter;
    List<Diary> mDiaryList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_main);

        mDiaryDAO = Room.databaseBuilder(this, AppDatabase.class, "db-diary")
                .allowMainThreadQueries()
                .build()
                .getDiaryDAO();

        mRecyclerView = findViewById(R.id.recyclerviewCalendar);

        mDiaryAdapter = new DiaryListAdapter(this, mDiaryList);
        mRecyclerView.setAdapter(mDiaryAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        loadDiary();
    }

    private void loadDiary(){
        mDiaryAdapter.updateDiary(mDiaryDAO.getDiaryList());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // really just refreshing the screen after data changes
        if (requestCode == NEW_DIARY_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            loadDiary();
        } else if (requestCode == DETAIL_DIARY_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            loadDiary();
        }
        else{
            Toast.makeText(getApplicationContext(), getString(R.string.sthwrong),Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu.menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_calendar) {
            Intent intent = new Intent(CalendarMainActivity.this, CalendarMainActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
