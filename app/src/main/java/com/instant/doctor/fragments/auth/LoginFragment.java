package com.instant.doctor.fragments.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.instant.doctor.MainActivity;
import com.instant.doctor.R;
import com.instant.doctor.Splash;
import com.instant.doctor.fragments.Doctor.DisplayPatientFragment;
import com.instant.doctor.fragments.Patient.DisplayDoctorsFragment;
import com.instant.doctor.models.DoctorInfo;
import com.instant.doctor.models.PatientInfo;
import com.instant.doctor.utils.UserTypePrefManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

public class LoginFragment extends Fragment {
    private EditText email;
    private EditText password;
    private Button button;
    private TextView signUpLink;
    private TextView forgotpassword;
    ProgressDialog progressDialog;
    FirebaseFirestore db;
    FirebaseUser user;




    FirebaseAuth auth;

    public static final String TAG = "LoginFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setTitle("Signing In..");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

//        Toolbar toolbar=findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Sign In");
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        button = view.findViewById(R.id.signIn_button);
        signUpLink = view.findViewById(R.id.signUpTextView);
        forgotpassword = view.findViewById(R.id.ForgotPasswordTextView);

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = ((Splash) v.getContext()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.splash_frame, new ForgotPasswordFragment(), "Forgot Password Fragment").commit();

            }
        });


        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction transaction = ((Splash) v.getContext()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.splash_frame, new RegisterFragment(), "Register Fragment").commit();

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String user_email = email.getText().toString();
                String user_password = password.getText().toString();

                if (TextUtils.isEmpty(user_email) || TextUtils.isEmpty(user_password)) {
                    Toast.makeText(getActivity(),"All fields must be filled",Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(user_email, user_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        user = auth.getCurrentUser();
                                        Log.d(TAG, "Current user"+ user);

                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                        startActivity(intent);
                                    } else {

                                       // Log.d(TAG, "onComplete: "+ task.getResult().getUser().toString());
                                        Toast.makeText(getActivity(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                                    }

                                }



                            });
                }

            }
        });



    }




}
