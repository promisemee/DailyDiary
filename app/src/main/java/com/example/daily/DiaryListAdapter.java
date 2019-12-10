package com.example.daily;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.example.daily.model.Diary;

public class DiaryListAdapter extends RecyclerView.Adapter<DiaryListAdapter.ViewHolder> {
    private final LayoutInflater mInflater;
    private List<Diary> mDiaryList;
    private static ClickListener clickListener;
    private Context context;

    DiaryListAdapter(Context context, List<Diary> diaryList)
    {
        mInflater = LayoutInflater.from(context);
        this.context = context;
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.diary_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    @NonNull
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mDiaryList != null) {
            Diary mCurrent = getDiaryAtPosition(position);
            TextView dateView = holder.dateView;
            TextView contentView = holder.contentView;
            ImageView imageView = holder.imageView;

            imageView.setImageBitmap(null);
            imageView.setVisibility(View.GONE);
            byte[] bytes = mCurrent.getImg();
            if (bytes!=null){
                imageView.setVisibility(View.VISIBLE);
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bmp);
            }

            String date = mCurrent.getDayOfMonth()+"."+mCurrent.getMonth()+"."+mCurrent.getYear();

            dateView.setText(date);
            contentView.setText(cutString(mCurrent.getContext()));

            cutString(mCurrent.getContext());
        }
    }


    public String cutString(String temp){
        String cut = temp;
        if (temp.length()>=100){
            cut = temp.substring(0,100)+"..."+"\n\n"+context.getString(R.string.seemore);
        }
        return cut+"\n";
    }

    @Override
    public int getItemCount() {
        if (mDiaryList != null)
            return mDiaryList.size();
        else return 0;
    }

    public void updateDiary(List<Diary> d){
        Collections.sort(d, new sortByDate());
        //Collections.sort(d, new sortById());
        this.mDiaryList = d;
        notifyDataSetChanged();
    }

    private Diary getDiaryAtPosition(int position) {
        return mDiaryList.get(position);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        DiaryListAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(Diary diary);
    }
    class sortByDate implements Comparator<Diary> {
        @Override
        public int compare(Diary a, Diary b){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date date1 = null;
            Date date2 = null;
            try {
                String string1 = a.getYear()+"/"+a.getMonth()+"/"+a.getDayOfMonth();
                String string2 = b.getYear()+"/"+b.getMonth()+"/"+b.getDayOfMonth();
                date1 = sdf.parse(string1);
                date2 = sdf.parse(string2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date2.compareTo(date1)==0){
                return b.getId() - a.getId();
            }

            return date2.compareTo(date1);
        }
    }

    class sortById implements Comparator<Diary>{
        @Override
        public int compare(Diary a, Diary b){
            return a.getId()-b.getId();
        }
    }
}
