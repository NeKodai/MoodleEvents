package com.example.scheduleapp;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class SubjectFragmentModel extends Model{
    private SubjectFragment SubjectFragment;
    /**
     * このクラスのコンストラクタ
     * @param aSubjectFragment ビュー
     */
    public SubjectFragmentModel(SubjectFragment aSubjectFragment){
        super();
        this.SubjectFragment= aSubjectFragment;
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
        return;
    }

    /**
     * イベントの削除に成功したことを通知
     */
    public void notifySuccessDelete(){
        this.SubjectFragment.notifySuccessDelete();
        return;
    }

    /**
     * イベントの削除に失敗したことを通知
     */
    public void notifyFailedDeleteEvent(String message){
        this.SubjectFragment.failedDeleteEvent(message);
        return;
    }
}
