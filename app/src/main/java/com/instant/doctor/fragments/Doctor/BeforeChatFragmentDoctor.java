package com.instant.doctor.fragments.Doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.instant.doctor.ChatActivity;
import com.instant.doctor.R;
import com.instant.doctor.models.MedicalSession;
import com.instant.doctor.models.PatientInfo;
import com.instant.doctor.models.PatientSymptoms;

import java.util.Date;
import java.util.List;

public class BeforeChatFragmentDoctor extends Fragment {
    private TextView tv_patientInfo;
    private TextView tv_patientSymptoms;
    private Button startChatButton;
    private FirebaseFirestore db;

//    private PatientInfo patient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.before_chat_doctor_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        tv_patientInfo = view.findViewById(R.id.patient_personal_info);
        tv_patientSymptoms = view.findViewById(R.id.patient_symptoms);
        startChatButton = view.findViewById(R.id.Start_chat_button);




        String doctorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final String patientId = getArguments().getString("patientId");
        final String sessionId = getArguments().getString("sessionId");

        startChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("medical-session-id", sessionId);
                intent.putExtra("receiver-id", patientId);
                startActivity(intent);
            }
        });


        db.collection("patients")
                .whereEqualTo("id", patientId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : list) {
                                PatientInfo patient = d.toObject(PatientInfo.class);

                                Date dob = new Date(patient.getDate());
                                Date currentDate = new Date();
                                int age = currentDate.getYear() - dob.getYear();
                                StringBuilder patientStringInfo = new StringBuilder();

                                patientStringInfo.append("Name: " + patient.getName()).append("\n\n");
                                patientStringInfo.append("Gender: " + patient.getGender()).append("\n\n");
                                patientStringInfo.append("Age : " + age).append("\n\n");

                                tv_patientInfo.setText(patientStringInfo.toString());


                            }

                        }

                    }
                });


        db.collection("Symptoms")
                .whereEqualTo("patientId", patientId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : list) {
                                PatientSymptoms symptomsList = d.toObject(PatientSymptoms.class);

                                List<String> symptoms = symptomsList.getSymptoms();
                                String specialConditions = symptomsList.getSpecialConditions();

                                StringBuilder symptomsString = new StringBuilder();
                                symptomsString.append("Symptoms: \n\n");
                                for (String symptom : symptoms) {
                                    symptomsString.append(symptom);
                                    symptomsString.append("\n");
                                }
                                symptomsString.append("\n\n");
                                symptomsString.append("Special Conditions \n\n");
                                symptomsString.append(specialConditions);

                                tv_patientSymptoms.setText(symptomsString.toString());

                            }

                        }

                    }
                });


    }
}
