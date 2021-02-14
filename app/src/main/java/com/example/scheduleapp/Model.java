package com.example.scheduleapp;

import androidx.core.text.HtmlCompat;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Handler;

/**
 * データ管理をするクラス
 */
public class Model extends Object{
    private List<Subject> scheduleList;
    private MainActivity mainActivity;
    private Handler handler;

    /**
     * このクラスのコンストラクタ
     * @param aHandler UI操作のためのメインスレッドのハンドラ
     * @param aMainActivity ビュー
     */
    public Model(MainActivity aMainActivity,Handler aHandler){
        this.scheduleList = Collections.synchronizedList(new ArrayList<>());
        this.handler = aHandler;
        this.mainActivity = aMainActivity;
        return;
    }

    /**
     * json形式のカレンダーイベントをスケジュールに追加する
     * @param jsonString json形式の文字列
     */
    public void addCalendarSchedules(String jsonString){
        try {
            List<Subject> subjectList = SubjectUtility.jsonToSubjectList(jsonString);
            this.scheduleList.clear();
            this.addSchedule(subjectList);
            this.sortScheduleByCalendar();
            this.mainActivity.notifyFinCalendarUpdate();
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
     * スケジュールをJson形式の文字列にして応答する
     * @return json形式の文字列
     */
    public String getJsonSchedule(){
        Gson gson = new Gson();
        //System.out.println(this.scheduleList.get(24).getDescription());
        String jsonString = gson.toJson(this.scheduleList);
        return jsonString;
    }


    /**
     * スケジュールに課題を追加する
     * @param aSubject 課題データ
     */
    public void addSubject(Subject aSubject){
        scheduleList.add(aSubject);
        return;
    }

    /**
     * スケジュールを追加する
     * @param aSchedule スケジュール
     */
    public void addSchedule(List<Subject> aSchedule){
        for(Subject aSubject : aSchedule){
            this.scheduleList.add(aSubject);
        }
        return;
    }

    /**
     * スケジュールのリストを応答する
     * @return スケジュールを表すリスト
     */
    public List<Subject> getScheduleList(){
        return this.scheduleList;
    }

    /**
     * 対応するインデックス番号のスケジュールを応答する
     * もしインデックス番号が範囲を超えていたらnullを応答
     * @param index インデックス番号
     * @return スケジュールを表すリスト
     */
    public Subject getSchedule(Integer index) {
        if (0 <= index && index < this.getScheduleSize()) {
            return this.scheduleList.get(index);
        }
        return null;
    }

    /**
     * スケジュールのサイズを応答する
     * @return スケジュールのサイズ
     */
    public Integer getScheduleSize(){
       return this.scheduleList.size();
    }

    /**
     * このモデルのスケジュールをカレンダーでソートする
     */
    public void sortScheduleByCalendar(){
        Collections.sort(this.scheduleList);
        return;
    }

    /**
     * UIのハンドラを返す
     * @return UIのハンドラ
     */
    public Handler getHandler(){
        return this.handler;
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
        this.mainActivity.update();
        return;
    }

    /**
     * カレンダーの更新が失敗したことを通知
     */
    public void notifyFailedCalendarUpdate(){
        this.mainActivity.failedCalendarUpdate();
        return;
    }

}
