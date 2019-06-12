package com.instant.doctor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.badoualy.datepicker.DatePickerTimeline;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.instant.doctor.fragments.Doctor.BeforeChatFragmentDoctor;
import com.instant.doctor.fragments.Doctor.DoctorPersonalInfoFragment;
import com.instant.doctor.fragments.Doctor.SetDoctorAvailabilityFragment;
import com.instant.doctor.fragments.auth.SplashFragment;
import com.instant.doctor.models.DoctorAvailability;
import com.instant.doctor.models.DoctorInfo;
import com.nex3z.togglebuttongroup.MultiSelectToggleGroup;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class DoctorPersonalInfoActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 60;
    private static final int SELECT_IMAGE = 15;
    private EditText et_name;
    private EditText et_specialization;
    private EditText et_serviceNo;
    private EditText et_phoneNo;
    private CircleImageView doc_image;
    private Button btn_save;
    private String imageURL;
//    private DatePickerTimeline timeline;
//    private MultiSelectToggleGroup multiSelectToggleGroup;
//    private SingleSelectToggleGroup singleSelectToggleGroup;

    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_personal_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        changeFragment(1);


    }

    public void changeFragment(int page) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (page) {
            case 1:
                transaction.replace(R.id.doc_info_frame, new DoctorPersonalInfoFragment(), "Doc Personal Info Fragment").commit();
                break;


            case 2:
                transaction.replace(R.id.doc_info_frame, new SetDoctorAvailabilityFragment(), "Doc Personal Info Fragment").commit();
                break;

            case 3:
                transaction.replace(R.id.doc_info_frame, new BeforeChatFragmentDoctor(), "Before Doctor Fragment").commit();
                break;

        }

    }
}


//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Personal Information");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        et_name = findViewById(R.id.name);
//        et_specialization = findViewById(R.id.specialization);
//        et_serviceNo = findViewById(R.id.ServiceNumber);
//        et_phoneNo = findViewById(R.id.PhoneNumber);
//        btn_save = findViewById(R.id.save_button);
//        doc_image = findViewById(R.id.doctor_image);
////        timeline=findViewById(R.id.timeline);
////        multiSelectToggleGroup=findViewById(R.id.group_appointment);
////        singleSelectToggleGroup=findViewById(R.id.group_select_appointment_hours);
//
//        doc_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (ContextCompat.checkSelfPermission(DoctorPersonalInfoActivity.this,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//
//                    // Permission is not granted
//
//                    // No explanation needed; request the permission
//                    ActivityCompat.requestPermissions(DoctorPersonalInfoActivity.this,
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
//        btn_save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String doc_name = et_name.getText().toString();
//                String doc_specialization = et_specialization.getText().toString();
//                String doc_service_no = et_serviceNo.getText().toString();
//                String doc_phone_no = et_phoneNo.getText().toString();
//
//                if (TextUtils.isEmpty(doc_name) | TextUtils.isEmpty(doc_specialization) | TextUtils.isEmpty(doc_service_no) | TextUtils.isEmpty(doc_phone_no)) {
//                    Toast.makeText(DoctorPersonalInfoActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
//
//                } else {
//                    saveDoctorInfo();
//                }
//            }
//
//
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
////                Log.i(TAG, "uploaded image path: "+path);
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
//    private void uploadImageToStorage(final String path) {
//
//        doc_image.setImageResource(R.drawable.loading);
////        patient_image
//        Uri file = Uri.fromFile(new File(path));
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//        final StorageReference imgRef = storageRef.child("images/" + file.getLastPathSegment());
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
//                    imageURL = downloadUri.toString();
////                    Log.d(TAG, "image url " + imageURL);
//
//                    setPic(path);
//
//                } else {
//                    // Handle failures
//                    // ...
//                    doc_image.setImageResource(R.drawable.ic_unselected_patient);
//                    Toast.makeText(DoctorPersonalInfoActivity.this, "Upload failed", Toast.LENGTH_LONG).show();
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
//        doc_image.setImageBitmap(bitmap);
//    }
//
//    private void saveDoctorInfo() {
//        String name = et_name.getText().toString();
//        String specialization = et_specialization.getText().toString();
//        String serviceNo = et_serviceNo.getText().toString();
//        String phoneNo = et_phoneNo.getText().toString();
//        String image = imageURL;
//
//
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        String id = mAuth.getCurrentUser().getUid();
//        String email = mAuth.getCurrentUser().getEmail();
//
//
//        if (!isValidPhoneNumber(phoneNo)) {
//            Toast.makeText(DoctorPersonalInfoActivity.this, "Enter a valid phone Number", Toast.LENGTH_SHORT).show();
//
//            return;
//        }
//
//        if (!isValidServiceNumber(serviceNo)) {
//            Toast.makeText(DoctorPersonalInfoActivity.this, "Enter a valid service Number", Toast.LENGTH_SHORT).show();
//
//            return;
//        }
//
//        DoctorInfo doctorInfo = new DoctorInfo(id, name, email, phoneNo, specialization, serviceNo, image);
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        db.collection("doctors").add(doctorInfo)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        String documentId = documentReference.getId();
//                        SharedPreferences sharedPref = getSharedPreferences("my_prefs", 0);
//                        SharedPreferences.Editor editor = sharedPref.edit();
//                        editor.putString("doctorId", documentId);
//                        editor.apply();
//
//                        Toast.makeText(DoctorPersonalInfoActivity.this,
//                                "Info saved successfully", Toast.LENGTH_SHORT).show();
//
//                        Intent intent = new Intent(DoctorPersonalInfoActivity.this, SetDoctorsAvailabilityActivity.class);
//                        startActivity(intent);
//
//                        finish();
//                    }
//                });
//
//    }
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
//    static boolean isValidServiceNumber(String serviceNo) {
//        if (TextUtils.isEmpty(serviceNo) || serviceNo.length() != 6) {
//            return false;
//        }
//
//
//        if (!Character.isLetter(serviceNo.charAt(0))) {
//            return false;
//        }
//
//        String sub = serviceNo.substring(1);
//        try {
//            int num = Integer.parseInt(sub);
//        } catch (NumberFormatException e) {
//            return false;
//        }
//        return true;
//
//    }
//    }
//}
