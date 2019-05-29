package com.instant.doctor.fragments.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.instant.doctor.DoctorPersonalInfoActivity;
import com.instant.doctor.PatientPersonalInfoActivity;
import com.instant.doctor.R;
import com.instant.doctor.Splash;
import com.instant.doctor.fragments.Doctor.DoctorPersonalInfoFragment;
import com.instant.doctor.fragments.Patient.PatientPersonalInfoFragment;
import com.instant.doctor.utils.UserTypePrefManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class UserTypeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_type_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Who are you?");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Button patientBtn = view.findViewById(R.id.patient_btn);
        Button doctorBtn =view. findViewById(R.id.doctor_btn);

        patientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UserTypePrefManager manager = new UserTypePrefManager(getActivity());
                manager.addUserType(0);
                Intent intent = new Intent(getActivity(), PatientPersonalInfoActivity.class);
                startActivity(intent);




            }
        });

        doctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentTransaction transaction=((Splash)v.getContext()).getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.splash_frame,new DoctorFragmentReg(),"Doctor  Personal info").commit();

                UserTypePrefManager manager = new UserTypePrefManager(getActivity());
                manager.addUserType(1);
                Intent intent = new Intent(getActivity(), DoctorPersonalInfoActivity.class);
//                intent.putExtra("Doctordata",1);
                startActivity(intent);


            }
        });
    }
    }
