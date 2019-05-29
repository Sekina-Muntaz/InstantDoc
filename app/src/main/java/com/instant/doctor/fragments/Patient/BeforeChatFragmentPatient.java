package com.instant.doctor.fragments.Patient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.instant.doctor.Adapters.DoctorAdapter;
import com.instant.doctor.Adapters.RequestedDoctorsAdapter;
import com.instant.doctor.ChatActivity;
import com.instant.doctor.R;
import com.instant.doctor.models.DoctorInfo;
import com.instant.doctor.models.MedicalSession;

import java.util.ArrayList;
import java.util.List;

public class BeforeChatFragmentPatient extends Fragment  implements RequestedDoctorsAdapter.OnDoctorSelected {
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private RequestedDoctorsAdapter adapter;
    private List<MedicalSession> medicalSessions;

    private static final String TAG = BeforeChatFragmentPatient.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.before_chat_patient_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        recyclerView = view.findViewById(R.id.requested_doctors);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        medicalSessions = new ArrayList<>();
        adapter = new RequestedDoctorsAdapter(getActivity(), this);

        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        String patientId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("sessions")
                .whereEqualTo("patient_id", patientId)
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            Log.d(TAG, "data: " + list);

                            for (DocumentSnapshot d : list) {
                                MedicalSession session = d.toObject(MedicalSession.class);
                                session.setId(d.getId());
                                medicalSessions.add(session);
                            }

                            adapter.setRequestedDoc(medicalSessions);

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "error fetching data: ",e );
            }
        });
    }

    @Override
    public void doctorSelected(String medicalSessionId, String doctorId) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("medical-session-id", medicalSessionId);
        intent.putExtra("receiver-id", doctorId);
        startActivity(intent);
    }
}
