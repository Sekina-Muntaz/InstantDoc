package com.instant.doctor.fragments.Patient;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.instant.doctor.MainActivity;
import com.instant.doctor.PatientPersonalInfoActivity;
import com.instant.doctor.R;
import com.instant.doctor.fragments.DatePickerFragment;
import com.instant.doctor.fragments.Doctor.DisplayPatientFragment;
import com.instant.doctor.fragments.Doctor.SetDoctorAvailabilityFragment;
import com.instant.doctor.fragments.SelectDatePicker;
import com.instant.doctor.models.PatientInfo;
import com.instant.doctor.utils.UserTypePrefManager;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientPersonalInfoFragment extends Fragment {
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 12;
    private static final String TAG = PatientPersonalInfoActivity.class.getSimpleName();
    private static final int SELECT_IMAGE = 20;
    private EditText nameEditText;
    private TextView dobTextView;
    private TextView genderTextView;
    private RadioGroup genderRadioGroup;
    private EditText phoneNoEditText;
    private Button btn_save;
    private long mDate;
    private CircleImageView patient_image;
    private String imageURL;
    private TextView patientDOB;
    private RadioButton genderRadioButton;


    private String imagePath;
    private String mGender;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.patient_personal_info_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        nameEditText = view.findViewById(R.id.name);
        genderTextView = view.findViewById(R.id.gender);
        phoneNoEditText = view.findViewById(R.id.patientPhone);
        dobTextView = view.findViewById(R.id.patientDOB);
        btn_save = view.findViewById(R.id.save_button);
        patient_image = view.findViewById(R.id.patient_image);

        dobTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment dialogFragment=new SelectDatePicker();
                dialogFragment.show(getActivity().getSupportFragmentManager(),"Date");

                setDate();

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePatientInfo();
            }
        });



        genderRadioGroup =  view.findViewById(R.id.radioGender);

        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int SelectedId=genderRadioGroup.getCheckedRadioButtonId();
                genderRadioButton=view.findViewById(SelectedId);
                mGender =genderRadioButton.getText().toString();
            }
        });

        patient_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted

                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.

                } else {
                    // Permission has already been granted

                    ///select from store=gae
                    selectImage();


                }

            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePatientInfo();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do something

                    selectImage();
                }

            }

            // other 'case' lines to check for other
            // permissions this app might request.
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
//                Toast.makeText(PatientPersonalInfoActivity.this, "Anything "+path, Toast.LENGTH_LONG).show();

                Log.i(TAG, "uploaded image path: "+path);

//                uploadImageToStorage(path);

                setPic(path);

//                uploadImageToServer(UUID.randomUUID().toString());
                uploadImageToStorage(path);

            }

        }
    }


    private void uploadImageToStorage(final String path) {

        patient_image.setImageResource(R.drawable.loading);
//        patient_image
        Uri file = Uri.fromFile(new File(path));
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference imgRef = storageRef.child("images/"+file.getLastPathSegment());
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
                    imageURL =downloadUri.toString();
                    Log.d(TAG, "image url " + imageURL);

                    setPic(path);

                } else {
                    // Handle failures
                    // ...
                    patient_image.setImageResource(R.drawable.ic_unselected_patient);
                    Toast.makeText(getActivity(), "Upload failed", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private String getPathFromUri(Uri selectedImageUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor =getActivity(). getContentResolver()
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
        patient_image.setImageBitmap(bitmap);
    }




    private void setDate(){
        PatientPersonalInfoActivity activity = (PatientPersonalInfoActivity)getActivity();
        mDate = activity.getDateResult();

        Date date = new Date(mDate);

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day =    c.get(Calendar.DAY_OF_MONTH);

        String dateString = day + " / " + (month+1) + " / " + year;
        dobTextView.setText(dateString);

    }

    private void savePatientInfo() {

        final String name = nameEditText.getText().toString();
//        String gender = genderTextView.getText().toString();
        String phoneNo = phoneNoEditText.getText().toString();
        String gender= mGender;


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String id = mAuth.getCurrentUser().getUid();
        String email = mAuth.getCurrentUser().getEmail();
        String image=imageURL;

        if (!isValidPhoneNumber(phoneNo)) {
            Toast.makeText(getActivity(), "Enter a valid phone Number", Toast.LENGTH_SHORT).show();

            return;


        }




        PatientInfo patientInfo = new PatientInfo(name, mDate, gender, phoneNo, email, id, image);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("patients").document()
                .set(patientInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getActivity(),
                        "Info saved successfully", Toast.LENGTH_LONG).show();

                UserTypePrefManager manager = new UserTypePrefManager(getActivity());
                manager.addUserName(name);
//                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.doc_info_frame, new DisplayDoctorsFragment());

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
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





}

