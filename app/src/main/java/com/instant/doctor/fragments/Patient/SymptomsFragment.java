package com.instant.doctor.fragments.Patient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.instant.doctor.ChatActivity;
import com.instant.doctor.MainActivity;
import com.instant.doctor.PatientPersonalInfoActivity;
import com.instant.doctor.R;

import com.instant.doctor.fragments.Doctor.DisplayPatientFragment;
import com.instant.doctor.models.DoctorInfo;
import com.instant.doctor.models.MedicalSession;
import com.instant.doctor.models.PatientSymptoms;
import com.instant.doctor.utils.UserTypePrefManager;
import com.nex3z.togglebuttongroup.MultiSelectToggleGroup;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;
import com.nex3z.togglebuttongroup.button.LabelToggle;

import java.util.ArrayList;
import java.util.Date;

public class SymptomsFragment extends Fragment {

    private ArrayList<String> symptomsList = new ArrayList<>();
    private EditText et_specialConditions;
    private Button button;
    private String doctorId;
    private String doctorName;

    private DoctorInfo mDoctorInfo;

    public static final String TAG = "SymptomsFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDoctorInfo = (DoctorInfo) getArguments().get("doctor");
        doctorId = mDoctorInfo.getId();
        doctorName = mDoctorInfo.getName();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.symptoms_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar2);

//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_specialConditions = view.findViewById(R.id.specialCondition);

        button = view.findViewById(R.id.symptoms_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePatientSymptoms();

            }
        });

        MultiSelectToggleGroup multiSymptoms = view.findViewById(R.id.group_select_symptoms);
        final String[] symptoms = getResources().getStringArray(R.array.symptoms);
        for (String text : symptoms) {
            LabelToggle toggle = new LabelToggle(getContext());
            toggle.setText(text);
            multiSymptoms.addView(toggle);

            multiSymptoms.setOnCheckedChangeListener(new MultiSelectToggleGroup.OnCheckedStateChangeListener() {
                @Override
                public void onCheckedStateChanged(MultiSelectToggleGroup group, int checkedId, boolean isChecked) {
                    LabelToggle toggle = view.findViewById(checkedId);
                    String text = toggle.getText().toString();

                    if (isChecked) {
                        symptomsList.add(text);
                    } else {
                        symptomsList.remove(text);
                    }

                }
            });
        }
    }

    public void savePatientSymptoms() {
        String conditions = et_specialConditions.getText().toString();

        final UserTypePrefManager manager = new UserTypePrefManager(getActivity());
        String patientId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        PatientSymptoms patientSymptoms = new PatientSymptoms(conditions, symptomsList, patientId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Symptoms").add(patientSymptoms).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                Toast.makeText(getActivity(), "information saved Successfully ", Toast.LENGTH_SHORT).show();

                String patientId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String symptomsId = documentReference.getId();
                String patientName = manager.getUserName();
                long time = new Date().getTime();

                MedicalSession session = new MedicalSession(
                        patientId, symptomsId, doctorId, doctorName,
                        mDoctorInfo.getImageURL(), mDoctorInfo.getSpecialization(), patientName, time);

                createASession(session);


                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("beforeChat", "true");
                startActivity(intent);


            }
        });

    }

    public void createASession(MedicalSession session) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("sessions")
                .add(session)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getContext(), "Info saved successfully", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "error saving data: ", e);
            }
        });


    }
}
