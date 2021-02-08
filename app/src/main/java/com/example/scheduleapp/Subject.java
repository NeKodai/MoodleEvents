package com.example.scheduleapp;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;

/**
 * 課題を表すクラス
 * 課題のタイトル、内容、日時を持つ
 */
public class Subject extends Object implements Comparable<Subject>, Serializable {

    private Integer id; //課題のID
    private String subjectTitle; //課題のタイトル
    private String description; //課題の内容
    private String courseName; //コースの名前
    private Calendar calendar; //課題の時間を持つカレンダー

    /**
     * このクラスのコンストラクタ
     * @param eventId 課題のID
     * @param name 課題のタイトル
     * @param aDescription 課題の内容
     * @param aCourseName コースの名前
     * @param aCalendar 課題の日時を表すカレンダー
     */
    public Subject(Integer eventId,String name,String aDescription,String aCourseName,Calendar aCalendar){
        this.id = eventId;
        this.subjectTitle = name;
        this.description = aDescription;
        this.courseName = aCourseName;
        this.calendar = aCalendar;
    }

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
     * この課題のカレンダーを返す
     * @return この課題のカレンダー
     */
    public String getCourseName(){
        return this.courseName;
    }

    /**
     * この課題のカレンダーを返す
     * @return この課題のカレンダー
     */
    public Calendar getCalendar(){
        return this.calendar;
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
     * このクラスの自然順序で与えられたSubject型と比較
     * 自然順序はカレンダーの日時
     * @param targetSubject 比較対象
     * @return この課題が与えられた課題よりも日時が速いなら-1、遅いなら1、同じなら0
     */
    public int compareTo(Subject targetSubject){
        return this.calendar.compareTo(targetSubject.getCalendar());
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
