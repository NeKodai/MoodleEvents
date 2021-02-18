package com.example.scheduleapp;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Locale;

public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private Calendar calendar;
    private EditText editText;
    public TimePickerDialogFragment(Calendar aCalendar,EditText aEditText){
        this.calendar = aCalendar;
        this.editText = aEditText;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, hour, minute, true);
        return timePickerDialog;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        this.calendar.set(Calendar.MINUTE,minute);
        this.editText.setText(DayUtility.createTimeString(this.calendar));
    }

}