package com.example.scheduleapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;
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
    private MainModel model;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView noScheduleText;
    private Timer timer;
    private Handler handler;
    private LinearLayout upperMenu;
    private AlertDialog sortDialog;
    private UserStatus user;
    private Boolean isUpdating = false;

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
        FileUtility.initialize(getActivity().getApplicationContext());
        this.recyclerView = view.findViewById(R.id.recyclerView1);
        this.noScheduleText  =view.findViewById(R.id.no_schedule_text);
        this.upperMenu = view.findViewById(R.id.event_list_upper);
        this.handler = new Handler();
        this.user = new UserStatus();
        this.user.readUserStatus();
        this.selectEventSortDialog();


        this.model = new MainModel(this,this.user);
        this.aScheduleGetter = new ScheduleGetter(this.model,aWebView);
        aWebView.addJavascriptInterface(new MainJsInterface(this.handler,this.model,this.aScheduleGetter),"Android");

        //RecyclerViewの設定
        this.recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(this.getContext());
        this.recyclerView.setLayoutManager(rLayoutManager);
        this.rAdapter = new EventListAdapter(this.model,this.getActivity()){
            @Override
            protected void onItemClick(View view, Integer position, Subject aSubject){
                if(!isUpdating) {
                    SubjectFragment subjectFragment = new SubjectFragment();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Bundle args = new Bundle();
                    args.putSerializable("subject", aSubject);
                    subjectFragment.setArguments(args);
                    transaction.replace(R.id.container, subjectFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                else{
                    Toast.makeText(getContext(),"更新中です。しばらくお待ちください",Toast.LENGTH_SHORT).show();
                }
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
                isUpdating = true;
                ((MainActivity)getContext()).setDrawerLock(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                aScheduleGetter.loadMoodle();
            }
        });

        this.upperMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(!isUpdating) sortDialog.show();
            }
        });

        //予定表を読み込む
        try {
            this.model.readSchedule(FileUtility.readFile("schedule.json"));
            this.update();
        }catch (IOException anException){
            //Toast.makeText(getContext(),"正しく読み込めませんでした",Toast.LENGTH_LONG).show();
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
        this.isUpdating = false;
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                ((MainActivity)getContext()).setDrawerLock(DrawerLayout.LOCK_MODE_UNLOCKED);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        //スケジュールの内容を保存する
        try {
            FileUtility.writeFile("schedule.json", this.model.getJsonSchedule());
            Toast.makeText(this.getContext(), "更新しました。", Toast.LENGTH_SHORT).show();
        }catch (IOException anException){
            anException.printStackTrace();
            this.failedCalendarUpdate("保存失敗しました");
        }
        return;
    }

    /**
     * スケジュールの更新に失敗した場合の処理
     */
    public void failedCalendarUpdate(String message){
        this.isUpdating = false;

        this.handler.post(new Runnable() {
            @Override
            public void run() {
                ((MainActivity)getContext()).setDrawerLock(DrawerLayout.LOCK_MODE_UNLOCKED);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        Toast.makeText(this.getContext(), message, Toast.LENGTH_LONG).show();
        return;
    }

    /**
     * ソート方法を決定するダイアログを表示
     */
    private void selectEventSortDialog(){
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.sort_menu, null);
        RadioGroup radioGroup = view.findViewById(R.id.sort_radio_group);
        CheckBox checkBox = view.findViewById(R.id.is_before_subject);
        radioGroup.check((this.user.isAscendingOrder()) ? R.id.ascending : R.id.descending);
        checkBox.setChecked(this.user.isBeforeSubjectVisible());
        this.sortDialog = new AlertDialog.Builder(getContext())
                .setTitle("ソート方法")
                .setView(view)
                .setPositiveButton("決定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Integer id = radioGroup.getCheckedRadioButtonId();
                        user.setIsAscendingOrder(id.equals(R.id.ascending));
                        user.setBeforeSubjectVisible(checkBox.isChecked());
                        user.writeUserStatus();
                        rAdapter.modelDataUpdate();
                    }
                })
                .create();
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
        this.swipeRefreshLayout.setRefreshing(false);
        super.onPause();
        System.out.println("中断しました");
        return;
    }

}