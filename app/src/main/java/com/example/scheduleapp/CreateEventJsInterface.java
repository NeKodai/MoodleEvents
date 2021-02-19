package com.example.scheduleapp;

import android.os.Handler;
import android.webkit.JavascriptInterface;

public class CreateEventJsInterface {
    private ScheduleSetter scheduleSetter;
    private volatile Integer errorCount = 0;
    private Handler handler;

    /**
     * このクラスのコンストラクタ
     * @param handler
     * @param scheduleSetter
     */
    public CreateEventJsInterface(Handler handler,ScheduleSetter scheduleSetter){
        this.scheduleSetter = scheduleSetter;
        this.handler = handler;
    }

    @JavascriptInterface
    public void create(String jsonString){
        System.out.println("登録完了");
        this.scheduleSetter.successCreate(jsonString);
        this.errorCount = 0;
    }

    /**
     * 実行に失敗した際に呼ばれるメソッド
     * @param aString エラーメッセージ
     */
    @JavascriptInterface
    public void error(String aString){
        System.out.println(aString);
        if(this.errorCount>5){
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
                    scheduleSetter.failedToAccess();
                }
            });
        }
    }

}

