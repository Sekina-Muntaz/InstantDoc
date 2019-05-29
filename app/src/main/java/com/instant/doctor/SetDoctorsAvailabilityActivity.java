package com.instant.doctor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.badoualy.datepicker.DatePickerTimeline;
import com.github.badoualy.datepicker.MonthView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.instant.doctor.fragments.Doctor.DisplayPatientFragment;
import com.instant.doctor.models.DoctorAvailability;
import com.nex3z.togglebuttongroup.MultiSelectToggleGroup;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;
import com.nex3z.togglebuttongroup.button.LabelToggle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

public class SetDoctorsAvailabilityActivity extends AppCompatActivity {
    private ArrayList<String> doctorAvailabilityTimes = new ArrayList<>();
    private String availableDate;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_availability_doctors);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Set Availability");

        button=findViewById(R.id.submit_availability);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAvailableTimes();

            }
        });


        //select date
        DatePickerTimeline timeline = findViewById(R.id.timeline);
        timeline.setDateLabelAdapter(new MonthView.DateLabelAdapter() {
            @Override
            public CharSequence getLabel(Calendar calendar, int index) {
                return Integer.toString(calendar.get(Calendar.MONTH) + 1) + "/" + (calendar.get(Calendar.YEAR) % 2000);
            }
        });

        timeline.setOnDateSelectedListener(new DatePickerTimeline.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int index) {

                Calendar c = Calendar.getInstance();
                month += 1;
                c.set(year, month, day);

                Date date = c.getTime();
                String dateString = day + " / " + month + " / " + year;
                availableDate = String.valueOf(dateString);

                Toast.makeText(SetDoctorsAvailabilityActivity.this,"the date is"+availableDate,Toast.LENGTH_LONG).show();


//                String dates = day + " / " + month + " / " + year;

            }
        });

        timeline.setFirstVisibleDate(2019, Calendar.JULY, 19);
        timeline.setLastVisibleDate(2020, Calendar.JULY, 19);
        init();

    }


    // select the appointment time toggle
    private void init() {
//        SingleSelectToggleGroup singleHours = findViewById(R.id.group_select_appointment_hours);
//        singleHours.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
//                //Log.v(LOG_TAG, "onCheckedChanged(): checkedId = " + checkedId);
//                Toast.makeText(SetDoctorsAvailabilityActivity.this, checkedId, Toast.LENGTH_SHORT).show();
//            }
//        });

        MultiSelectToggleGroup multiSelectHours = findViewById(R.id.group_appointment);
        final String[] availableTimes = getResources().getStringArray(R.array.availableTimes);
        for (String text : availableTimes) {
            LabelToggle toggle = new LabelToggle(this);
            toggle.setText(text);
            multiSelectHours.addView(toggle);

            multiSelectHours.setOnCheckedChangeListener(new MultiSelectToggleGroup.OnCheckedStateChangeListener() {
                @Override
                public void onCheckedStateChanged(MultiSelectToggleGroup group, int checkedId, boolean isChecked) {

                    if (isChecked) {
                        doctorAvailabilityTimes.add(availableTimes[checkedId - 1]);



                        Toast.makeText(SetDoctorsAvailabilityActivity.this, "YOU HAVE SELECTED" + availableTimes[checkedId - 1], Toast.LENGTH_LONG).show();
                    }else {

                        doctorAvailabilityTimes.remove(availableTimes[checkedId - 1]);

                        Toast.makeText(SetDoctorsAvailabilityActivity.this, "YOU HAVE Removed" + availableTimes[checkedId - 1], Toast.LENGTH_LONG).show();


                    }

                }
            });
        }
    }

    private void saveAvailableTimes() {
        SharedPreferences sharedPref = getSharedPreferences("my_prefs", 0);
        String doctorDocumentId = sharedPref.getString("doctorId", null);


        DoctorAvailability doctorAvailability = new DoctorAvailability();
        doctorAvailability.setDate(availableDate);
        doctorAvailability.setTime(doctorAvailabilityTimes);

       HashMap<String,Object>  doctorAvailabilities =new HashMap<>();
        doctorAvailabilities.put("days",doctorAvailability);
//
//        Map<String, List<DoctorAvailability>> map = new HashMap<>();
//        map.put();
////
//        HashMap<String,List<DoctorAvailability>> hashMap=new HashMap<>();
//        hashMap.put("days",doctorAvailability);



        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("doctors").document(doctorDocumentId)
                .update("availability", FieldValue.arrayUnion(doctorAvailability))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SetDoctorsAvailabilityActivity.this,
                                "Availabilty dates updated", Toast.LENGTH_LONG).show();

                        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.doc_info_frame,new DisplayPatientFragment()).commit();
                    }
                });


    }
}