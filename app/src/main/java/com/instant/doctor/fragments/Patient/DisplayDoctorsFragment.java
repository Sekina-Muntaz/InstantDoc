package com.instant.doctor.fragments.Patient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.instant.doctor.Adapters.DoctorAdapter;
import com.instant.doctor.R;
import com.instant.doctor.models.DoctorInfo;

import java.util.ArrayList;
import java.util.List;

public class DisplayDoctorsFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<DoctorInfo> doctorList;

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.display_doctors_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Doctors");


        recyclerView = view.findViewById(R.id.doctor_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        doctorList = new ArrayList<>();
        adapter = new DoctorAdapter(getActivity(), doctorList);

        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        db.collection("doctors").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for (DocumentSnapshot d : list) {
                        DoctorInfo aDoctor = d.toObject(DoctorInfo.class);
                        doctorList.add(aDoctor);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }
}

