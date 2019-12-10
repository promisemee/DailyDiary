package com.example.daily;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.daily.db.AppDatabase;
import com.example.daily.db.DiaryDAO;

public class SettingsActivity extends AppCompatActivity {

    private DiaryDAO mDiaryDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);

        mDiaryDAO = Room.databaseBuilder(this, AppDatabase.class, "db-diary")
                .allowMainThreadQueries()   //Allows room to do operation on main thread
                .build()
                .getDiaryDAO();

        Button deleteAll = findViewById(R.id.deleteText);
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);

                builder.setMessage(getString(R.string.deleteAll));

                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        mDiaryDAO.deleteAll();
                    }
                });

                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        //Does nothing!
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

}
