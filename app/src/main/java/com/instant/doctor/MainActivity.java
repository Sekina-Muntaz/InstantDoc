package com.instant.doctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.instant.doctor.fragments.Doctor.BeforeChatFragmentDoctor;
import com.instant.doctor.fragments.Doctor.DisplayPatientFragment;
import com.instant.doctor.fragments.Doctor.DoctorPersonalInfoFragment;
import com.instant.doctor.fragments.Doctor.SetDoctorAvailabilityFragment;
import com.instant.doctor.fragments.Patient.BeforeChatFragmentPatient;
import com.instant.doctor.fragments.Patient.DisplayDoctorsFragment;
import com.instant.doctor.fragments.Patient.DisplayMedicalNotesFragment;
import com.instant.doctor.models.DoctorInfo;
import com.instant.doctor.models.PatientInfo;
import com.instant.doctor.utils.UserTypePrefManager;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ProgressDialog progressDialog;
    UserTypePrefManager userTypePrefManager;


    private boolean navDrawerItemSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // set name and email

        View headerView = navigationView.getHeaderView(0);
//        final TextView nav_user_name = headerView.findViewById(R.id.nav_username);
//        final TextView nav_email = (TextView) headerView.findViewById(R.id.nav_email);

        //set username and email appropriately

//        if (user!=null){
//            nav_email.setText();
//        }


        MenuItem chats = navigationView.getMenu().findItem(R.id.nav_chats);
        MenuItem medicalNotes = navigationView.getMenu().findItem(R.id.nav_medicalNotes);
        MenuItem docStatistics = navigationView.getMenu().findItem(R.id.nav_statistics);
//        MenuItem completedJobs = navigationView.getMenu().findItem(R.id.nav_manage);

        UserTypePrefManager userTypePrefManager =new UserTypePrefManager(getApplicationContext());
        switch (userTypePrefManager.getUserType()) {

            case 0:
                //patient
//                nav_email.setText(patientInfo.getEmail());
//                nav_user_name.setText(patientInfo.getName());
//                medicalNotes.setVisible(true);
                docStatistics.setVisible(false);
                break;
//
            case 1:
                //doctor
//                nav_email.setText(doctorInfo.getEmail());
//                nav_user_name.setText(doctorInfo.getName());
                medicalNotes.setVisible(false);
//                docStatistics.setVisible(true);
                break;


        }
        Intent intent = getIntent();
        if (intent.hasExtra("beforeChat")) {
            changeFragment(3);
        } else if (!navDrawerItemSelected) {
            checkUserType();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                final UserTypePrefManager userTypePrefManager = new UserTypePrefManager(this);
                userTypePrefManager.deleteUserTypePref();
                startActivity(new Intent(this, Splash.class));
                finish();
                return true;


        }
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        switch (id == R.id.nav_chats) {
//
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }


        navDrawerItemSelected = true;
        final UserTypePrefManager userTypePrefManager = new UserTypePrefManager(this);
        switch (id) {

            case R.id.nav_chats:

                if (userTypePrefManager.getUserType() == 0) {
                    //patient
                    changeFragment(3);
                } else {
                    //doctor
                    changeFragment(4);
                }
                break;

            case R.id.nav_medicalNotes:
                if (userTypePrefManager.getUserType() == 0) {
                    changeFragment(5);

                } else {
                    //dummy trial
                    changeFragment(1);
                }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //online

    }

    @Override
    protected void onPause() {
        super.onPause();
        //offline
    }

    private void updateStatus(String status) {
        UserTypePrefManager userTypePrefManager = new UserTypePrefManager(this);
        int type = userTypePrefManager.getUserType();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (type == 0) {
                //patient
                db.collection("sessions")
                        .whereEqualTo("patient_id", user.getUid())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                    snapshot.getId();
                                }
                            }
                        });

            }

        }

    }

    public void changeFragment(int page) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (page) {
            case 1:
                transaction.replace(R.id.content_frame, new DisplayPatientFragment(), "Display  Patient Fragment").commit();
                break;


            case 2:
                transaction.replace(R.id.content_frame, new DisplayDoctorsFragment(), "Display Doctor Fragment").commit();
                break;
            case 3:
                transaction.replace(R.id.content_frame, new BeforeChatFragmentPatient(), "Before Chat Patients").commit();
                break;
            case 4:
                transaction.replace(R.id.content_frame, new DisplayPatientFragment(), "Before Chat Doctor").commit();
                break;
            case 5:
                transaction.replace(R.id.content_frame, new DisplayMedicalNotesFragment(), "Medical Notes").commit();
        }

    }


    public void checkUserType() {

        final UserTypePrefManager userTypePrefManager = new UserTypePrefManager(this);
        if (userTypePrefManager.getUserType() == 3 || userTypePrefManager.getUserName() == null) {


            db.collection("doctors").whereEqualTo("id", user.getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            // doctor found

                            List<DoctorInfo> doctors = task.getResult().toObjects(DoctorInfo.class);
                            for (DoctorInfo doctor : doctors) {
                                userTypePrefManager.addUserType(1);
                                userTypePrefManager.addUserName(doctor.getName());
                            }


                            Log.d(TAG, "Doctor--" + task.getResult().toObjects(DoctorInfo.class));
                            changeFragment(1);


                        } else {

                            db.collection("patients").whereEqualTo("id", user.getUid())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if (task.getResult() != null && !task.getResult().isEmpty()) {
                                                    List<PatientInfo> patients = task.getResult().toObjects(PatientInfo.class);
                                                    for (PatientInfo patient : patients) {
                                                        userTypePrefManager.addUserType(0);
                                                        userTypePrefManager.addUserName(patient.getName());
                                                    }

                                                    changeFragment(2);

                                                    Log.d(TAG, "User Patient--" + task.getResult().toObjects(PatientInfo.class));

                                                } else {
                                                    // redirect to user type
                                                    Log.d(TAG, "User Noone--");
                                                    Intent intent = new Intent(MainActivity.this, Splash.class);
                                                    intent.putExtra("detailsNotFilled", true);
                                                    startActivity(intent);
                                                }
                                            }


                                        }
                                    });
                        }

                    }
                }
            });

        } else {

            int type = userTypePrefManager.getUserType();
            Log.d(TAG, "User we got home--" + type);
            if (type == 1) {
                changeFragment(1);
            } else {
                changeFragment(2);
            }
        }
    }


}

