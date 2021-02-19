package com.example.scheduleapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class SubjectFragment extends Fragment {

    private TextView untilDeadLineText;
    private Handler handler;
    private Subject aSubject;
    private Timer timer;
    private MenuItem garbage ;
    private WebView webView;
    private SubjectFragmentModel model;
    private ScheduleDeleter scheduleDeleter;
    private ProgressDialog progressDialog;
    private Dialog yesNoDialog;

    // Fragmentで表示するViewを作成するメソッド
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // 先ほどのレイアウトをここでViewとして作成します
        return inflater.inflate(R.layout.subject_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Menu menu = ((MainActivity)getContext()).getMenu();
        this.garbage = menu.findItem(R.id.action_garbage);;
        this.handler = new Handler();
        this.aSubject = (Subject) getArguments().getSerializable("subject");
        this.webView = ((MainActivity)getContext()).findViewById(R.id.webView1);
        this.model = new SubjectFragmentModel(this);
        this.progressDialog = new ProgressDialog(this.getContext());
        this.progressDialog.setTitle("課題削除");
        this.progressDialog.setMessage("削除中");
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.yesNoDialog = new AlertDialog.Builder(getContext())
                .setTitle("課題削除")
                .setMessage("この課題を削除しますか")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteSubject();
                    }
                })
                .setNegativeButton("Calcel",null)
                .create();
        //予定表を読み込む
        try {
            this.model.readSchedule(FileUtility.readFile("schedule.json"));
        }catch (IOException anException){
            Toast.makeText(getContext(),"正しく読み込めませんでした",Toast.LENGTH_LONG).show();
        }
        this.scheduleDeleter = new ScheduleDeleter(this.model,this.webView);
        this.webView.addJavascriptInterface(new DeleteEventJsInterface(this.handler,this.scheduleDeleter),"Android3");

        TextView title = view.findViewById(R.id.subject_title);
        TextView course = view.findViewById(R.id.subject_course);
        TextView startTime = view.findViewById(R.id.subject_start_time);
        TextView endTime = view.findViewById(R.id.subject_end_time);
        TextView description = view.findViewById(R.id.subject_description);
        TextView startOrEnd = view.findViewById(R.id.start_or_end);
        TextView submit = view.findViewById(R.id.submit_view);
        this.untilDeadLineText = view.findViewById(R.id.subject_until_deadline);

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
            String startTimeText = "開始時刻　" + DayUtility.createFullDateString(aSubject.getStartTime());
            startTime.setText(startTimeText);
            String endTimeText = "終了時刻　" + DayUtility.createFullDateString(aSubject.getEndTime());
            endTime.setText(endTimeText);
        }else{
            //開始時刻が定義されていないのでTextViewを消す
            startTime.setVisibility(View.GONE);
            //userイベントなら開始時刻、その他なら提出期限とする
            String aString = (aSubject.getCategoryName().equals("user")) ? "開始時刻　" :"提出期限　";
            String endTimeText = aString + DayUtility.createFullDateString(aSubject.getEndTime());
            endTime.setText(endTimeText);
        }
        startOrEnd.setText((this.aSubject.isAlreadyStarted()) ? "終了まで　" : "開始まで　");
        setUntilDeadLineText();

        //提出状況のテキストの設定
        //ゴミ箱をつける
        if(this.aSubject.getCategoryName().equals("user")){
            submit.setText("ーーー");
            this.garbage.setVisible(true);
        }
        else if(this.aSubject.isSubmit()){
            submit.setText("提出済");
            submit.setTextColor(ContextCompat.getColor(getContext(),R.color.deadLineSafe));
        }else{
            submit.setText("未提出");
            submit.setTextColor(ContextCompat.getColor(getContext(),R.color.deadLineDanger));
        }

        getActivity().setTitle(R.string.subject);
    }

    public void pushGarbageButton(){
        this.yesNoDialog.show();
    }

    /**
     * この課題を削除する
     */
    public void deleteSubject(){
        System.out.println("削除");
        this.yesNoDialog.dismiss();
        this.progressDialog.show();
        this.scheduleDeleter.deleteCalendarEvent(this.aSubject);
    }

    /**
     * イベントの作成に成功した時の処理
     */
    public void notifySuccessDelete(){
        this.progressDialog.dismiss();
        this.model.deleteSubjectById(this.aSubject.getId());
        try {
            FileUtility.writeFile("schedule.json", this.model.getJsonSchedule());
            Toast.makeText(this.getContext(), "削除しました。", Toast.LENGTH_SHORT).show();
            if(getActivity()!=null) getActivity().getSupportFragmentManager().popBackStack();
        }catch (IOException anException){
            anException.printStackTrace();
            Toast.makeText(this.getContext(),"正しく書き込めませんでした",Toast.LENGTH_LONG).show();
            this.failedDeleteEvent();
        }
    }

    /**
     * スケジュールの更新に失敗した場合の処理
     */
    public void failedDeleteEvent(){
        this.progressDialog.dismiss();
        Toast.makeText(this.getContext(), "削除に失敗しました。", Toast.LENGTH_LONG).show();
        return;
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
        this.untilDeadLineText.setTextColor(ContextCompat.getColor(getContext(),R.color.deadLineSafe));
        if(diffDay<=1){
            this.untilDeadLineText.setTextColor(ContextCompat.getColor(getContext(),R.color.deadLineDanger));
        }
        else if(diffDay<=3){
            this.untilDeadLineText.setTextColor(ContextCompat.getColor(getContext(),R.color.deadLineWarning));
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
     * このアクティビティの読み込みが完了したときの処理
     */
    @Override
    public void onStart(){
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
     * このフラグメントから画面が離れた時の処理
     */
    @Override
    public void onPause(){
        this.timer.cancel();
        this.progressDialog.dismiss();
        this.yesNoDialog.dismiss();
        super.onPause();
        System.out.println("中断しました");
        return;
    }

    /**
     * このフラグメントが破棄された時
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        this.garbage.setVisible(false);
    }


}
