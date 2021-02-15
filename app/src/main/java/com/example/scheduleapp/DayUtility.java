package com.example.scheduleapp;

import java.util.Calendar;
import java.util.Locale;

/**
 * 日付に関するユーティリティ
 */
public class DayUtility extends Object{

    /**
     * ミリ秒からカレンダークラスを作成
     * @param millis
     * @return カレンダー
     */
    public static Calendar millisToDate(Long millis){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar;
    }

    /**
     * カレンダークラスを作成する
     * 月は1オリジンでOK
     * @param year 年
     * @param month　月
     * @param day　日
     * @param hour　時間
     * @param minutes　分
     * @param seconds　秒
     * @return カレンダー
     */
    public static Calendar createCalendar(Integer year,Integer month,Integer day,Integer hour,Integer minutes,Integer seconds){
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.set(year,month-1,day,hour,minutes,0);
        return aCalendar;
    }

    /**
     * カレンダーから日付の文字列を作成
     * @param aCalendar
     * @return 日付の文字列
     */
    public static String createDateString(Calendar aCalendar){
        return  String.format(Locale.US,"%d年%d月%d日 %d時%d分",
                aCalendar.get(Calendar.YEAR),aCalendar.get(Calendar.MONTH)+1,aCalendar.get(Calendar.DATE),
                aCalendar.get(Calendar.HOUR_OF_DAY),aCalendar.get(Calendar.MINUTE));
    }

    /**
     * テスト用メインメソッド
     * @param args
     */
    public static void main(String... args){
        Long time = Long.valueOf("1612191600000");
        Calendar calendar = millisToDate(time);
        System.out.println(calendar.getTime());
    }

}
