package com.example.scheduleapp;


import android.renderscript.Sampler;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;

import java.io.IOException;

public class ScheduleGetter extends Object{
    private Model model;
    private WebView hiddenView; //webview
    private CookieManager cookieManager; // Cookie管理クラス
    private Integer accessErrorCount = 0; // アクセス不能回数をカウントする変数

    public ScheduleGetter(Model model,WebView aView){
        this.model = model;
        this.hiddenView = aView;
        this.hiddenView.getSettings().setJavaScriptEnabled(true);//javascriptオン
        this.hiddenView.getSettings().setDomStorageEnabled(true); // WebStorageをオン
        this.hiddenView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        this.hiddenView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        this.hiddenView.clearCache(true);
        this.cookieManager= CookieManager.getInstance();
        this.cookieManager.setAcceptCookie(true);
        this.cookieManager.removeAllCookies(null);
        this.cookieManager.setAcceptThirdPartyCookies(this.hiddenView, true);
        this.cookieManager.flush();
        this.hiddenView.setWebViewClient(new moodleWebViewClient());
    }


    /**
     * moodleログインのためのWebViewClient
     */
    private class moodleWebViewClient extends WebViewClient{
        private Integer loginErrorCount = 0; //ログインエラーの回数をカウントする変数

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view,url);
            int len = url.length(); //urlの長さ
            char end = url.charAt(len - 1);
            System.out.println(url);
            if(this.loginErrorCount>10){
                System.out.println("ログインエラー");
                accessErrorCount = 0;
                this.loginErrorCount = 0;
                model.notifyFailedCalendarUpdate();
                return;
            }
            if(url.matches("https://cclms.kyoto-su.ac.jp/")){
                System.out.println("get schedule");
                getCalendarEvents();
            }
            else if (url.matches("https://gakunin.kyoto-su.ac.jp/idp/profile/SAML2/Redirect/SSO.execution=.*")) {
                this.loginErrorCount+=1;
                view.evaluateJavascript("document.getElementById('username')", new ValueCallback<String>() {
                    UserStatus user = new UserStatus();
                    @Override
                    public void onReceiveValue(String value) {
                        user.readUserStatus();
                        if(!value.equals("null")){ //1段階目の認証
                            System.out.println("first");
                            view.evaluateJavascript("document.getElementById('username').value='"+user.getUserId()+"'", null);
                            view.evaluateJavascript("document.getElementById('password').value='"+user.getPassword()+"'", null);
                            view.evaluateJavascript("var elements=document.getElementsByClassName('form-element form-button')\nelements[0].click()", null);
                        }else { //2段階目の認証
                            System.out.println("second");
                            try {
                                String script = String.format("document.getElementById('token').value='%s'", AuthPassWord.getAuthPass(user.getAuthKey()));
                                view.evaluateJavascript(script, null);
                                view.evaluateJavascript("var elements=document.getElementsByClassName('form-element form-button')\nelements[0].click()", null);
                            } catch (Exception anException) {
                                anException.printStackTrace();
                            }
                        }
                    }
                });
            }else if(url.matches("https://cclms.kyoto-su.ac.jp/login/index.php?")){
                gakuninButtonClick();
            }
        }
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request , WebResourceError error) {
            if(accessErrorCount>10){
                System.out.println("アクセスエラー");
                accessErrorCount = 0;
                this.loginErrorCount = 0;
                model.notifyFailedCalendarUpdate();
            }
            else if(request.isForMainFrame()) {
                failedToAccess();
            }
            return;
        }
    }

    /**
     * Moodleにログインする
     */
    public void failedToAccess(){
        this.accessErrorCount+=1;
        this.loadMoodle();
    }

    /**
     * Moodleにアクセスする
     */
    public void loadMoodle(){
        this.hiddenView.loadUrl("https://cclms.kyoto-su.ac.jp/auth/shibboleth/");
    }

    /**
     * カレンダーからイベントIDを取得し、イベントの詳細を取得するJavascriptを実行する
     */
    private void getCalendarEvents(){
        try {
            String script = FileUtility.readAssets("test.js");
            this.hiddenView.evaluateJavascript(script, null);
        }
        catch (IOException anException){
            anException.printStackTrace();
            System.out.println("js読み込み失敗");
        }
        return;
    }

    /**
     * 学認からログインのボタンをクリックする
     */
    private void gakuninButtonClick(){
        try {
            String script = FileUtility.readAssets("gakuninButtonClick.js");
            this.hiddenView.evaluateJavascript(script, null);
        }catch (IOException anException){
            anException.printStackTrace();
            System.out.println("js読み込み失敗");
        }
        return;
    }
}
