package com.instant.doctor.fragments.auth;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.instant.doctor.R;
import com.instant.doctor.Splash;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class RegisterFragment extends Fragment {
    private EditText email;
    private EditText password;
    private TextView signIn;
    Button button;
    ProgressDialog progressDialog;


    FirebaseAuth auth;
    DatabaseReference reference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.register_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);
//        progressDialog.setIndeterminate();




        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        button = view.findViewById(R.id.signUp_button);
        signIn =view.findViewById(R.id.signInTextView);

        auth = FirebaseAuth.getInstance();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                FragmentTransaction transaction=((Splash)v.getContext()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.splash_frame,new LoginFragment(),"Login Fragment").commit();

            }
        });

        button.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                progressDialog.show();


                String user_email = email.getText().toString();
                String user_password = password.getText().toString();

                if (TextUtils.isEmpty(user_email) | TextUtils.isEmpty(user_password)) {
                    progressDialog.dismiss();

                    Toast.makeText(getActivity(), "All fields must be filled", Toast.LENGTH_SHORT).show();

                } else if (password.length() < 8) {
                    progressDialog.dismiss();

                    Toast.makeText(getActivity(), "Password must be atleast 8 characters long", Toast.LENGTH_SHORT).show();

                } else {
                    register(user_email, user_password);

                }
            }
        });




    }


    private void register(String email, final String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userId = firebaseUser.getUid();

                            FragmentTransaction transaction=((Splash) getView().getContext()).getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.splash_frame,new UserTypeFragment(),"UserType Fragment").commit();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);



//                            HashMap<String, String> hashMap = new HashMap<>();
//                            hashMap.put("id", userId);
//
//                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
////                                        progressDialog.dismiss();
////                                        Intent intent = new Intent(getActivity(), UserTypeActivity.class);
////                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
////                                        startActivity(intent);
//
//                                    }
//                                }
//                            });


                        } else {
                            progressDialog.dismiss();

                            Toast.makeText(getActivity(), "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

}
