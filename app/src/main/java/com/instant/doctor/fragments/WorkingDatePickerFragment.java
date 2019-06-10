package com.instant.doctor.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WorkingDatePickerFragment extends DialogFragment {
    DatePickerDialog.OnDateSetListener endDateSet;
//    String minDate;

    public WorkingDatePickerFragment() {}


    @SuppressLint("NewApi")
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
//        year = args.getInt("year");
//        month = args.getInt("month");
//        day = args.getInt("day");
//        minDate = args.getString("minDate");

    }



    public void setCallBack(DatePickerDialog.OnDateSetListener endDate) {
        endDateSet = endDate;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // Create a new instance of DatePickerDialog and return it

//        vdre.add(Calendar.DAY_OF_MONTH,noOfDays);


        SimpleDateFormat dateFormatter=new SimpleDateFormat("dd-MM-yyyy");
//        Date sdsd = new Date();
//        try {
//            sdsd= dateFormatter.parse(minDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        Calendar vdre=Calendar.getInstance();
//        vdre.setTime(sdsd);

        Calendar maxCal=Calendar.getInstance();
        maxCal.add(Calendar.MONTH,6);

        DatePickerDialog datePickerDialog=new DatePickerDialog(getActivity(), endDateSet, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        return  datePickerDialog;
    }






}
