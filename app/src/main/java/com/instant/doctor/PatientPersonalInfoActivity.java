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


//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Personal Information");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//
//        nameEditText = findViewById(R.id.name);
//        genderTextView = findViewById(R.id.gender);
//        phoneNoEditText = findViewById(R.id.patientPhone);
//        dobTextView = findViewById(R.id.patientDOB);
//        btn_save = findViewById(R.id.save_button);
//        patient_image = findViewById(R.id.patient_image);
//
//
//        genderRadioGroup =  findViewById(R.id.radioGender);
//
//        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                int SelectedId=genderRadioGroup.getCheckedRadioButtonId();
//                genderRadioButton=findViewById(SelectedId);
//                mGender =genderRadioButton.getText().toString();
//            }
//        });
//
//        patient_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (ContextCompat.checkSelfPermission(PatientPersonalInfoActivity.this,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//
//                    // Permission is not granted
//
//                    // No explanation needed; request the permission
//                    ActivityCompat.requestPermissions(PatientPersonalInfoActivity.this,
//                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                            PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
//
//                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                    // app-defined int constant. The callback method gets the
//                    // result of the request.
//
//                } else {
//                    // Permission has already been granted
//
//                    ///select from store=gae
//                    selectImage();
//
//
//                }
//
//            }
//        });
//
//
//        btn_save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                savePatientInfo();
//            }
//        });
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    //do something
//
//                    selectImage();
//                }
//
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request.
//        }
//
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == SELECT_IMAGE) {
//
//                Uri selectedImageUri = data.getData();
//                //get the path from the uri;;
//                final String path = getPathFromUri(selectedImageUri);
//                imagePath = path;
////                Toast.makeText(PatientPersonalInfoActivity.this, "Anything "+path, Toast.LENGTH_LONG).show();
//
//                Log.i(TAG, "uploaded image path: "+path);
//
////                uploadImageToStorage(path);
//
//                setPic(path);
//
////                uploadImageToServer(UUID.randomUUID().toString());
//                uploadImageToStorage(path);
//
//            }
//
//        }
//    }
//
//
//    private void uploadImageToStorage(final String path) {
//
//        patient_image.setImageResource(R.drawable.loading);
////        patient_image
//        Uri file = Uri.fromFile(new File(path));
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//        final StorageReference imgRef = storageRef.child("images/"+file.getLastPathSegment());
//        UploadTask uploadTask = imgRef.putFile(file);
//
//// Register observers to listen for when the download is done or if it fails
//        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//            @Override
//            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                if (!task.isSuccessful()) {
//                    throw task.getException();
//                }
//
//                // Continue with the task to get the download URL
//                return imgRef.getDownloadUrl();
//            }
//        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(@NonNull Task<Uri> task) {
//                if (task.isSuccessful()) {
//                    Uri downloadUri = task.getResult();
//                    imageURL =downloadUri.toString();
//                    Log.d(TAG, "image url " + imageURL);
//
//                    setPic(path);
//
//                } else {
//                    // Handle failures
//                    // ...
//                    patient_image.setImageResource(R.drawable.ic_unselected_patient);
//                    Toast.makeText(PatientPersonalInfoActivity.this, "Upload failed", Toast.LENGTH_LONG).show();
//
//                }
//            }
//        });
//    }
//
//    private String getPathFromUri(Uri selectedImageUri) {
//        String res = null;
//        String[] proj = {MediaStore.Images.Media.DATA};
//        Cursor cursor = getContentResolver()
//                .query(selectedImageUri, proj, null, null, null);
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                res = cursor.getString(column_index);
//            }
//            cursor.close();
//        }
//
//        return res;
//    }
//
//    public void selectImage() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);//
//        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_IMAGE);
//
//    }
//
//
//    private void setPic(String photoPath) {
//
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(photoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW / 100, photoH / 100);
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
//        patient_image.setImageBitmap(bitmap);
//    }
//
//
//    public void showDatePickerDialog(View v) {
//        DialogFragment newFragment = new DatePickerFragment();
//        newFragment.show(getSupportFragmentManager(), "date_picker");
//
//    }
//
//    public void processDatePickerResult(int year, int month, int day) {
//        Calendar c = Calendar.getInstance();
//        c.set(year, month + 1, day);
//
//        Date date = c.getTime();
//        mDate = date.getTime();
//
//        String dateString = day + " / " + month + " / " + year;
//        dobTextView.setText(dateString);
//
//
//    }
//
//    private void savePatientInfo() {
//
//        String name = nameEditText.getText().toString();
////        String gender = genderTextView.getText().toString();
//        String phoneNo = phoneNoEditText.getText().toString();
//        String gender= mGender;
//
//
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        String id = mAuth.getCurrentUser().getUid();
//        String email = mAuth.getCurrentUser().getEmail();
//        String image=imageURL;
//
//        if (!isValidPhoneNumber(phoneNo)) {
//            Toast.makeText(PatientPersonalInfoActivity.this, "Enter a valid phone Number", Toast.LENGTH_SHORT).show();
//
//            return;
//
//
//        }
//
//
//
//
//        PatientInfo patientInfo = new PatientInfo(name, mDate, gender, phoneNo, email, id, image);
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("patients").document()
//                .set(patientInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                Toast.makeText(PatientPersonalInfoActivity.this,
//                        "Info saved successfully", Toast.LENGTH_LONG).show();
//            }
//        });
//
//    }
//
//
//    static boolean isValidPhoneNumber(String phoneNo) {
//        // First validate that the phone number is not null and has a length of 10
//        if (TextUtils.isEmpty(phoneNo) || phoneNo.length() != 10) {
//            return false;
//        }
//        // Next check the first two characters of the string to make sure it's 07
//        if (!phoneNo.startsWith("07")) {
//            return false;
//        }
//        // Now verify that each character of the string is a digit
//        for (char c : phoneNo.toCharArray()) {
//            if (!Character.isDigit(c)) {
//                // One of the characters is not a digit (e.g. 0-9)
//                return false;
//            }
//        }
//        // At this point you know it is valid
//        return true;
//    }
//
//
//
//}
