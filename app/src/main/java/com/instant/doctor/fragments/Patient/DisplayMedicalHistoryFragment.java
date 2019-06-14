package com.instant.doctor.fragments.Patient;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.instant.doctor.Adapters.MedicalHistoryAdapter;
import com.instant.doctor.Adapters.MedicalNotesAdapter;
import com.instant.doctor.R;
import com.instant.doctor.models.MedicalNote;

import java.util.ArrayList;
import java.util.List;

public class DisplayMedicalHistoryFragment extends Fragment {
    RecyclerView recyclerView;
    List<MedicalNote> medicalNoteList;
    private RecyclerView.Adapter adapter;
    FirebaseFirestore db;
    public static final String TAG="History";

    TextView tv_NoMedicalHistory;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.display_medical_history_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Medical History");


        tv_NoMedicalHistory=view.findViewById(R.id.noNote);


        recyclerView=view.findViewById(R.id.medical_history_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.addItemDecoration();
        medicalNoteList = new ArrayList<>();
        adapter = new MedicalHistoryAdapter(getActivity(),medicalNoteList);

        recyclerView.setAdapter(adapter);
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading ...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        db = FirebaseFirestore.getInstance();
        String patientId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("medicalNotes")
                .whereEqualTo("patientId",patientId)
                .orderBy("time", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                progressDialog.dismiss();
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    Log.d(TAG, "data fetched: "+list.size());


                    for (DocumentSnapshot d : list) {
                        MedicalNote notes = d.toObject(MedicalNote.class);
                        Log.d(TAG, "Document Id: "+ d.getId());
                        medicalNoteList.add(notes);
                    }
                    adapter.notifyDataSetChanged();
                }else {
                    tv_NoMedicalHistory.setVisibility(View.VISIBLE);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.e(TAG, "data fetch failed: ",e);
            }
        });
    }

    }

