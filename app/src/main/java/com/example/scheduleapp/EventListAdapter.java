package com.example.scheduleapp;

import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    private List<Subject> eventList;
    private Model aModel;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        private TextView title;
        private TextView deadLine;
        private TextView course;

        ViewHolder(View v) {
            super(v);
            this.title = (TextView)v.findViewById(R.id.title_view);
            this.deadLine = (TextView)v.findViewById(R.id.deadLine_view);
            this.course = (TextView)v.findViewById((R.id.course_view));
        }
    }

    // Provide a suitable constructor (depends on the kind of subjectList)
    EventListAdapter(Model aModel) {
        this.aModel = aModel;
        this.eventList = aModel.getScheduleList();
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);

        // set the view's size, margins, paddings and layout parameters
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                //処理はonItemClick()に丸投げ
                onItemClick(view, position, eventList.get(position));
            }
        });
        return holder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // - get element from your subjectList at this position
        // - replace the contents of the view with that element
        holder.title.setText(this.eventList.get(position).getTitle());
        setDeadLineString(holder.deadLine,position);
        holder.course.setText(this.eventList.get(position).getCourseName());
    }

    // Return the size of your subjectList (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.eventList.size();
    }


    /**
     * 指定された課題の締め切りまでの時間を文字列にして応答する
     * @param position 課題のインデックス番号
     * @return 締め切りまでを表す文字列
     */
    private void setDeadLineString(TextView deadLineTextView,Integer position){
        Long currentMillis = System.currentTimeMillis();
        Long targetMillis = this.eventList.get(position).getCalendar().getTimeInMillis();
        Long diffMillis = targetMillis-currentMillis;
        // ミリ秒から秒へ変換
        Long diffSeconds = diffMillis / 1000;
        //秒から分へ
        Long diffMinute = diffSeconds / 60;
        Long diffHour = diffMinute/60;
        Long diffDay = diffHour / 24;
        if (diffMinute==0 && diffHour == 0){
            deadLineTextView.setText("あと"+diffSeconds+"秒");
            deadLineTextView.setTextColor(Color.RED);
        }
        else if(diffHour==0){
            deadLineTextView.setText("あと"+diffMinute+"分");
            deadLineTextView.setTextColor(Color.RED);
        }
        else if(diffDay==0){
            deadLineTextView.setText("あと"+diffHour+"時間"+diffMinute%(60*diffHour)+"分");
            deadLineTextView.setTextColor(Color.RED);
        }
        else if(diffDay<=1){
            deadLineTextView.setText("あと" + diffDay + "日");
            deadLineTextView.setTextColor(Color.RED);
        }
        else if(diffDay<=3){
            deadLineTextView.setText("あと" + diffDay + "日");
            deadLineTextView.setTextColor(Color.parseColor("#FF9900"));
        }
        else {
            deadLineTextView.setText("あと" + diffDay + "日");
            deadLineTextView.setTextColor(Color.parseColor("#008D56"));
        }
        return;
    }

    /**
     * クリックした際の処理。オーバライドして定義
     * @param view view
     * @param position 選択したインデックス番号
     * @param aSubject イベントオブジェクト
     */
    protected void onItemClick(View view, Integer position, Subject aSubject){

    }
}