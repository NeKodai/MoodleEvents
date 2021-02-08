package com.example.scheduleapp;

public class DayUtility extends Object{

    /**
     * ミリ秒から日・時・分・秒に直す
     * @param millis
     * @return 日・時・分・秒の配列
     */
    public static Long[] millisToDate(Long millis){
        Long[] date= new Long[4];
        Long seconds = millis /1000%60;
        Long minutes  = millis /1000/60%60;
        Long hour  = millis /1000/60/60%24;
        Long day  = millis /1000/60/60/24;
        date[0] = day;
        date[1] = hour;
        date[2] = minutes;
        date[3] = seconds;
        return date;
    }

    /**
     * ミリ秒を秒に変換する
     * @param millis
     * @return
     */
    public static Long millisToSeconds(Long millis){
        return millis/1000;
    }

}
