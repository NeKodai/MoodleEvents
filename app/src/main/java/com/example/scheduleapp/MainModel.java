package com.example.scheduleapp;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 課題一覧のモデル
 */
public class MainModel extends Model{

    private MainFragment mainFragment;
    private UserStatus user;
    /**
     * このクラスのコンストラクタ
     * @param aMainFragment ビュー
     */
    public MainModel(MainFragment aMainFragment,UserStatus user){
        super();
        this.mainFragment = aMainFragment;
        this.user = user;
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
            this.mainFragment.notifyFinCalendarUpdate();
            this.notifyUpdate();
        }catch (Exception anException){
            anException.printStackTrace();
            this.notifyFailedCalendarUpdate("正しく追加できませんでした");
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
     * ユーザに指定された方法でソートされたイベントリストを返す
     */
    public List<Subject> getSortedScheduleList(){
        Iterator<Subject> anIterator = this.scheduleList.iterator();
        List<Subject> sortedList = new ArrayList<>();
        //過去の課題を表示するかどうか
        if(! this.user.isBeforeSubjectVisible()) {
            while (anIterator.hasNext()) {
                Subject aSubject = anIterator.next();
                if (aSubject.getRepresentativeTime() >= System.currentTimeMillis()) {
                    sortedList.add(aSubject);
                }
            }
        }
        else{
            sortedList = this.scheduleList;
        }
        //降順か昇順か
        if(this.user.isAscendingOrder()) {
            Collections.sort(sortedList);
        }else{
            Collections.sort(sortedList,Collections.reverseOrder());
        }
        return sortedList;
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
    public void notifyFailedCalendarUpdate(String message){
        this.mainFragment.failedCalendarUpdate(message);
        return;
    }

}
