package com.example.daily;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.daily.db.AppDatabase;
import com.example.daily.db.DiaryDAO;
import com.example.daily.model.Diary;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.example.daily.DetailDiaryActivity.EXTRA_DIARY_ID;

public class CalendarMainActivity extends AppCompatActivity {

    public static final int DETAIL_DIARY_ACTIVITY_REQUEST_CODE = 2;

    private DiaryDAO mDiaryDAO;

    RecyclerView mRecyclerView;
    CalendarListAdapter mCalendarAdapter;
    List<Diary> mDiaryList = new LinkedList<>();
    CalendarView mCalendarView;

    Calendar calendar;

    int mDate, mMonth, mYear;

    TextView emptyRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_main);

        mDiaryDAO = Room.databaseBuilder(this, AppDatabase.class, "db-diary")
                .allowMainThreadQueries()
                .build()
                .getDiaryDAO();

        mRecyclerView = findViewById(R.id.recyclerviewCalendar);

        mCalendarAdapter = new CalendarListAdapter(this, mDiaryList);
        mRecyclerView.setAdapter(mCalendarAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyRecycler = findViewById(R.id.recyclerviewempty);

        calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH)+1;
        mDate = calendar.get(Calendar.DAY_OF_MONTH);

        mCalendarView = findViewById(R.id.calendarView);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String selectedDate = sdf.format(new Date(mCalendarView.getDate()));

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                //pick date from calendar
                mDate = day;
                mMonth = month + 1;
                mYear = year;
                loadDateDiary();
            }
        });

        mCalendarAdapter.setOnItemClickListener(new CalendarListAdapter.ClickListener(){
            @Override
            public void onItemClick(Diary diary) {
                Intent intent = new Intent(CalendarMainActivity.this, DetailDiaryActivity.class);
                int id = diary.getId();
                intent.putExtra(EXTRA_DIARY_ID, id);
                startActivityForResult(intent, DETAIL_DIARY_ACTIVITY_REQUEST_CODE);
            }
        });
        loadDateDiary();
    }

    private void loadDateDiary() {
        mDiaryList = mDiaryDAO.getDiaryListWithDate(mDate, mMonth, mYear);
        Log.i("#TEST", "calendarchange");
        for(int i=0;i<mDiaryList.size();i++){
            Log.i("#TEST", String.valueOf(mDiaryList.get(i).getId()));
        }
        mCalendarAdapter.updateDiary(mDiaryList);
        if (!mDiaryList.isEmpty()){
            emptyRecycler.setVisibility(View.GONE);
        }else{
            emptyRecycler.setVisibility(View.VISIBLE);
            emptyRecycler.setText(getString(R.string.nodiary));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DETAIL_DIARY_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            loadDateDiary();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.sthwrong), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed(){
        setResult(RESULT_OK);
        finish();
    }
}