package com.example.scheduleapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import java.util.Calendar;
import java.util.Locale;

public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {


    private Calendar calendar;
    private EditText editText;
    public DatePickerDialogFragment(Calendar aCalendar,EditText aEditText){
        this.calendar = aCalendar;
        this.editText = aEditText;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),this, year, month, dayOfMonth);

        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //日付が選択されたときの処理
        this.calendar.set(Calendar.YEAR,year);
        this.calendar.set(Calendar.MONTH,month);
        this.calendar.set(Calendar.DAY_OF_MONTH,day);
        this.editText.setText(DayUtility.createDateString(this.calendar));
    }

}