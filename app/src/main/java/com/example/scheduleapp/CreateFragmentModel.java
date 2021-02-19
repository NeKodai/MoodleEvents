package com.example.scheduleapp;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class CreateFragmentModel extends Model{

    private CreateEventFragment createFragment;
    /**
     * このクラスのコンストラクタ
     * @param aCreateFragment ビュー
     */
    public CreateFragmentModel(CreateEventFragment aCreateFragment){
        super();
        this.createFragment= aCreateFragment;
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
     * イベントの作成に成功したことを通知
     */
    public void notifySuccessCreate(){
        this.createFragment.notifySuccessCreate();
    }

    /**
     * イベントの作成に失敗したことを通知
     */
    public void notifyFailedCalendarUpdate(String message){
        this.createFragment.failedCalendarUpdate(message);
        return;
    }
}
