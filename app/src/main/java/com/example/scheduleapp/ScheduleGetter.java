package com.example.scheduleapp;


import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.util.Calendar;

/**
 * Moodleへのログインおよび課題取得を実行するクラス
 */
public class ScheduleGetter extends Object{
    private Model model; // モデル
    private WebView hiddenView; //WebView
    private UserStatus user; //ユーザを管理するクラス
    private CookieManager cookieManager; // Cookie管理クラス
    private Integer accessErrorCount = 0; // アクセス不能回数をカウントする変数

    /**
     * このクラスのコンストラクタ
     * @param model モデル
     * @param aView WebView
     */
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
        this.user = new UserStatus();
    }


    /**
     * moodleログインのためのWebViewClient
     */
    private class moodleWebViewClient extends WebViewClient{
        private Integer loginErrorCount = 0; //ログインエラーの回数をカウントする変数

        /**
         *ページ読み込み完了後の処理
         * @param view WebView
         * @param url 読み込んだURL
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view,url);

            System.out.println(url);
            if(this.loginErrorCount>10){
                System.out.println("ログインエラー");
                accessErrorCount = 0;
                this.loginErrorCount = 0;
                model.notifyFailedCalendarUpdate();
                return;
            }
            //Moodleなら
            if(url.matches("https://cclms.kyoto-su.ac.jp/")){
                System.out.println("get schedule");
                getCalendarEvents();
            }
            //ログインページなら
            else if (url.matches("https://gakunin.kyoto-su.ac.jp/idp/profile/SAML2/Redirect/SSO.execution=.*")) {
                this.loginErrorCount+=1;
                view.evaluateJavascript("document.getElementById('username')", new ValueCallback<String>() {
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

        /**
         * 読み込みエラーの場合の処理
         * @param view WebView
         * @param request リクエスト
         * @param error エラー
         */
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
     * アクセスに失敗した場合の処理
     * エラーカウントを1増加させ、再度Moodleにアクセスする
     */
    public void failedToAccess(){
        this.accessErrorCount+=1;
        this.loadMoodle();
    }

    /**
     * Moodleにアクセスする
     */
    public void loadMoodle(){
        this.user.readUserStatus();
        this.hiddenView.loadUrl("https://cclms.kyoto-su.ac.jp/auth/shibboleth/");
    }

    /**
     * カレンダーからイベントIDを取得し、イベントの詳細を取得するJavascriptを実行する
     */
    private void getCalendarEvents(){
        try {
            String script = FileUtility.readAssets("getEvents.js");
            String args = this.makeJsArgumentString();
            script+="(function () { wait("+args+"); })();";
            this.hiddenView.evaluateJavascript(script, null);
        }
        catch (IOException anException){
            anException.printStackTrace();
            System.out.println("js読み込み失敗");
        }
        return;
    }

    /**
     * getCalendarEventsで使用するJavaScriptの引数の文字列を作成する
     * @return 引数の文字列
     */
    private String makeJsArgumentString(){
        SpinnerItem before = this.user.getBeforeSpinnerItem();
        SpinnerItem after = this.user.getAfterSpinnerItem();
        Calendar beforeCalendar = Calendar.getInstance();
        Calendar afterCalendar = Calendar.getInstance();
        StringBuilder stringBuilder = new StringBuilder();
        beforeCalendar.add(before.getCalendarField(),before.getAmount());
        afterCalendar.add(after.getCalendarField(),after.getAmount());
        stringBuilder.append(beforeCalendar.get(Calendar.YEAR));
        stringBuilder.append(",");
        stringBuilder.append(beforeCalendar.get(Calendar.MONTH)+1);
        stringBuilder.append(",");
        stringBuilder.append(afterCalendar.get(Calendar.YEAR));
        stringBuilder.append(",");
        stringBuilder.append(afterCalendar.get(Calendar.MONTH)+1);
        return new String(stringBuilder);
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
