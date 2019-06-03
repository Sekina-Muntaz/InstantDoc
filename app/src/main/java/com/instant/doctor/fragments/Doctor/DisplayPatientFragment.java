package com.instant.doctor.fragments.Doctor;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.instant.doctor.Adapters.PatientAdapter;
import com.instant.doctor.R;
import com.instant.doctor.fragments.Patient.BeforeChatFragmentPatient;
import com.instant.doctor.models.MedicalSession;
import com.instant.doctor.models.PatientInfo;

import java.util.ArrayList;
import java.util.List;

public class DisplayPatientFragment extends Fragment implements PatientAdapter.OnPatientSelected {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<MedicalSession> medicalSessions;

    private FirebaseFirestore db;
    public static final String TAG = "DisplayPatientFragment";

    TextView noPatientsTv;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.display_patients_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Patients");


        recyclerView = view.findViewById(R.id.patient_recyclerView);
        noPatientsTv = view.findViewById(R.id.tv_nopatient_data);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        medicalSessions = new ArrayList<>();
        adapter = new PatientAdapter(getActivity(), medicalSessions, this);

        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();


        String doctorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading ...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        db.collection("sessions")
                .whereEqualTo("doctor_id",doctorId)
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            Log.d(TAG, "data fetched: "+list.size());


                            for (DocumentSnapshot d : list) {
                                MedicalSession session = d.toObject(MedicalSession.class);
                                Log.d(TAG, "Document Id: "+ d.getId());
                                session.setId(d.getId());
                                medicalSessions.add(session);
                            }
                            adapter.notifyDataSetChanged();
                        }else {
                            noPatientsTv.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }

    @Override
    public void patientSelected(String patientId, String medicalSessionId) {
        BeforeChatFragmentDoctor fragment = new BeforeChatFragmentDoctor();
        Bundle bundle = new Bundle();
        bundle.putString("patientId", patientId);
        bundle.putString("sessionId", medicalSessionId);
        fragment.setArguments(bundle);
        FragmentTransaction ts = getActivity().getSupportFragmentManager().beginTransaction();
        ts.replace(R.id.content_frame, fragment).commit();
    }
}

