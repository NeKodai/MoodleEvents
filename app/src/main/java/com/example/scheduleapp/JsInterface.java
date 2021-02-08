package com.example.scheduleapp;


import androidx.appcompat.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Javascriptから呼ぶJavaのメソッドを定義するクラス
 */
public class JsInterface extends Object{
    protected Model model;
    private AppCompatActivity  activity;

    /**
     * このクラスのコンストラクタ
      * @param activity
     * @param aModel
     */
    JsInterface(AppCompatActivity activity,Model aModel){
        this.activity = activity;
        this.model = aModel;
    }

    /**
     * カレンダーイベントのJsonを受け取り、スケージュールに追加
     * @param aString カレンダーイベントのJson文字列
     */
    @JavascriptInterface
    public void add(String aString){
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
        this.model.notifyFailedCalendarUpdate();

    }

}
