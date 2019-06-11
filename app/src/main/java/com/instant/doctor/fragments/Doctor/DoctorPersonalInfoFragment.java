package com.instant.doctor.fragments.Doctor;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.instant.doctor.DoctorPersonalInfoActivity;
import com.instant.doctor.R;
import com.instant.doctor.SetDoctorsAvailabilityActivity;
import com.instant.doctor.Splash;
import com.instant.doctor.fragments.auth.RegisterFragment;
import com.instant.doctor.fragments.auth.UserTypeFragment;
import com.instant.doctor.models.DoctorInfo;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorPersonalInfoFragment extends Fragment {
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 60;
    private static final int SELECT_IMAGE = 15;
    private static final String TAG = "DoctorPersonalInfo";
    private EditText et_name;
    private EditText et_specialization;
    private EditText et_serviceNo;
    private EditText et_phoneNo;
    private CircleImageView doc_image;
    private Button btn_save;
    private String imageURL;
    private String imagePath;
    private ProgressDialog progressDialog;
    private ArrayList<DoctorInfo> doctorList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.doctor_personal_info_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//
//        fetchDoctors();



        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Saving...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        et_name = view.findViewById(R.id.name);
        et_specialization = view.findViewById(R.id.specialization);
        et_serviceNo = view.findViewById(R.id.ServiceNumber);
        et_phoneNo = view.findViewById(R.id.PhoneNumber);
        btn_save = view.findViewById(R.id.save_button);
        doc_image = view.findViewById(R.id.doctor_image);


        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        String rationale = "Please provide storage write permission so that you can upload your photo";
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning");

        Permissions.check(getActivity()/*context*/, permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                // permission denied, block the feature.
                Toast.makeText(getContext(), "Please accept this permission to proceed", Toast.LENGTH_LONG).show();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.doc_info_frame, new UserTypeFragment()).commit();

            }
        });


        doc_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String doc_name = et_name.getText().toString();
                String doc_specialization = et_specialization.getText().toString();
                String doc_service_no = et_serviceNo.getText().toString();
                String doc_phone_no = et_phoneNo.getText().toString();
                if (TextUtils.isEmpty(doc_name) | TextUtils.isEmpty(doc_specialization) | TextUtils.isEmpty(doc_service_no) | TextUtils.isEmpty(doc_phone_no)) {
                    Toast.makeText(getActivity(), "All fields must be filled", Toast.LENGTH_SHORT).show();

                    return;
                }
                db = FirebaseFirestore.getInstance();
                db.collection("doctors").whereEqualTo("serviceNo", doc_service_no).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();

//                                    for (DocumentSnapshot snapshot : task.getResult()) {
//                                        String serviceNos=snapshot.getString("serviceNo");
//                                        if (serviceNos.equals(doc_service_no)) {
//                                            Toast.makeText(getActivity(), "Enter a correct Number", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
                                    if(task.getResult().size() == 0 ){
                                        Log.d(TAG, "User not Exists");
                                        //You can store new user information here
                                        saveDoctorInfo();

                                    }else {
                                        Toast.makeText(getActivity(), "Enter a correct Registration Number", Toast.LENGTH_SHORT).show();
                                    }

                                }

                            }
                        });

            }


        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

                }

                Toast.makeText(getActivity(), "Please accept this permission to proceed", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onRequestPermissionsResult: " + grantResults);
//


            }


        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_IMAGE) {

                Uri selectedImageUri = data.getData();
                //get the path from the uri;;
                final String path = getPathFromUri(selectedImageUri);
                imagePath = path;

                setPic(path);

//                uploadImageToServer(UUID.randomUUID().toString());
                uploadImageToStorage(path);

            }

        }
    }

    private void uploadImageToStorage(final String path) {

        doc_image.setImageResource(R.drawable.loading);
//        patient_image
        Uri file = Uri.fromFile(new File(path));
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference imgRef = storageRef.child("images/" + file.getLastPathSegment());
        UploadTask uploadTask = imgRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imgRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    imageURL = downloadUri.toString();
//                    Log.d(TAG, "image url " + imageURL);

                    setPic(path);

                } else {
                    // Handle failures
                    // ...
                    doc_image.setImageResource(R.drawable.ic_unselected_patient);
                    Toast.makeText(getActivity(), "Upload failed", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private String getPathFromUri(Uri selectedImageUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver()
                .query(selectedImageUri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                res = cursor.getString(column_index);
            }
            cursor.close();
        }

        return res;
    }

    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_IMAGE);

    }

    private void setPic(String photoPath) {

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / 100, photoH / 100);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        doc_image.setImageBitmap(bitmap);
    }

    private void saveDoctorInfo() {
        String name = et_name.getText().toString();
        String specialization = et_specialization.getText().toString();
        String serviceNo = et_serviceNo.getText().toString();
        String phoneNo = et_phoneNo.getText().toString();
        String image = imageURL;


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String id = mAuth.getCurrentUser().getUid();
        String email = mAuth.getCurrentUser().getEmail();


        if (!isValidPhoneNumber(phoneNo)) {
            Toast.makeText(getActivity(), "Enter a valid phone Number", Toast.LENGTH_SHORT).show();

            return;
        }

        if (!isValidServiceNumber(serviceNo)) {
            Toast.makeText(getActivity(), "Enter a valid service Number", Toast.LENGTH_SHORT).show();

            return;
        }

        DoctorInfo doctorInfo = new DoctorInfo(id, name, email, phoneNo, specialization, serviceNo, image);

//        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("doctors").add(doctorInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressDialog.dismiss();
                        String documentId = documentReference.getId();
                        SharedPreferences sharedPref = getActivity().getSharedPreferences("my_prefs", 0);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("doctorId", documentId);
                        editor.apply();

                        Toast.makeText(getActivity(),
                                "Info saved successfully", Toast.LENGTH_SHORT).show();


                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.doc_info_frame, new SetDoctorAvailabilityFragment()).commit();
//
                    }
                });

    }

    static boolean isValidPhoneNumber(String phoneNo) {
        // First validate that the phone number is not null and has a length of 10
        if (TextUtils.isEmpty(phoneNo) || phoneNo.length() != 10) {
            return false;
        }
        // Next check the first two characters of the string to make sure it's 07
        if (!phoneNo.startsWith("07")) {
            return false;
        }
        // Now verify that each character of the string is a digit
        for (char c : phoneNo.toCharArray()) {
            if (!Character.isDigit(c)) {
                // One of the characters is not a digit (e.g. 0-9)
                return false;
            }
        }
        // At this point you know it is valid
        return true;
    }

    static boolean isValidServiceNumber(String serviceNo) {
        if (TextUtils.isEmpty(serviceNo) || serviceNo.length() != 6) {
            return false;
        }


        if (!Character.isLetter(serviceNo.charAt(0))) {
            return false;
        }

        String sub = serviceNo.substring(1);
        try {
            int num = Integer.parseInt(sub);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;

    }

    public void fetchDoctors() {

        String serviceNo = et_serviceNo.getText().toString();
        db = FirebaseFirestore.getInstance();
        db.collection("doctors").whereEqualTo("serviceNo", serviceNo).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (DocumentSnapshot snapshot : task.getResult()) {
                                String serviceNos=snapshot.getString("serviceNo");
                                if (serviceNos.equals(serviceNo)) {
                                    Toast.makeText(getActivity(), "Enter a correct Number", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }

                    }
                });
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                if (!queryDocumentSnapshots.isEmpty()) {
//                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
//
//                    for (DocumentSnapshot d : list) {
//                        DoctorInfo aDoctor = d.toObject(DoctorInfo.class);
//                        doctorList.add(aDoctor);
//                    }
//                    adapter.notifyDataSetChanged();
//                }
//            }
//        });
    }


}

