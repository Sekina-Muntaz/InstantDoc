package com.instant.doctor.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.instant.doctor.PatientPersonalInfoActivity;
import com.instant.doctor.R;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    DatePickerDialog dialog;
    DatePickerInterface dateInterface;


//    public DatePickerFragment() {
//        // Required empty public constructor
//    }


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        TextView textView = new TextView(getActivity());
//        textView.setText(R.string.hello_blank_fragment);
//        return textView;
//    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar c=Calendar.getInstance();
        int year=c.get(Calendar.YEAR);
        int month=c.get(Calendar.MONTH);
        int day=c.get(Calendar.DAY_OF_MONTH);

         dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        return dialog;

//       return new DatePickerDialog(getActivity(),this,year,month,day);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            dateInterface=(DatePickerInterface)getActivity();

        }catch (ClassCastException e){
            throw new ClassCastException("error")  ;
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {


        dateInterface.getProcessedDate(year, month, day);
//        PatientPersonalInfoActivity activity = (PatientPersonalInfoActivity)getActivity();
//        assert activity != null;
//        dialog.processDatePickerResult(year, month,day);
    }

    public interface DatePickerInterface{

         void getProcessedDate(int year, int month, int day);

    }
}
