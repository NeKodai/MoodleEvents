package com.example.scheduleapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * イベント期間設定のためのスピナのアイテムを定義する列挙型
 */
public enum SpinnerItem {
    THREE_MONTH_BEFORE("3カ月前",Calendar.MONTH,-3),
    TWO_MONTH_BEFORE("2カ月前",Calendar.MONTH,-2),
    A_MONTH_BEFORE("1カ月前",Calendar.MONTH,-1),
    NOW("今月",Calendar.MONTH,0),
    A_MONTH_AFTER("1カ月先",Calendar.MONTH,1),
    THREE_MONTH_AFTER("3カ月先",Calendar.MONTH,3),
    SIX_MONTH_AFTER("6カ月先",Calendar.MONTH,6),
    TWELVE_MONTH_AFTER("12カ月先",Calendar.MONTH,12);


    private String label;
    private Integer calendarField;
    private Integer amount;

    /**
     * この列挙型のコンストラクタ
     * @param label　スピナに表示される文字
     * @param calendarField 加減算に使用するカレンダーの単位(月,日など)を表す整数
     * @param amount 加減算の量
     */
    private SpinnerItem(String label,Integer calendarField,Integer amount){
        this.label = label;
        this.calendarField = calendarField;
        this.amount = amount;
    }

    /**
     * 過去期間を設定するスピナのアイテム一覧を返す
     * ここで選択肢を定義
     * @return アイテムのリスト
     */
    public static List<SpinnerItem> getBeforeItems(){
        return new ArrayList<>(Arrays.asList(SpinnerItem.THREE_MONTH_BEFORE,SpinnerItem.TWO_MONTH_BEFORE,SpinnerItem.A_MONTH_BEFORE,SpinnerItem.NOW));
    }

    /**
     * 未来期間を設定するスピナのアイテム一覧を返す
     * ここで選択肢を定義
     * @return アイテムのリスト
     */
    public static List<SpinnerItem> getAfterItems(){
        return new ArrayList<>(Arrays.asList(SpinnerItem.A_MONTH_AFTER,SpinnerItem.THREE_MONTH_AFTER,SpinnerItem.SIX_MONTH_AFTER,SpinnerItem.TWELVE_MONTH_AFTER));
    }

    /**
     * スピナに表示される文字を返す
     * @return 文字
     */
    public String getLabel(){return this.label;}

    /**
     * カレンダーの単位を返す
     * @return 単位を表す整数
     */
    public Integer getCalendarField(){return this.calendarField;}

    /**
     * 加減算の量
     * @return 量
     */
    public Integer getAmount(){
        return this.amount;
    }

    /**
     * このオブジェクトを文字列にして返す
     * @return 文字列
     */
    @Override
    public String toString(){
        return this.label;
    }

}
