package com.example.daily;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.daily.model.Diary;

public class CalendarListAdapter extends RecyclerView.Adapter<CalendarListAdapter.ViewHolder> {
    private final LayoutInflater mInflater;
    private List<Diary> mDiaryList;
    private static ClickListener clickListener;
    private Context context;

    CalendarListAdapter(Context context, List<Diary> diaryList)
    {
        mInflater = LayoutInflater.from(context);
        this.mDiaryList = diaryList;
        this.context = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateView;
        private final ImageView imageView;
        private final TextView contentView;

        private ViewHolder(View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.calendarDate);
            imageView = itemView.findViewById(R.id.calendarImage);
            contentView = itemView.findViewById(R.id.calendarText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        Diary diary = mDiaryList.get(position);
                        clickListener.onItemClick(diary);
                    }

                }
            });

        }
    }

    @Override
    @NonNull
    // read in the description of the layout of a single item in the list
    // use it to create a ViewHolder
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.calendar_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    @NonNull
    // add String data to a ViewHolder
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mDiaryList != null) {
            Diary mCurrent = getDiaryAtPosition(position);
            TextView dateView = holder.dateView;
            TextView contentView = holder.contentView;
            ImageView imageView = holder.imageView;

            imageView.setImageBitmap(null);
            Bitmap bmp;
            byte[] bytes = mCurrent.getImg();
            if (bytes!=null){
                bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bmp);
            }

            String date = mCurrent.getDayOfMonth()+"."+mCurrent.getMonth()+"."+mCurrent.getYear()%100;

            dateView.setText(date);
            contentView.setText(cutString(mCurrent.getContext()));

            cutString(mCurrent.getContext());

        }
    }

    public String cutString(String temp){
        String cut = temp;
        if (temp.length()>=20){
            cut = temp.substring(0,20)+"..."+"\n"+context.getString(R.string.seemore);
        }
        return cut+"\n";
    }


    // getItemCount() is called many times, and when it is first called,
    // mWordList has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mDiaryList != null)
            return mDiaryList.size();
        else return 0;
    }

    public void updateDiary(List<Diary> d){
        this.mDiaryList = d;
        notifyDataSetChanged();
    }

    private Diary getDiaryAtPosition(int position) {
        return mDiaryList.get(position);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        CalendarListAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(Diary diary);
    }


}
