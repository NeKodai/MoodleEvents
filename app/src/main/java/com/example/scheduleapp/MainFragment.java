package com.example.scheduleapp;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 課題一覧のフラグメント
 */
public class MainFragment extends Fragment {

    private EventListAdapter rAdapter;
    private ScheduleGetter aScheduleGetter;
    private Controller controller;
    private MainModel model;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView noScheduleText;
    private Timer timer;
    private Handler handler;

    /**
     * Fragmentで表示するViewを作成するメソッド
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return ビュー
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // 先ほどのレイアウトをここでViewとして作成します
        return inflater.inflate(R.layout.content_main, container, false);
    }

    /**
     * Viewが生成し終わった時に呼ばれるメソッド
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.event_list);
        WebView aWebView = ((MainActivity)getContext()).getWebView();
        this.recyclerView = view.findViewById(R.id.recyclerView1);
        this.noScheduleText  =view.findViewById(R.id.no_schedule_text);
        this.handler = new Handler();
        FileUtility.initialize(getActivity().getApplicationContext());

        this.model = new MainModel(this);
        this.aScheduleGetter = new ScheduleGetter(this.model,aWebView);
        aWebView.addJavascriptInterface(new MainJsInterface(this.handler,this.model,this.aScheduleGetter),"Android");
        this.controller = new Controller();
        this.controller.initialize(this,aScheduleGetter);

        //RecyclerViewの設定
        this.recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(this.getContext());
        this.recyclerView.setLayoutManager(rLayoutManager);
        this.rAdapter = new EventListAdapter(this.model,this.getActivity()){
            @Override
            protected void onItemClick(View view, Integer position, Subject aSubject){
                SubjectFragment subjectFragment = new SubjectFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                args.putSerializable("subject",aSubject);
                subjectFragment.setArguments(args);
                transaction.replace(R.id.container,subjectFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        };
        this.recyclerView.setAdapter(this.rAdapter);
//
        //スワイプリフレッシュリスナの更新
        this.swipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
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
            this.update();
        }catch (IOException anException){
            Toast.makeText(getContext(),"正しく読み込めませんでした",Toast.LENGTH_LONG).show();
        }
        this.update();
        return;
    }

    /**
     * ビューの依存物に対して更新通知をする
     */
    public void update(){
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                rAdapter.modelDataUpdate();
                if(model.getScheduleList().isEmpty()){
                    noScheduleText.setText(R.string.no_schedule_text);
                }
                else{
                    noScheduleText.setText("");
                }
            }
        });
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
            Toast.makeText(this.getContext(), "更新しました。", Toast.LENGTH_SHORT).show();
        }catch (IOException anException){
            anException.printStackTrace();
            Toast.makeText(this.getContext(),"正しく書き込めませんでした",Toast.LENGTH_LONG).show();
            this.failedCalendarUpdate();
        }
        return;
    }

    /**
     * スケジュールの更新に失敗した場合の処理
     */
    public void failedCalendarUpdate(){
        this.swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(this.getContext(), "更新に失敗しました。", Toast.LENGTH_LONG).show();
        return;
    }

    /**
     * このアクティビティの読み込みが完了したときの処理
     */
    @Override
    public void onStart(){
        super.onStart();
       // 1秒毎にデータを更新
        this.timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        rAdapter.notifyDataSetChanged();
                    }
                });
            }
        },0,1000);
        return;
    }

    /**
     * このアクティビティから画面が離れた時の処理
     */
    @Override
    public void onPause(){
        this.timer.cancel();
        super.onPause();
        System.out.println("中断しました");
        return;
    }

}