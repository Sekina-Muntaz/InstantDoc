package com.instant.doctor.fragments.Doctor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.badoualy.datepicker.DatePickerTimeline;
import com.github.badoualy.datepicker.MonthView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import com.instant.doctor.DoctorPersonalInfoActivity;
import com.instant.doctor.MainActivity;
import com.instant.doctor.R;
import com.instant.doctor.SetDoctorsAvailabilityActivity;
import com.instant.doctor.models.DoctorAvailability;
import com.nex3z.togglebuttongroup.MultiSelectToggleGroup;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;
import com.nex3z.togglebuttongroup.button.CircularToggle;
import com.nex3z.togglebuttongroup.button.LabelToggle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class SetDoctorAvailabilityFragment extends Fragment {
    private ArrayList<String> doctorAvailabilityTimes = new ArrayList<>();
    private String availableDate;
    private Button button;

    private List<String> mTimes;
    private Map<String, List<String>> mDayTimes;


    MultiSelectToggleGroup multiSelectHours;

    public static final String TAG = "DoctorAvailability";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.set_doctor_availability_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Availability");



        button = view.findViewById(R.id.submit_availability);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAvailableTimes();

            }
        });


        mTimes = new ArrayList<>();
        mDayTimes = new HashMap<>();
        availableDate = "SUN";


        final SingleSelectToggleGroup single = view.findViewById(R.id.group_days);

        multiSelectHours = view.findViewById(R.id.group_appointment);
        single.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
                CircularToggle day_button = view.findViewById(checkedId);
                String specific_day = day_button.getText().toString();

                availableDate = specific_day;


                mTimes = new ArrayList<>();
                multiSelectHours.clearCheck();

//
//                Toast.makeText(getActivity(), "You have selected" + specific_day, Toast.LENGTH_SHORT).show();
//
//                Log.d(TAG, "day selected: " + availableDate);


            }
        });
        init(view);

    }


    // select the appointment time toggle
    private void init(View view) {

//        MultiSelectToggleGroup multiSelectHours = view.findViewById(R.id.group_appointment);

        final String[] availableTimes = getResources().getStringArray(R.array.availableTimes);
        for (String text : availableTimes) {
            final LabelToggle toggle = new LabelToggle(getContext());
            toggle.setText(text);
            multiSelectHours.addView(toggle);

            multiSelectHours.setOnCheckedChangeListener(new MultiSelectToggleGroup.OnCheckedStateChangeListener() {
                @Override
                public void onCheckedStateChanged(MultiSelectToggleGroup group, int checkedId, boolean isChecked) {

                    if (isChecked) {
//                        doctorAvailabilityTimes.add(availableTimes[checkedId - 1]);
                        if (!mTimes.contains(availableTimes[checkedId - 1])) {
                            mTimes.add(availableTimes[checkedId - 1]);
                        }

//                        Toast.makeText(getActivity(), "YOU HAVE SELECTED" + availableTimes[checkedId - 1], Toast.LENGTH_LONG).show();
                    } else {

//                        doctorAvailabilityTimes.remove(availableTimes[checkedId - 1]);

                        mTimes.remove(availableTimes[checkedId - 1]);


                        //Toast.makeText(getActivity(), "YOU HAVE Removed" + availableTimes[checkedId - 1], Toast.LENGTH_LONG).show();


                    }

                    mDayTimes.put(availableDate, mTimes);


                    Log.d(TAG, "time selected: " + mTimes);
                    Log.d(TAG, "day - time selected: " + mDayTimes);


                }
            });
        }
    }

    private void saveAvailableTimes() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("my_prefs", 0);
        String doctorDocumentId = sharedPref.getString("doctorId", null);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("doctors")
                .document(doctorDocumentId)
                .update("availability", mDayTimes)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(),
                                "Availabilty dates updated", Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
//
//                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//                        fragmentTransaction.replace(R.id.doc_info_frame, new DisplayPatientFragment()).commit();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                });


    }


}

