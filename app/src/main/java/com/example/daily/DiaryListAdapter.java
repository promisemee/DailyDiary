package com.example.daily;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.daily.model.Diary;

public class DiaryListAdapter extends RecyclerView.Adapter<DiaryListAdapter.ViewHolder> {
    private final LayoutInflater mInflater;
    private List<Diary> mDiaryList;
    private static ClickListener clickListener;

    DiaryListAdapter(Context context, List<Diary> diaryList)
    {
        mInflater = LayoutInflater.from(context);
        this.mDiaryList = diaryList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateView;
        private final ImageView imageView;
        private final TextView contentView;

        private ViewHolder(View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.dateText);
            imageView = itemView.findViewById(R.id.contentImage);
            contentView = itemView.findViewById(R.id.contentText);

        }
    }

    @Override
    // read in the description of the layout of a single item in the list
    // use it to create a ViewHolder
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.diary_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    // add String data to a ViewHolder
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mDiaryList != null) {
            Diary mCurrent = getDiaryAtPosition(position);
            TextView dateView = holder.dateView;
            TextView contentView = holder.contentView;
            ImageView imageView = holder.imageView;

            byte[] bytes = mCurrent.getImg();
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            dateView.setText(mCurrent.getDate());
            contentView.setText(mCurrent.getContext());
            imageView.setImageBitmap(bmp);
        }else{
        }
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

    // Gets the word at a given position.
    // This method is useful for identifying which word
    // was clicked in methods that handle user events.
    private Diary getDiaryAtPosition(int position) {
        return mDiaryList.get(position);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        DiaryListAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(Diary diary);
    }

    public Diary getDiaryItem(int index){
        return mDiaryList.get(index);
    }

}
