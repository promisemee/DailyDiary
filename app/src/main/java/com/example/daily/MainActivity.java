package com.example.daily;

import android.content.Intent;
import android.os.Bundle;

import com.example.daily.db.AppDatabase;
import com.example.daily.db.DiaryDAO;
import com.example.daily.model.Diary;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import static com.example.daily.DetailDiaryActivity.EXTRA_DIARY_ID;

public class MainActivity extends AppCompatActivity{

    public static final int NEW_DIARY_ACTIVITY_REQUEST_CODE = 1;
    public static final int DETAIL_DIARY_ACTIVITY_REQUEST_CODE = 2;
    private static final int SETTING_ACTIVITY_REQUEST_CODE = 3;

    private DiaryDAO mDiaryDAO;

    RecyclerView mRecyclerView;
    DiaryListAdapter mDiaryAdapter;
    List<Diary> mDiaryList = new LinkedList<>();

    TextView emptyRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDiaryDAO = Room.databaseBuilder(this, AppDatabase.class, "db-diary")
                .allowMainThreadQueries()
                .build()
                .getDiaryDAO();

        mRecyclerView = findViewById(R.id.recyclerview);

        mDiaryList = mDiaryDAO.getDiaryList();
        mDiaryAdapter = new DiaryListAdapter(this, mDiaryList);
        mRecyclerView.setAdapter(mDiaryAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyRecycler = findViewById(R.id.emptyrecycler);

        //Button for New Diary
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddDiaryActivity.class);
                startActivityForResult(intent, NEW_DIARY_ACTIVITY_REQUEST_CODE);
            }
        });

        //Click to Get Detail of Diary
        mDiaryAdapter.setOnItemClickListener(new DiaryListAdapter.ClickListener(){
            @Override
            public void onItemClick(Diary diary) {
                Intent intent = new Intent(MainActivity.this, DetailDiaryActivity.class);
                int id = diary.getId();
                intent.putExtra(EXTRA_DIARY_ID, id);
                startActivityForResult(intent, DETAIL_DIARY_ACTIVITY_REQUEST_CODE);
            }
        });

        loadDiary();
    }

    private void loadDiary(){
        mDiaryList = mDiaryDAO.getDiaryList();
        if (!mDiaryList.isEmpty()){
            emptyRecycler.setVisibility(View.GONE);
        }else {
            emptyRecycler.setVisibility(View.VISIBLE);
            emptyRecycler.setText(getString(R.string.nodiary));
        }
        mDiaryAdapter.updateDiary(mDiaryList);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadDiary();
            if (requestCode==NEW_DIARY_ACTIVITY_REQUEST_CODE){
                mRecyclerView.smoothScrollToPosition(0);
            }
        }else if(requestCode==SETTING_ACTIVITY_REQUEST_CODE){
            loadDiary();
        }else if(resultCode == RESULT_CANCELED){
            ;
        }else{
            Toast.makeText(getApplicationContext(), getString(R.string.sthwrong),Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_calendar) {
            Intent intent = new Intent(MainActivity.this, CalendarMainActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(intent, SETTING_ACTIVITY_REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
