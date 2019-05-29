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
import com.instant.doctor.Adapters.DoctorAdapter;
import com.instant.doctor.Adapters.MedicalNotesAdapter;
import com.instant.doctor.R;
import com.instant.doctor.models.MedicalNote;
import com.instant.doctor.models.MedicalSession;

import java.util.ArrayList;
import java.util.List;

public class DisplayMedicalNotesFragment extends Fragment {
    public static final String TAG = "MedicalNotesFragment";

    RecyclerView recyclerView;
    List<MedicalNote> medicalNoteList;
    private RecyclerView.Adapter adapter;
    FirebaseFirestore db;

    TextView tv_NoMedicalNote;
    ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.display_medical_notes_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_NoMedicalNote=view.findViewById(R.id.noMedicalNote);


        recyclerView = view.findViewById(R.id.display_medical_notes_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        medicalNoteList = new ArrayList<>();
        adapter = new MedicalNotesAdapter(getActivity(),medicalNoteList);

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
//                        notes.setId(d.getId());
                        medicalNoteList.add(notes);
                    }
                    adapter.notifyDataSetChanged();
                }else {
                    tv_NoMedicalNote.setVisibility(View.VISIBLE);
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
