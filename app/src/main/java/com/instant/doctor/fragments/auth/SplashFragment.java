package com.instant.doctor.fragments.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.instant.doctor.R;
import com.instant.doctor.Splash;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class SplashFragment extends Fragment {

    Button registerBtn,loginBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.splash_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerBtn=view.findViewById(R.id.buttonRegister);
        loginBtn=view.findViewById(R.id.buttonLogin);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                FragmentTransaction transaction=((Splash)v.getContext()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.splash_frame,new RegisterFragment(),"Register Fragment").commit();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                FragmentTransaction transaction=((Splash)v.getContext()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.splash_frame,new LoginFragment(),"Login Fragment").commit();

            }
        });

    }
    
}
