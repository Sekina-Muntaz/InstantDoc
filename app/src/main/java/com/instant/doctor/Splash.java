package com.instant.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.instant.doctor.fragments.auth.LoginFragment;
import com.instant.doctor.fragments.auth.RegisterFragment;
import com.instant.doctor.fragments.auth.SplashFragment;
import com.instant.doctor.fragments.auth.UserTypeFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class Splash extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (user != null) {
            Intent intent = getIntent();

            if(intent.hasExtra("detailsNotFilled")){
                changeFragment(4);
            }else {
                intent = new Intent(Splash.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } else {
            changeFragment(1);

        }


    }

    public void changeFragment(int page) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (page) {

            case 1:

                transaction.replace(R.id.splash_frame, new SplashFragment(), "Splash Fragment").commit();
                break;

            case 2:
                transaction.replace(R.id.splash_frame, new LoginFragment(), "Login Fragment").commit();

                break;

            case 3:
                transaction.replace(R.id.splash_frame, new RegisterFragment(), "Register Fragment").commit();

                break;

            case 4:
                transaction.replace(R.id.splash_frame,new UserTypeFragment(),"UserType Fragment").commit();
                break;

            default:
                //incase you put the wrong number

                break;
        }


    }


}
