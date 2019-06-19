package com.instant.doctor.fragments.Doctor;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.instant.doctor.R;
import com.instant.doctor.models.MedicalSession;


import java.util.List;


public class PatientStatisticsFragment extends Fragment {
    TextView tv_patientNumber;
    TextView tv_totalAmount;
    int numberOfPatients;

    public static final String TAG = "PatientStatistics";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.patient_statistics_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Your Number of Patients");

        tv_patientNumber = view.findViewById(R.id.actualNumberOfPatients);
        tv_totalAmount = view.findViewById(R.id.actualAmount);

        getNumberOfPatients();


    }

    public void getNumberOfPatients() {
        String doctor_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("sessions").whereEqualTo("doctor_id", doctor_id).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<MedicalSession> list = queryDocumentSnapshots.toObjects(MedicalSession.class);
                        numberOfPatients = list.size();

                        tv_patientNumber.setText(String.valueOf(numberOfPatients));
                        totalAmount();
                    }
                });
        // figure out how to get patients of a particualr day.


    }

    public void totalAmount() {

        int totalAmount =numberOfPatients;
         String amt=String.valueOf(totalAmount);


        tv_totalAmount.setText(amt + "Ksh");
    }

}
