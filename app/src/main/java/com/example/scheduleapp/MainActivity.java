package com.example.scheduleapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * メインスレッド
 */
public class MainActivity extends AppCompatActivity {

    private EventListAdapter rAdapter;
    private ScheduleGetter aScheduleGetter;
    private Controller controller;
    private Model model;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView noScheduleText;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //レイアウトの読み込み開始
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        WebView aWebView = (WebView) findViewById(R.id.webView1);
        this.recyclerView = findViewById(R.id.recyclerView1);
        this.noScheduleText  =findViewById(R.id.no_schedule_text);
        //レイアウト読み込みここまで
        setSupportActionBar(toolbar);
        //ファイルユーティリティ初期化
        FileUtility.initialize(getApplicationContext());
        this.model = new Model(this,new Handler());
        this.aScheduleGetter = new ScheduleGetter(this.model,aWebView);
        aWebView.addJavascriptInterface(new JsInterface(this,this.model,this.aScheduleGetter),"Android");
        this.controller = new Controller();
        this.controller.initialize(this,aScheduleGetter);

        //RecyclerViewの設定
        this.recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(rLayoutManager);
        this.rAdapter = new EventListAdapter(this.model){
            @Override
            protected void onItemClick(View view, Integer position, Subject aSubject){
                controller.setSubActivity(aSubject);
            }
        };
        this.recyclerView.setAdapter(this.rAdapter);

        //スワイプリフレッシュリスナの更新
        this.swipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        this.swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                controller.updateSchedule();
            }
        });

        //予定表を読み込む
        try {
            this.model.readSchedule(FileUtility.readFile("schedule.json"));
        }catch (IOException anException){
            Toast.makeText(this,"正しく読み込めませんでした",Toast.LENGTH_LONG).show();
        }
        return;
    }

    /**
     * ビューの依存物に対して更新通知をする
     */
    public void update(){
        this.rAdapter.notifyDataSetChanged();
        if(this.model.getScheduleList().isEmpty()){
            this.noScheduleText.setText("課題がありません。\n下にスワイプして更新してください。");
        }
        else{
            this.noScheduleText.setText("");
        }
        return;
    }

    /**
     * シケジュールの更新が終了したことを通知
     */
    public void notifyFinCalendarUpdate() {
        this.swipeRefreshLayout.setRefreshing(false);
        //スケジュールの内容を保存する
        try {
            FileUtility.writeFile("schedule.json", this.model.getJsonSchedule());
            Toast.makeText(this, "更新しました。", Toast.LENGTH_SHORT).show();
        }catch (IOException anException){
            anException.printStackTrace();
            Toast.makeText(this,"正しく書き込めませんでした",Toast.LENGTH_LONG).show();
            this.failedCalendarUpdate();
        }
        return;
    }

    /**
     * スケジュールの更新に失敗した場合の処理
     */
    public void failedCalendarUpdate(){
        this.swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(this, "更新に失敗しました。", Toast.LENGTH_LONG).show();
        return;
    }

    @Override
    protected void onStart(){
        super.onStart();
        //1秒毎にデータを更新
        this.timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                model.scheduleUpdate();
            }
        },0,1000);
        return;

    }

    @Override
    protected void onPause(){
        this.timer.cancel();
        super.onPause();
        System.out.println("中断しました");
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            this.controller.setSettingActivity();
            return true;
        }
        else if(id == R.id.action_reset){
            File aFile = new File(getFilesDir(),"schedule.json");
            if(aFile.exists()){
                aFile.delete();
                Toast.makeText(this, "リセットしました", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
