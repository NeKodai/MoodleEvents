package com.example.scheduleapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.text.Spanned;

import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 1つの課題の詳細の見た目を管理するクラス
 */
public class SubActivity extends AppCompatActivity {

    private Calendar aCalendar;
    private TextView untilDeadLineText;
    private Handler handler;

    /**
     * ビューの初期設定
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.handler = new Handler();
        Subject aSubject = (Subject)getIntent().getSerializableExtra("subject");
        setContentView(R.layout.subject_view);
        TextView title = findViewById(R.id.subject_title);
        TextView course = findViewById(R.id.subject_course);
        TextView deadLine = findViewById(R.id.subject_deadline);
        TextView description = findViewById(R.id.subject_description);
        this.untilDeadLineText = findViewById(R.id.subject_until_deadline);

        title.setText(aSubject.getTitle());
        course.setText(aSubject.getCourseName());
        String descriptionString = aSubject.getDescription();
        Spanned spannedDescription = HtmlCompat.fromHtml(descriptionString, HtmlCompat.FROM_HTML_MODE_COMPACT);

        if(spannedDescription.toString().equals("")) {
            description.setText("なし");
        }
        else {
            description.setText(spannedDescription);
        }
        this.aCalendar = aSubject.getCalendar();
        String deadLineText = String.format(Locale.US,"提出期限 %d年%d月%d日 %d時%d分",
                this.aCalendar.get(Calendar.YEAR),this.aCalendar.get(Calendar.MONTH)+1,this.aCalendar.get(Calendar.DATE),
                this.aCalendar.get(Calendar.HOUR_OF_DAY),this.aCalendar.get(Calendar.MINUTE));
        deadLine.setText(deadLineText);
        setUntilDeadLineText();
    }

    @Override
    protected void onStart(){
        super.onStart();
        //1秒毎にデータを更新
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setUntilDeadLineText();
                    }
                });
            }
        },0,1000);
    }

    /**
     * 提出期限までの日数のテキストの設定
     */
    private void setUntilDeadLineText(){
        Long currentMillis = System.currentTimeMillis();
        Long targetMillis = this.aCalendar.getTimeInMillis();
        Long diffMillis = targetMillis-currentMillis;
        // ミリ秒から秒へ変換
        Long diffSeconds = diffMillis / 1000;
        //秒から分へ
        Long diffMinute = diffSeconds / 60;
        //分から時間へ
        Long diffHour = diffMinute/60;
        //時間から日付へ
        Long diffDay = diffHour / 24;

        Integer hour = Long.valueOf(diffHour%24).intValue();
        Integer minute = Long.valueOf(diffMinute%60).intValue();
        Integer seconds = Long.valueOf(diffSeconds%60).intValue();

        StringBuilder aBuilder = new StringBuilder();
        this.untilDeadLineText.setTextColor(Color.parseColor("#008D56"));
        if(diffDay<=1){
            this.untilDeadLineText.setTextColor(Color.RED);
        }
        else if(diffDay<=3){
            this.untilDeadLineText.setTextColor(Color.parseColor("#FF9900"));
        }
        if(diffSeconds < 0){
            aBuilder.append("終了");
        }else {
            if (diffDay != 0) {
                aBuilder.append(diffDay);
                aBuilder.append("日と");
            }
            if(hour != 0) {
                aBuilder.append(hour);
                aBuilder.append("時間");
            }
            if(diffMinute!=0) {
                aBuilder.append(minute);
            }
            if(diffMinute!=0){
                aBuilder.append("分");
            }
            aBuilder.append(seconds);
            aBuilder.append("秒");
        }
        this.untilDeadLineText.setText(new String(aBuilder));
        return;
    }

    /**
     * 戻るボタンを押した時の処理
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

}