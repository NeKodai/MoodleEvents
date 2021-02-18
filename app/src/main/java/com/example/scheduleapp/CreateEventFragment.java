package com.example.scheduleapp;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateEventFragment extends Fragment {

    private TextInputEditText title;
    private TextInputEditText description;
    private EditText start_date;
    private EditText start_time;
    private EditText end_date;
    private EditText end_time;
    private FragmentManager fragmentManager;
    private Calendar startCalendar;
    private Calendar endCalendar;
    private Button save;
    private WebView webView;
    private ScheduleSetter scheduleSetter;
    private CreateFragmentModel model;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // 先ほどのレイアウトをここでViewとして作成します
        return inflater.inflate(R.layout.create_event_view, container, false);
    }

    /**
     * ビューが作成さてた後の処理
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.fragmentManager = getParentFragmentManager();
        this.title = view.findViewById(R.id.title_input);
        this.description = view.findViewById(R.id.description_input);
        this.start_date = view.findViewById(R.id.add_start_date_input);
        this.start_time = view.findViewById(R.id.add_start_time_input);
        this.end_date = view.findViewById(R.id.add_end_date_input);
        this.end_time = view.findViewById(R.id.add_end_time_input);
        this.save = view.findViewById(R.id.add_save_button);
        this.startCalendar = Calendar.getInstance();
        this.endCalendar = Calendar.getInstance();
        this.model = new CreateFragmentModel(this);
        //予定表を読み込む
        try {
            this.model.readSchedule(FileUtility.readFile("schedule.json"));
        }catch (IOException anException){
            Toast.makeText(getContext(),"正しく読み込めませんでした",Toast.LENGTH_LONG).show();
        }
        this.webView = ((MainActivity)getContext()).findViewById(R.id.webView1);
        this.scheduleSetter = new ScheduleSetter(this.model,this.webView);


        this.webView.addJavascriptInterface(new CreateEventJsInterface(new Handler(),this.scheduleSetter),"Android2");

        this.start_date.setOnClickListener(new onDateInputClick(this.startCalendar,this.start_date));
        this.start_time.setOnClickListener(new onTimeInputClick(this.startCalendar,this.start_time) );
        this.end_date.setOnClickListener(new onDateInputClick(this.endCalendar,this.end_date));
        this.end_time.setOnClickListener(new onTimeInputClick(this.endCalendar,this.end_time));
        this.save.setOnClickListener(new onSaveButtonClick());

        this.start_date.setText(DayUtility.createDateString(this.startCalendar));
        this.end_date.setText(DayUtility.createDateString(this.endCalendar));
        this.start_time.setText(DayUtility.createTimeString(this.startCalendar));
        this.end_time.setText(DayUtility.createTimeString(this.endCalendar));

    }

    private class onSaveButtonClick implements View.OnClickListener{
        @Override
        public void onClick(View v){
            String titleString = getText(title);
            if(!titleString.equals("")){
                String descriptionString = getText(description);
                Subject aSubject;
                if(startCalendar.compareTo(endCalendar) == 0) {
                    aSubject = new Subject(-1, titleString, descriptionString, "user", "userイベント", null, endCalendar);
                }
                else if (startCalendar.compareTo(endCalendar) < 0){
                    aSubject = new Subject(-1, titleString, descriptionString, "user", "userイベント", startCalendar, endCalendar);
                }
                else{
                    Toast.makeText(getContext(),"開始日時は終了日時よりも前の日時を入力してください",Toast.LENGTH_LONG).show();
                    return;
                }
                scheduleSetter.createCalendarEvent(aSubject);
            }
            else{
                Toast.makeText(getContext(),"タイトルを入力してください",Toast.LENGTH_LONG).show();
            }
            return;
        }
    }


    /**
     * 日付を入力欄をクリックしたときの処理
     */
    private class  onDateInputClick implements View.OnClickListener{
        private Calendar calendar;
        private EditText editText;
        public onDateInputClick(Calendar aCalendar,EditText aEditText){
            this.calendar = aCalendar;
            this.editText = aEditText;
        }
        @Override
        public void onClick(View v){
            DatePickerDialogFragment datePicker = new DatePickerDialogFragment(this.calendar,this.editText);
            datePicker.show(fragmentManager,"datePicker");
        }
    }

    /**
     * 時間を入力欄にクリックしたときの処理
     */
    private class onTimeInputClick implements  View.OnClickListener{
        private Calendar calendar;
        private EditText editText;
        public onTimeInputClick(Calendar aCalendar,EditText aEditText){
            this.calendar = aCalendar;
            this.editText = aEditText;
        }
        @Override
        public void onClick(View v){
            TimePickerDialogFragment timePicker = new TimePickerDialogFragment(this.calendar,this.editText);
            timePicker.show(fragmentManager,"timePicker");
        }
    }

    /**
     * TextInputEditTextから入力した文字を取得する
     * @param aEditText 入力欄
     * @return 入力した文字
     */
    private String getText(EditText aEditText){
        String aString = "";
        if(aEditText!=null) {
            Editable editable = aEditText.getText();
            if (editable != null) {
                SpannableStringBuilder sb = (SpannableStringBuilder) editable;
                aString = sb.toString();
            }
        }
        return aString;
    }

    /**
     * イベントの作成に成功した時の処理
     */
    public void notifySuccessCreate(){
        try {
            System.out.println(this.model.getJsonSchedule());
            FileUtility.writeFile("schedule.json", this.model.getJsonSchedule());
            Toast.makeText(this.getContext(), "作成しました。", Toast.LENGTH_SHORT).show();
        }catch (IOException anException){
            anException.printStackTrace();
            Toast.makeText(this.getContext(),"正しく書き込めませんでした",Toast.LENGTH_LONG).show();
            this.failedCalendarUpdate();
        }
    }


    /**
     * シケジュールの更新が終了したことを通知
     */
    public void notifyFinCalendarUpdate() {
        //スケジュールの内容を保存する
        try {
            FileUtility.writeFile("schedule.json", this.model.getJsonSchedule());
            Toast.makeText(this.getContext(), "更新しました。", Toast.LENGTH_SHORT).show();
        }catch (IOException anException){
            anException.printStackTrace();
            Toast.makeText(this.getContext(),"正しく書き込めませんでした",Toast.LENGTH_LONG).show();
            this.failedCalendarUpdate();
        }
        return;
    }

    /**
     * スケジュールの更新に失敗した場合の処理
     */
    public void failedCalendarUpdate(){
        Toast.makeText(this.getContext(), "作成に失敗しました。", Toast.LENGTH_LONG).show();
        return;
    }

}
