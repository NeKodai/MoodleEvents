package com.example.scheduleapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.contentcapture.ContentCaptureSession;
import android.widget.TextView;
import android.text.Spanned;

import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 1つの課題の詳細のビュー
 */
public class SubActivity extends AppCompatActivity {

    private TextView untilDeadLineText;
    private Handler handler;
    private Subject aSubject;
    private Timer timer;

    /**
     * ビューの初期設定
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.handler = new Handler();
        this.aSubject = (Subject)getIntent().getSerializableExtra("subject");
        setContentView(R.layout.subject_view);
        TextView title = findViewById(R.id.subject_title);
        TextView course = findViewById(R.id.subject_course);
        TextView startTime = findViewById(R.id.subject_start_time);
        TextView endTime = findViewById(R.id.subject_end_time);
        TextView description = findViewById(R.id.subject_description);
        TextView startOrEnd = findViewById(R.id.start_or_end);
        TextView submit = findViewById(R.id.submit_view);
        this.untilDeadLineText = findViewById(R.id.subject_until_deadline);

        title.setText(aSubject.getTitle());
        course.setText(aSubject.getCourseName());
        String descriptionString = aSubject.getDescription();
        Spanned spannedDescription = HtmlCompat.fromHtml(descriptionString, HtmlCompat.FROM_HTML_MODE_COMPACT);

        //説明のテキストを設定
        if(spannedDescription.toString().equals("")) {
            description.setText("なし");
        }
        else {
            description.setText(spannedDescription);
        }

        //開始時刻と終了時刻のテキストを設定
        if(aSubject.getStartTime()!=null){
            String startTimeText = "開始時刻　" + DayUtility.createDateString(aSubject.getStartTime());
            startTime.setText(startTimeText);
            String endTimeText = "終了時刻　" + DayUtility.createDateString(aSubject.getEndTime());
            endTime.setText(endTimeText);
        }else{
            //開始時刻が定義されていないのでTextViewを消す
            startTime.setVisibility(View.GONE);
            //userイベントなら開始時刻、その他なら提出期限とする
            String aString = (aSubject.getCategoryName().equals("user")) ? "開始時刻　" :"提出期限　";
            String endTimeText = aString + DayUtility.createDateString(aSubject.getEndTime());
            endTime.setText(endTimeText);
        }
        startOrEnd.setText((this.aSubject.isAlreadyStarted()) ? "終了まで　" : "開始まで　");
        setUntilDeadLineText();

        //提出状況のテキストの設定
        if(this.aSubject.getCategoryName().equals("user")){
            submit.setText("ーーー");
        }
        else if(this.aSubject.isSubmit()){
            submit.setText("提出済");
            submit.setTextColor(ContextCompat.getColor(this,R.color.deadLineSafe));
        }else{
            submit.setText("未提出");
            submit.setTextColor(ContextCompat.getColor(this,R.color.deadLineDanger));
        }
    }

    /**
     * 提出期限までの日数のテキストの設定
     */
    private void setUntilDeadLineText(){
        Long currentMillis = System.currentTimeMillis();
        Long targetMillis = this.aSubject.getRepresentativeTime();
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
        this.untilDeadLineText.setTextColor(ContextCompat.getColor(this,R.color.deadLineSafe));
        if(diffDay<=1){
            this.untilDeadLineText.setTextColor(ContextCompat.getColor(this,R.color.deadLineDanger));
        }
        else if(diffDay<=3){
            this.untilDeadLineText.setTextColor(ContextCompat.getColor(this,R.color.deadLineWarning));
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

    /**
     * このアクティビティの読み込みが完了したときの処理
     */
    @Override
    protected void onStart(){
        super.onStart();
        //1秒毎にデータを更新
        this.timer = new Timer();
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
     * このアクティビティから画面が離れた時の処理
     */
    @Override
    protected void onPause(){
        this.timer.cancel();
        super.onPause();
        System.out.println("中断しました");
        return;
    }

}