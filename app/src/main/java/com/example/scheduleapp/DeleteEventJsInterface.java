package com.example.scheduleapp;

import android.os.Handler;
import android.webkit.JavascriptInterface;

public class DeleteEventJsInterface {
    private ScheduleDeleter scheduleDeleter;
    private volatile Integer errorCount = 0;
    private Handler handler;

    /**
     * このクラスのコンストラクタ
     * @param handler
     * @param scheduleDeleter
     */
    public DeleteEventJsInterface(Handler handler,ScheduleDeleter scheduleDeleter){
        this.scheduleDeleter = scheduleDeleter;
        this.handler = handler;
    }

    @JavascriptInterface
    public void delete(){
        System.out.println("削除完了");
        this.scheduleDeleter.successDelete();
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
                    scheduleDeleter.failedToAccess();
                }
            });
        }
    }
}
