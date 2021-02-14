package com.example.scheduleapp;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    private List<Subject> eventList;
    private Model model;
    private Activity activity;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        private TextView title;
        private TextView deadLine;
        private TextView course;
        private ImageView submitState;

        ViewHolder(View v) {
            super(v);
            this.title = (TextView)v.findViewById(R.id.title_view);
            this.deadLine = (TextView)v.findViewById(R.id.deadLine_view);
            this.course = (TextView)v.findViewById((R.id.course_view));
            this.submitState = (ImageView)v.findViewById(R.id.submit_color_view);
        }
    }

    // Provide a suitable constructor (depends on the kind of subjectList)
    EventListAdapter(Model model,Activity activity) {
        this.model = model;
        this.eventList = new ArrayList<Subject>();
        this.activity = activity;
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
        holder.course.setText(this.eventList.get(position).getCourseName());
        this.setSubmitColor(holder.submitState,this.eventList.get(position));
        this.setDeadLineString(holder.deadLine,position);

    }

    // Return the size of your subjectList (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.eventList.size();
    }

    /**
     * 提出しているかのイメージビューの色
     * @param subject 課題データ
     */
    private void setSubmitColor(ImageView submitView , Subject subject){
        Integer color = 0;
        if(subject.getCourseName().equals("userイベント") || subject.isSubmit() == null){
            color = ContextCompat.getColor(this.activity,R.color.deadLineEnded);
        }
        else if(subject.isSubmit()){
            color = ContextCompat.getColor(this.activity,R.color.deadLineSafe);
        }
        else{
            color = ContextCompat.getColor(this.activity,R.color.deadLineDanger);
        }
        submitView.setColorFilter(color,android.graphics.PorterDuff.Mode.SRC_IN);
    }

    /**
     * 指定された課題の締め切りまでの時間を文字列にして応答する
     * @param position 課題のインデックス番号
     * @return 締め切りまでを表す文字列
     */
    private void setDeadLineString(TextView deadLineTextView,Integer position){
        Long currentMillis = System.currentTimeMillis();
        Long targetMillis = this.eventList.get(position).getRepresentativeTime();
        StringBuilder builder = new StringBuilder();
        boolean isEnded = false;
        Long diffMillis = targetMillis-currentMillis;
        if(diffMillis<0){
            isEnded = true;
            diffMillis*=-1;
        }
        // ミリ秒から秒へ変換
        Long diffSeconds = diffMillis / 1000;
        //秒から分へ
        Long diffMinutes = diffSeconds / 60;
        Long diffHour = diffMinutes/60;
        Long diffDay = diffHour / 24;

        if(!isEnded){
            builder.append("あと");
            deadLineTextView.setTextColor(this.getDeadLineColor(diffDay));
        }else{
            deadLineTextView.setTextColor(ContextCompat.getColor(this.activity,R.color.deadLineEnded));
        }
        if (diffMinutes==0 && diffHour == 0){
            builder.append(diffSeconds);
            builder.append("秒");
        }
        else if(diffHour==0){
            builder.append(diffMinutes);
            builder.append("分");
        }
        else if(diffDay==0){
            builder.append(diffHour);
            builder.append("時間");
            builder.append(diffMinutes%(60*diffHour));
            builder.append("分");
        }
        else {
            builder.append(diffDay);
            builder.append("日");
        }

        if(isEnded)builder.append("前");

        deadLineTextView.setText(new String(builder));
        return;
    }

    /**
     * 締め切りまでの時間の文字の色を返す
     * @param diffDay 今の時間と締め切りまでの差（日）
     * @return 色を表す整数
     */
    private int getDeadLineColor(Long diffDay){
        if(diffDay>3){
            return ContextCompat.getColor(this.activity,R.color.deadLineSafe);
        }
        else if(diffDay>1){
            return ContextCompat.getColor(this.activity,R.color.deadLineWarning);
        }
        return ContextCompat.getColor(this.activity, R.color.deadLineDanger);
    }

    /**
     * モデルのデータを取得し、このアダプタのリストを更新
     */
    public void modelDataUpdate(){
        this.eventList.clear();
        this.eventList.addAll(this.model.getScheduleList());
        this.notifyDataSetChanged();
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