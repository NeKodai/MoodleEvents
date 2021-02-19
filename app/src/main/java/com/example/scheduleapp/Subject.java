package com.example.scheduleapp;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;

/**
 * 課題を表すクラス
 */
public class Subject extends Object implements Comparable<Subject>, Serializable {

    private Integer id; //課題のID
    private String subjectTitle; //課題のタイトル
    private String description; //課題の内容
    private String categoryName; //課題のカテゴリ名（user,course...）
    private String courseName; //コースの名前
    private Calendar startTime; //課題の開始時間を持つカレンダー
    private Calendar endTime; //課題終了時間を持つカレンダー
    private Boolean isSubmit; //提出済みか

    /**
     * このクラスのコンストラクタ
     * @param eventId 課題のID
     * @param name 課題のタイトル
     * @param aDescription 課題の内容
     * @param aCourseName コースの名前
     * @param start 課題の開始時間を持つカレンダー
     * @param end 課題終了時間を持つカレンダー
     */
    public Subject(Integer eventId,String name,String aDescription,String categoryName,String aCourseName,Calendar start,Calendar end){
        this.id = eventId;
        this.subjectTitle = name;
        this.description = aDescription;
        this.categoryName = categoryName;
        this.courseName = aCourseName;
        this.startTime = start;
        this.endTime = end;
        this.isSubmit = true;
    }

    /**
     *　この課題の代表となる時間をミリ秒で返す
     * @return 代表となるミリ秒
     */
    public long getRepresentativeTime() {
       if(this.isAlreadyStarted()) return this.endTime.getTimeInMillis();
       return this.startTime.getTimeInMillis();
    }

    /**
     * この課題がすでに始まっているか
     * @return 始まっているならtrue、それ以外ならfalse
     */
    public boolean isAlreadyStarted(){
        if (this.startTime != null) {
            if (this.startTime.getTimeInMillis() > System.currentTimeMillis()) {
                return false;
            }
        }
        return true;
    }

    /**
     * この課題のIDを返す
     * @return この課題のID
     */
    public Integer getId(){ return this.id; }

    /**
     * この課題のIDをセットする
     * @param aInteger ID
     */
    public void setId(Integer aInteger){this.id = aInteger;}

    /**
     * この課題のタイトルを返す
     * @return タイトルの文字列
     */
    public String getTitle(){
        return this.subjectTitle;
    }

    /**
     * この課題の内容を返す
     * @return 内容の文字列
     */
    public String getDescription(){
        return this.description;
    }

    /**
     * この課題の内容をセットする
     * @param aString 内容
     */
    public void setDescription(String aString){
        this.description = aString;
    }

    /**
     * この課題のカテゴリ名を返す
     * @return この課題のカテゴリ名
     */
    public String getCategoryName(){ return this.categoryName; }

    /**
     * この課題のカレンダーを返す
     * @return この課題のカレンダー
     */
    public String getCourseName(){
        return this.courseName;
    }

    /**
     * この課題開始時間を返す
     * 開始時間が存在しない場合はnullを返す
     * @return この課題のカレンダー
     */
    public Calendar getStartTime(){
        return this.startTime;
    }

    /**
     * 開始時間を変更する
     * @param aCalendar 変更する時間
     */
    public void setStartTime(Calendar aCalendar){
        this.startTime = aCalendar;
        return;
    }

    /**
     * この課題の終了時間を返す
     * @return この課題のカレンダー
     */
    public Calendar getEndTime(){
        return this.endTime;
    }

    /**
     * 終了時間を変更する
     * @param aCalendar 変更する時間
     */
    public void setEndTime(Calendar aCalendar){
        this.endTime = aCalendar;
        return;
    }

    /**
     * 提出判定を設定する
     * @param aBoolean
     */
    public void setSubmit(Boolean aBoolean){
        this.isSubmit = aBoolean;
    }

    /**
     * 提出済みか
     * @return 提出済みならtrue、それ以外ならfalse
     */
    public Boolean isSubmit(){
        return this.isSubmit;
    }

    /**
     * この課題のシャローコピーを返す
     * @return この課題のコピー
     */
    public Subject clone(){
        Subject aSubject = new Subject(this.id,this.subjectTitle,this.description,this.categoryName,this.courseName,this.startTime,this.endTime);
        aSubject.setSubmit(this.isSubmit);
        return aSubject;
    }


    /**
     * このクラスを文字列にして返す
     * @return このクラスを表す文字列
     */
    @Override
    public String toString(){
        return this.subjectTitle;
    }

    /**
     * このクラスの代表となる時間で、与えられたSubject型と比較
     * @param targetSubject 比較対象
     * @return この課題が与えられた課題よりも日時が速いなら-1、遅いなら1、同じなら0
     */
    public int compareTo(Subject targetSubject){
        long aMillis = this.getRepresentativeTime();
        long anotherMillis = targetSubject.getRepresentativeTime();
        if(aMillis>anotherMillis)return 1;
        else if(aMillis<anotherMillis)return -1;
        return 0;
    }

    /**
     * 同値性を判定 = idが同じか
     * @param anObject 比較対象のオブジェクト
     * @return 同値かどうか
     */
    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) return true;
        if (anObject == null || getClass() != anObject.getClass()) return false;
        Subject subject = (Subject) anObject;
        return Objects.equals(id, subject.id);
    }

    /**
     * ハッシュコードを応答する
     * @return ハッシュコード
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
