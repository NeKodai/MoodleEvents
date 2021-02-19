package com.example.scheduleapp;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MainModel extends Model{

    private MainFragment mainFragment;
    /**
     * このクラスのコンストラクタ
     * @param aMainFragment ビュー
     */
    public MainModel(MainFragment aMainFragment){
        super();
        this.mainFragment = aMainFragment;
        return;
    }

    /**
     * json形式のカレンダーイベントをスケジュールに追加する
     * @param jsonString json形式の文字列
     */
    public void addCalendarSchedules(String jsonString){
        try {
            List<Subject> subjectList = SubjectUtility.jsonToSubjectList(jsonString);
            scheduleList.clear();
            this.addSchedule(subjectList);
            this.sortScheduleByCalendar();
            this.mainFragment.notifyFinCalendarUpdate();
            this.notifyUpdate();
        }catch (Exception anException){
            anException.printStackTrace();
            this.notifyFailedCalendarUpdate();
        }
        return;
    }

    /**
     * jsonファイルからスケジュールを読み込み追加する
     * @param jsonString jsonファイル
     */
    public void readSchedule(String jsonString){
        Gson gson = new Gson();
        if(jsonString==null){
            return;
        }
        JsonArray aJsonArray = gson.fromJson(jsonString, JsonArray.class);
        for(JsonElement e : aJsonArray){
            this.scheduleList.add(gson.fromJson(e,Subject.class));
        }
        this.notifyUpdate();
        return;
    }

    /**
     * 課題が現在よりも過去のものなら削除し、更新通知をする
     */
    public void scheduleUpdate(){
        Iterator<Subject> anIterator = this.scheduleList.iterator();
        while(anIterator.hasNext()){
            Subject aSubject = anIterator.next();
            if(aSubject.getRepresentativeTime()<System.currentTimeMillis()){
                //anIterator.remove();
            }
        }
        this.notifyUpdate();
    }

    /**
     * モデルの内容が変化したことを全ての依存物に通知
     */
    public void notifyUpdate(){
        this.mainFragment.update();
        return;
    }

    /**
     * カレンダーの更新が失敗したことを通知
     */
    public void notifyFailedCalendarUpdate(){
        this.mainFragment.failedCalendarUpdate();
        return;
    }

}
