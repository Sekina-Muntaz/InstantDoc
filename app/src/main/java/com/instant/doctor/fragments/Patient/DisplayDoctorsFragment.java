package com.instant.doctor.fragments.Patient;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.instant.doctor.Adapters.DoctorAdapter;
import com.instant.doctor.R;
import com.instant.doctor.models.DoctorInfo;

import java.util.ArrayList;
import java.util.List;

public class DisplayDoctorsFragment extends Fragment {
    private RecyclerView recyclerView;
    private DoctorAdapter adapter;
    private List<DoctorInfo> doctorList;
    EditText et_search;

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

        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.doctor_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);

        doctorList = new ArrayList<>();
        adapter = new DoctorAdapter(getActivity(), doctorList);

        recyclerView.setAdapter(adapter);

        et_search=view.findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//                searchDoctors(charSequence.toString());
                String searchString  = charSequence.toString();
                if(searchString.length() == 0){
                    adapter.setDoctorList(doctorList);

                }else {
                    List<DoctorInfo> doctorSearchList = new ArrayList<>();
                    for (DoctorInfo doctorInfo : doctorList) {
                        if (doctorInfo.getSpecialization().toLowerCase().contains(searchString.trim().toLowerCase())) {
                            doctorSearchList.add(doctorInfo);
                        }
                    }

                    adapter.setDoctorList(doctorSearchList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



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

