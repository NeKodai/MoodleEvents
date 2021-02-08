package com.example.scheduleapp;


import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;

import java.io.IOException;

public class ScheduleGetter extends Object{
    private  WebView hiddenView;
    private AuthPassWord anAuthPassWord;
    private Model model;

    public ScheduleGetter(Model model,WebView aView){
        hiddenView = aView;
        hiddenView.getSettings().setJavaScriptEnabled(true);//javascriptオン
        hiddenView.getSettings().setDomStorageEnabled(true); // WebStorageをオン
        this.model = model;
        anAuthPassWord = new AuthPassWord();
    }

    /**
     * Moodleにログインする
     */
    public void loadMoodle(){
        UserStatus user = new UserStatus();
        user.readUserStatus();
        this.hiddenView.setWebViewClient(new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            int len = url.length(); //urlの長さ
            char end = url.charAt(len - 1);
            System.out.println(url);
            if(url.matches("https://cclms.kyoto-su.ac.jp/calendar/view.php.view=month.*?")){
                //getActionCalendarEvents();
                getCalendarEvents();
            }
            else if (end == '1') {
                view.evaluateJavascript("document.getElementById('username').value='"+user.getUserId()+"'", null);
                view.evaluateJavascript("document.getElementById('password').value='"+user.getPassword()+"'", null);
                view.evaluateJavascript("var elements=document.getElementsByClassName('form-element form-button')\nelements[0].click()", null);
            } else if (end == '2') {
                try {
                    String script = String.format("document.getElementById('token').value='%s'", anAuthPassWord.getAuthPass(user.getAuthKey()));

                    view.evaluateJavascript(script, null);
                    view.evaluateJavascript("var elements=document.getElementsByClassName('form-element form-button')\nelements[0].click()", null);
                } catch (Exception anException) {
                    anException.printStackTrace();
                }
            }else if(url.matches("https://cclms.kyoto-su.ac.jp/login/index.php?")){
                view.evaluateJavascript("document.getElementById('gakuninloginbtn').click()", null);
            }

        }});
        this.hiddenView.loadUrl("https://cclms.kyoto-su.ac.jp/calendar/view.php?view=month");
    }

    /**
     * カレンダーからイベントIDを取得し、イベントの詳細を取得するJavascriptを実行する
     */
    private void getCalendarEvents(){
        try {
            String script = FileUtility.readAssets("getCalendarSchedule.js");
            this.hiddenView.evaluateJavascript(script, null);
        }
        catch (IOException anException){
            anException.printStackTrace();
            System.out.println("js読み込み失敗");
        }
        return;
    }

    private void getActionCalendarEvents(){
        try {
            String script = FileUtility.readAssets("getActionEvent.js");
            this.hiddenView.evaluateJavascript(script, null);
        }catch (IOException anException){
            anException.printStackTrace();
            System.out.println("js読み込み失敗");
        }
    }

}
