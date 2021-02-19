package com.example.scheduleapp;


import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Handler;
import android.webkit.JavascriptInterface;

/**
 * Javascriptから呼ぶJavaのメソッドを定義するクラス
 */
public class MainJsInterface extends Object{
    protected MainModel model;
    private ScheduleGetter scheduleGetter;
    private volatile Integer errorCount = 0;
    private Handler handler;

    /**
     * このクラスのコンストラクタ
      * @param handler
     * @param aModel
     */
    MainJsInterface(Handler handler, MainModel aModel, ScheduleGetter scheduleGetter){
        this.model = aModel;
        this.scheduleGetter = scheduleGetter;
        this.handler = handler;
    }

    /**
     * イベントのJsonを受け取り、スケージュールに追加
     * @param aString イベントのJson文字列
     */
    @JavascriptInterface
    public void add(String aString){
        System.out.println("JS追加処理");
        this.errorCount = 0;
        this.model.addCalendarSchedules(aString);
    }

    /**
     * 実行に失敗した際に呼ばれるメソッド
     * @param aString エラーメッセージ
     */
    @JavascriptInterface
    public void error(String aString){
        System.out.println(aString);
        if(this.errorCount>5){
            this.model.notifyFailedCalendarUpdate("正しくアクセス出来ませんでした");
            this.errorCount = 0;
        }
        else if(aString.equals("アクセス不能")){
            System.out.println(this.errorCount);
            synchronized (this){
                this.errorCount +=1;
            }
            this.handler.post(new Runnable() {
                @Override
                public void run() {
                    scheduleGetter.failedToAccess();
                }
            });
        }


    }

}
