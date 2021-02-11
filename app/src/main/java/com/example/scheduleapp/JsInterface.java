package com.example.scheduleapp;


import androidx.appcompat.app.AppCompatActivity;
import android.webkit.JavascriptInterface;

/**
 * Javascriptから呼ぶJavaのメソッドを定義するクラス
 */
public class JsInterface extends Object{
    protected Model model;
    private AppCompatActivity  activity;
    private ScheduleGetter scheduleGetter;
    private volatile Integer errorCount = 0;

    /**
     * このクラスのコンストラクタ
      * @param activity
     * @param aModel
     */
    JsInterface(AppCompatActivity activity,Model aModel,ScheduleGetter scheduleGetter){
        this.activity = activity;
        this.model = aModel;
        this.scheduleGetter = scheduleGetter;
    }

    /**
     * カレンダーイベントのJsonを受け取り、スケージュールに追加
     * @param aString カレンダーイベントのJson文字列
     */
    @JavascriptInterface
    public void add(String aString){
        System.out.println("JS追加処理");
        this.errorCount = 0;
        this.model.addCalendarSchedules(aString);

    }

    @JavascriptInterface
    public void addActionEvents(String aString){

        System.out.println(aString);
    }

    /**
     * 実行に失敗した際に呼ばれるメソッド
     * @param aString エラーメッセージ
     */
    @JavascriptInterface
    public void error(String aString){
        System.out.println(aString);
        if(this.errorCount>5){
            this.model.notifyFailedCalendarUpdate();
            this.errorCount = 0;
        }
        else if(aString.equals("アクセス不能")){
            System.out.println(this.errorCount);
            synchronized (this){
                this.errorCount +=1;
            }
            this.model.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    scheduleGetter.loadURL("https://cclms.kyoto-su.ac.jp/auth/shibboleth/");
                }
            });
        }


    }

}
