package com.instant.doctor.fragments.Patient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    TextView patientName;
    private Button button;
    private String doctorId;
    private String doctorName;
    private ProgressDialog progressDialog;
    UserTypePrefManager userTypePrefManager;

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

        getActivity().setTitle("Symptoms");


        patientName=view.findViewById(R.id.patientName);
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Saving...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        et_specialConditions = view.findViewById(R.id.specialCondition);


        button = view.findViewById(R.id.symptoms_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
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
        String aCondition = et_specialConditions.getText().toString();
        Toast.makeText(getActivity(),"Your symptoms" +aCondition,Toast.LENGTH_LONG).show();

        final UserTypePrefManager manager = new UserTypePrefManager(getActivity());
        String patientId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        PatientSymptoms patientSymptoms = new PatientSymptoms(aCondition, symptomsList, patientId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Symptoms").add(patientSymptoms).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                progressDialog.dismiss();

                Toast.makeText(getActivity(), "information saved Successfully ", Toast.LENGTH_SHORT).show();

                String patientId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String symptomsId = documentReference.getId();
                String patientName = manager.getUserName();
                long time = new Date().getTime();
                String paymentId=null;

                MedicalSession session = new MedicalSession(
                        patientId, symptomsId, doctorId, doctorName,
                        mDoctorInfo.getImageURL(), mDoctorInfo.getSpecialization(), patientName, time,paymentId);

                createASession(session);


//                Intent intent = new Intent(getActivity(), MainActivity.class);
//                intent.putExtra("beforeChat", "true");
//                startActivity(intent);


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
                        String documentId=documentReference.getId();
                        Bundle bundle=new Bundle();
                        bundle.putString("sessionId",documentId);


                        PaymentInstructions paymentInstructions=new PaymentInstructions();
                        paymentInstructions.setArguments(bundle);

                        FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.patient_info_frame,paymentInstructions).commit();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "error saving data: ", e);
            }
        });


    }
}

