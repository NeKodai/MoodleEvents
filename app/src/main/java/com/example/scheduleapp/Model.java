package com.example.scheduleapp;

import androidx.core.text.HtmlCompat;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Calendar;
import android.os.Handler;

/**
 * データ管理をするクラス
 */
public class Model extends Object{
    private List<Subject> scheduleList;
    private MainActivity mainActivity;
    private Handler handler;

    /**
     * このクラスのコンストラクタ
     * @param aHandler UI操作のためのメインスレッドのハンドラ
     * @param aMainActivity ビュー
     */
    public Model(MainActivity aMainActivity,Handler aHandler){
        this.scheduleList = Collections.synchronizedList(new ArrayList<>());
        this.handler = aHandler;
        this.mainActivity = aMainActivity;
        return;
    }

    /**
     * json形式のカレンダーイベントをスケジュールに追加する
     * @param jsonString json形式の文字列
     */
    public void addCalendarSchedules(String jsonString){
        List<Subject> subjectList = this.jsonToSubjectList(jsonString);
        this.scheduleList.clear();
        this.addSchedule(subjectList);
        this.sortScheduleByCalendar();
        this.mainActivity.notifyFinCalendarUpdate();
        this.notifyUpdate();
        return;
    }

    /**
     * Json形式で渡されてきたイベントデータをSubject型を持つリストへ変換
     * @param jsonString Json形式の文字列
     */
    private List<Subject> jsonToSubjectList(String jsonString){
        List<ArrayList> yearDataList =  new Gson().fromJson(jsonString, List.class);
        List<Subject> subjectList = new ArrayList<>();
        for(ArrayList aList  : yearDataList) {
            LinkedTreeMap jsonMap = (LinkedTreeMap) aList.get(0);
            LinkedTreeMap dataTreeMap = (LinkedTreeMap) jsonMap.get("data");
            LinkedTreeMap dateTreeMap = (LinkedTreeMap) dataTreeMap.get("date");
            Integer year = ((Double)dateTreeMap.get("year")).intValue();
            Integer month = ((Double)dateTreeMap.get("mon")).intValue();
            ArrayList<LinkedTreeMap> weekMap =(ArrayList<LinkedTreeMap>) dataTreeMap.get("weeks");
            for(LinkedTreeMap weekTreeMap : weekMap){
                ArrayList<LinkedTreeMap> days = (ArrayList<LinkedTreeMap>)weekTreeMap.get("days");
                for(LinkedTreeMap dayTreeMap :days){
                    //イベントを持っているか
                    if(!(boolean)dayTreeMap.get("hasevents")){
                        continue;
                    }
                    //日を取得
                    Integer day = ((Double) dayTreeMap.get("mday")).intValue();
                    //イベント情報取得
                    ArrayList<LinkedTreeMap> events = (ArrayList)dayTreeMap.get("events");
                    for(LinkedTreeMap eventTreeMap : events){
                        //各イベントデータ取得
                        //イベントタイプを取得。
                        String courseName = (String)eventTreeMap.get("calendareventtype");
                        //コースイベントならそのまま、それ以外ならイベントを後ろにつける
                        if(courseName.equals("course")) {
                            courseName = (String) ((LinkedTreeMap) eventTreeMap.get("course")).get("fullname");
                        }
                        else{
                            courseName+="イベント";
                        }
                        String title = ((String)eventTreeMap.get("name")).replaceAll("( の提出期限が到来しています。)$","");
                        String description = (String)eventTreeMap.get("description");
                        if(description.equals("")){
                            description = "<p></p>";
                        }
                        String formattedTime =(String)eventTreeMap.get("formattedtime");

                        Integer eventId = ((Double)eventTreeMap.get("id")).intValue();
                        //時刻を取得
                        String timeString = HtmlCompat.fromHtml(formattedTime,HtmlCompat.FROM_HTML_MODE_COMPACT).toString();
                        //ユーザイベント対策　最初の時間しか採用されない不具合あり
                        timeString = timeString.replaceAll(".*(年|月|日|, )","");
                        String[] timeStringArray = timeString.split("(:| » )");
                        //時間の文字列を整数に変換
                        List<Integer> timeArray = new ArrayList<>();
                        for(String aString:timeStringArray){
                            timeArray.add(Integer.valueOf(aString));
                        }
                        Integer hour = timeArray.get(0);
                        Integer minute = timeArray.get(1);
                        //カレンダー設定
                        Calendar aCalendar = Calendar.getInstance();
                        aCalendar.set(year,month-1,day,hour,minute,0);
                        //現在時刻よりもカレンダーの予定が過去のものなら保存しない
                        if(aCalendar.getTimeInMillis()<System.currentTimeMillis())
                            continue;

                        Subject aSubject = new Subject(eventId,title,description,courseName,aCalendar);
//                        System.out.println(aSubject.getTitle());
//                        System.out.println(aCalendar.get(Calendar.YEAR));
//                        System.out.println(aCalendar.get(Calendar.MONTH)+1);
//                        System.out.println(aCalendar.get(Calendar.DATE));
//                        System.out.println(aCalendar.get(Calendar.HOUR_OF_DAY));
//                        System.out.println(aCalendar.get(Calendar.MINUTE));
                        subjectList.add(aSubject);
                    }
                }
            }
        }
        return subjectList;
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
     * スケジュールをJson形式の文字列にして応答する
     * @return json形式の文字列
     */
    public String getJsonSchedule(){
        Gson gson = new Gson();
        //System.out.println(this.scheduleList.get(24).getDescription());
        String jsonString = gson.toJson(this.scheduleList);
        return jsonString;
    }


    /**
     * スケジュールに課題を追加する
     * @param aSubject 課題データ
     */
    public void addSubject(Subject aSubject){
        scheduleList.add(aSubject);
        return;
    }

    /**
     * スケジュールを追加する
     * @param aSchedule スケジュール
     */
    public void addSchedule(List<Subject> aSchedule){
        for(Subject aSubject : aSchedule){
            this.scheduleList.add(aSubject);
        }
        return;
    }

    /**
     * スケジュールのリストを応答する
     * @return スケジュールを表すリスト
     */
    public List<Subject> getScheduleList(){
        return this.scheduleList;
    }

    /**
     * 対応するインデックス番号のスケジュールを応答する
     * もしインデックス番号が範囲を超えていたらnullを応答
     * @param index インデックス番号
     * @return スケジュールを表すリスト
     */
    public Subject getSchedule(Integer index) {
        if (0 <= index && index < this.getScheduleSize()) {
            return this.scheduleList.get(index);
        }
        return null;
    }

    /**
     * スケジュールのサイズを応答する
     * @return スケジュールのサイズ
     */
    public Integer getScheduleSize(){
       return this.scheduleList.size();
    }

    /**
     * このモデルのスケジュールをカレンダーでソートする
     */
    public void sortScheduleByCalendar(){
        Collections.sort(this.scheduleList);
        return;
    }

    /**
     * 課題が現在よりも過去のものなら削除し、更新通知をする
     */
    public void scheduleUpdate(){
        Iterator<Subject> anIterator = this.scheduleList.iterator();
        while(anIterator.hasNext()){
            Subject aSubject = anIterator.next();
            if(aSubject.getCalendar().getTimeInMillis()<System.currentTimeMillis()){
                anIterator.remove();
            }
        }
        this.notifyUpdate();
    }


    /**
     * モデルの内容が変化したことを全ての依存物に通知
     */
    public void notifyUpdate(){
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                mainActivity.update();
            }
        });
        return;
    }

    /**
     * カレンダーの更新が失敗したことを通知
     */
    public void notifyFailedCalendarUpdate(){
        this.mainActivity.failedCalendarUpdate();
        return;
    }

}
