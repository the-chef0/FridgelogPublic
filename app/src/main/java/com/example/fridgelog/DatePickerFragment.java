package com.example.fridgelog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends AppCompatDialogFragment implements DatePickerDialog.OnDateSetListener
{
    final Calendar c = Calendar.getInstance();
    private int selectedDay;
    private int selectedMonth;
    private int selectedYear;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Calendar c = Calendar.getInstance();
        //We get the values from user input
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), DatePickerFragment.this, year, month, day);
    }
    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        //We set the calendar object to the user-selected values
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        //Then we put them into an array to return back to the fragment
        selectedDay = c.get(Calendar.DAY_OF_MONTH);
        selectedMonth = c.get(Calendar.MONTH);
        selectedYear = c.get(Calendar.YEAR);
        int[] selectedDate = new int[3];
        selectedDate[0] = selectedDay;
        selectedDate[1] = selectedMonth;
        selectedDate[2] = selectedYear;

        getTargetFragment().onActivityResult(
                getTargetRequestCode(),
                Activity.RESULT_OK,
                new Intent().putExtra("selectedDate", selectedDate)
        );
    }
}