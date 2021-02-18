package com.example.scheduleapp;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * データ管理をするクラス
 */
public class Model extends Object{
    protected List<Subject> scheduleList;

    /**
     * このクラスのコンストラクタ
     */
    public Model(){
        this.scheduleList = Collections.synchronizedList(new ArrayList<>());
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

}
