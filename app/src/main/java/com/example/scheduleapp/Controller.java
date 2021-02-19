package com.example.scheduleapp;

import android.content.Intent;
import android.os.Handler;

import androidx.fragment.app.Fragment;


/**
 * ユーザからの入力の処理
 */
public class Controller {
    Fragment mainFragment;
    ScheduleGetter scheduleGetter;


    public void initialize(Fragment aMainFragment, ScheduleGetter aScheduleGetter){
        this.scheduleGetter = aScheduleGetter;
        this.mainFragment = aMainFragment;
        return;
    }

    /**
     * スケジュールを更新する
     */
    public void updateSchedule(){
        this.scheduleGetter.loadMoodle();
    }

//    /**
//     * 選択した課題のViewへの遷移を行う
//     * @param aSubject 選択した課題のデータ
//     */
//    public void setSubActivity(Subject aSubject){
//        Intent intent = new Intent(mainActivity, SubActivity.class);
//        intent.putExtra("subject", aSubject);
//        mainActivity.startActivity(intent);
//        return;
//    }
//
//    /**
//     * 設定画面のViewへの遷移を行う
//     */
//    public void setSettingActivity(){
//        Intent intent = new Intent(mainActivity,SettingActivity.class);
//        mainActivity.startActivity(intent);
//        return;
//    }
}
