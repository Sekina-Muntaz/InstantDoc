package com.instant.doctor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.instant.doctor.fragments.DatePickerFragment;
import com.instant.doctor.fragments.Doctor.DoctorPersonalInfoFragment;
import com.instant.doctor.fragments.Patient.BeforeChatFragmentPatient;
import com.instant.doctor.fragments.Patient.PatientPersonalInfoFragment;
import com.instant.doctor.fragments.Patient.SymptomsFragment;
import com.instant.doctor.models.DoctorInfo;
import com.instant.doctor.models.PatientInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class PatientPersonalInfoActivity extends AppCompatActivity {


    long dateLong;

    private DoctorInfo mDoctorInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_personal_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();

        if (intent.hasExtra("doctor")) {
            mDoctorInfo = (DoctorInfo) intent.getSerializableExtra("doctor");

            changeFragment(2);
        } else {

          changeFragment(1);

        }


    }

    public void processDatePickerResults(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        Date date = calendar.getTime();
        dateLong = date.getTime();
    }

    public long getDateResult() {
        return dateLong;
    }

    public void changeFragment(int page) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (page) {
            case 1:
                transaction.replace(R.id.patient_info_frame, new PatientPersonalInfoFragment(), "Doc Personal Info Fragment").commit();
                break;


            case 2:
                SymptomsFragment fragment = new SymptomsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("doctor", mDoctorInfo);

                fragment.setArguments(bundle);
                transaction.replace(R.id.patient_info_frame, fragment, "Patient Symptoms  Fragment").commit();
                break;


        }
    }
}
