package com.instant.doctor.fragments.Patient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.instant.doctor.MainActivity;
import com.instant.doctor.R;
import com.instant.doctor.models.MpesaResponse;
import com.instant.doctor.models.PatientInfo;
import com.instant.doctor.notifications.Client;
import com.instant.doctor.payment.ConfirmPayment;
import com.instant.doctor.payment.Data;
import com.instant.doctor.payment.PaymentResponse;
import com.instant.doctor.payment.PaymentService;
import com.instant.doctor.utils.UserTypePrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentInstructions extends Fragment {
    Button button;
    Button proceedBtn;
    TextView tv_response;
    PatientInfo patientInfo;
    String sessionId;
    ProgressDialog dialog;
    MpesaResponse mpesaResponse;
    PaymentService service;


    final String BASE_URL = "https://instant-doctor-mpesa-service.herokuapp.com";

    final String TAG = "Payment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        sessionId = getArguments().getString("sessionId");
        UserTypePrefManager manager = new UserTypePrefManager(getActivity());
        manager.saveSessionID(sessionId);
        return inflater.inflate(R.layout.payment_instructions_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle("Payment");
        super.onViewCreated(view, savedInstanceState);
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);

        service = Client.getClient(BASE_URL).create(PaymentService.class);
        getActivity().setTitle("Payment");

        fetchPatientDetails();


        button = view.findViewById(R.id.pay);
        proceedBtn = view.findViewById(R.id.proceed);
        UserTypePrefManager manager = new UserTypePrefManager(getActivity());
        String requestID = manager.getMerchantID();
        if (requestID != null) {
            proceedBtn.setEnabled(true);
        }

        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPayment();
            }
        });
        tv_response = view.findViewById(R.id.tv_response);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String number = patientInfo.getPhoneNo();
                number = number.replaceFirst("0", "254");

                Data data = new Data(number);
                dialog.setMessage("Sending Request...");
                dialog.show();
                tv_response.setText("");
                service.makePayment(data).enqueue(new Callback<MpesaResponse>() {
                    @Override
                    public void onResponse(Call<MpesaResponse> call, Response<MpesaResponse> response) {
                        dialog.dismiss();
                        if (response.body() != null) {
                            mpesaResponse = response.body();
                            tv_response.setText("Payment request received awaiting processing");

                            UserTypePrefManager manager = new UserTypePrefManager(getActivity());
                            manager.saveMerchantID(mpesaResponse.getMerchantRequestID());
                            Log.d(TAG, "Payment Response: " + mpesaResponse.getResponseCode());
                            proceedBtn.setEnabled(true);
                        } else {
                            tv_response.setText("Error Connecting to server. (;");
                        }
                    }

                    @Override
                    public void onFailure(Call<MpesaResponse> call, Throwable t) {
                        dialog.dismiss();
                        String message = t.getMessage();
                        if (message != null) {
                            message = "Error Connecting to server. (;";
                        }

                        tv_response.setText(message);

                        Log.e(TAG, "Payment failure: ", t);
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        UserTypePrefManager manager = new UserTypePrefManager(getActivity());
        String requestID = manager.getMerchantID();
        if (requestID != null) {
            proceedBtn.setEnabled(true);
        }
    }

    private void confirmPayment() {
        UserTypePrefManager manager = new UserTypePrefManager(getActivity());
        String requestID = manager.getMerchantID();
        dialog.setMessage("Confirming payment...");
        dialog.show();
        if (requestID != null) {
            ConfirmPayment payment = new ConfirmPayment(requestID);
            service.confirmPayment(payment).enqueue(new Callback<PaymentResponse>() {
                @Override
                public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                    dialog.dismiss();
                    if (response.isSuccessful()) {
                        PaymentResponse paymentResponse = response.body();
                        Log.d(TAG, "Confirmation  Response: " + paymentResponse.getSuccess());
                        if (paymentResponse.getSuccess() == 1) {
                            //successful

//                            Toast.makeText(getContext(), "Payment Confirmation Successfully", Toast.LENGTH_LONG).show();
                            updatePayment();
                        } else {
                            Toast.makeText(getContext(), "Payment Not Made", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<PaymentResponse> call, Throwable t) {
                    dialog.dismiss();

                    Log.e(TAG, "Confirmation Failure: ", t);
                }
            });
        }

    }

    private void updatePayment(){
        UserTypePrefManager manager = new UserTypePrefManager(getActivity());
        String sessionID = manager.getSessionID();

        dialog.setMessage("Confirming Payment ...");
        dialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("sessions").document(sessionID).update("paymentId","paid")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                        UserTypePrefManager manager = new UserTypePrefManager(getActivity());
                        manager.deleteMerchantID();
                        manager.deleteSessionID();
                        Intent intent =new Intent(getActivity(), MainActivity.class);
                        intent.putExtra("beforeChat", "chats");
                        startActivity(intent);
                    }
                });
    }

    private void fetchPatientDetails() {
        dialog.setMessage("Loading...");
        dialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
