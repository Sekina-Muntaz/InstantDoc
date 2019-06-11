package com.instant.doctor.fragments.Patient;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.instant.doctor.ChatActivity;
import com.instant.doctor.R;
import com.instant.doctor.models.PatientInfo;
import com.instant.doctor.notifications.Client;
import com.instant.doctor.payment.Data;
import com.instant.doctor.payment.MyResponse;
import com.instant.doctor.payment.PaymentService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PaymentInstructions extends Fragment {
    Button button;
    PatientInfo patientInfo;
    String sessionId;
    ProgressDialog dialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        sessionId = getArguments().getString("sessionId");
        return inflater.inflate(R.layout.payment_instructions_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);

        fetchPatientDetails();



        button=view.findViewById(R.id.pay);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PaymentService service = Client.getClient("http://s")
//                        .create(PaymentService.class);
//                Data data = new Data();
//                service.makePayment(data).enqueue(new Callback<MyResponse>() {
//                    @Override
//                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<MyResponse> call, Throwable t) {
//
//                    }
//                });
            }
        });
    }

    private void fetchPatientDetails(){

        dialog.show();
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("patients").whereEqualTo("id", userId)
                .limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                dialog.dismiss();

                if (task.isSuccessful()) {


                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {

                        patientInfo = queryDocumentSnapshot.toObject(PatientInfo.class);


                        break;

                    }

                }
            }
        });
    }
}
