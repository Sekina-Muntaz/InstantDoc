package com.instant.doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.instant.doctor.Adapters.ChatAdapter;
import com.instant.doctor.fragments.Doctor.PrescriptionDiagnosisFragment;
import com.instant.doctor.fragments.auth.UserTypeFragment;
import com.instant.doctor.models.Chat;
import com.instant.doctor.models.DoctorInfo;
import com.instant.doctor.models.Message;
import com.instant.doctor.models.PatientInfo;
import com.instant.doctor.notifications.APIService;
import com.instant.doctor.notifications.Client;
import com.instant.doctor.notifications.Data;
import com.instant.doctor.notifications.MyResponse;
import com.instant.doctor.notifications.Sender;
import com.instant.doctor.notifications.Token;
import com.instant.doctor.utils.UserTypePrefManager;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = ChatActivity.class.getSimpleName();
    private static final int SELECT_IMAGE = 142;
    CircleImageView profile_image;
    TextView user_name;
    FirebaseUser firebaseUser;

    FirebaseFirestore db;
    ImageButton button_send, button_attach;
    EditText send_edit_text;
    RecyclerView recyclerView;


    String medicalSessionId;
    String receiverId;
    String senderId;
    String chatId;
    ChatAdapter chatAdapter;
    Chat chat;
    List<Message> messages;
    PatientInfo patientInfo;
    DoctorInfo doctorInfo;
    LinearLayoutManager layoutManager;

    APIService apiService;
    boolean notify = false;
    boolean senderIsPatient;

    ImageView chatImageView;
    String imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);


        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra("medical-session-id")) {
            medicalSessionId = getIntent().getStringExtra("medical-session-id");
            receiverId = getIntent().getStringExtra("receiver-id");
        }

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        profile_image = findViewById(R.id.profile_image);
        user_name = findViewById(R.id.username);
        button_send = findViewById(R.id.button_send);
        button_attach = findViewById(R.id.button_attach);
//        chatImageView = findViewById(R.id.image_view);

        send_edit_text = findViewById(R.id.edit_text_send);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        senderId = firebaseUser.getUid();

        db = FirebaseFirestore.getInstance();
        messages = new ArrayList<>();


        initConversation();

        setProfilePic();

        updateToken(FirebaseInstanceId.getInstance().getToken());
        recyclerView = findViewById(R.id.chat_recyclerview);
        chatAdapter = new ChatAdapter(this);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);

        //fetch message


        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = send_edit_text.getText().toString();
                notify = true;

                if (message.trim().isEmpty()) {
                    Toast.makeText(ChatActivity.this, "You cant send an empty message", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendMessage(message, false);

            }
        });


        button_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                String rationale = "Please provide storage write permission so that you can upload your photo";
                Permissions.Options options = new Permissions.Options()
                        .setRationaleDialogTitle("Info")
                        .setSettingsDialogTitle("Warning");

                Permissions.check(getApplicationContext()/*context*/, permissions, rationale, options, new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_IMAGE);

                    }

                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        // permission denied, block the feature.
                        Toast.makeText(ChatActivity.this, "Please accept this permission to proceed", Toast.LENGTH_LONG).show();

                    }
                });
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_hospital_referral:
                intent = new Intent(this, LabReferralActivity.class);
                intent.putExtra("sessionId", medicalSessionId);
                intent.putExtra("patient", patientInfo);
                intent.putExtra("isLab", false);
                intent.putExtra("doctor", doctorInfo);
                startActivity(intent);
                return true;

            case R.id.action_lab_referral:
                intent = new Intent(this, LabReferralActivity.class);
                intent.putExtra("sessionId", medicalSessionId);
                intent.putExtra("patient", patientInfo);
                intent.putExtra("isLab", true);
                intent.putExtra("doctor", doctorInfo);
                startActivity(intent);
                return true;

            case R.id.action_medical_note:

                intent = new Intent(this, MedicalNoteActivity.class);
                intent.putExtra("sessionId", medicalSessionId);
                intent.putExtra("patient", patientInfo);
                intent.putExtra("doctor", doctorInfo);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_IMAGE) {

                Uri selectedImageUri = data.getData();
                //get the path from the uri;;
                final String path = getPathFromUri(selectedImageUri);

//                setPic(path);
//                chatImageView.setVisibility(View.VISIBLE);
                uploadImageToStorage(path);

            }

        }
    }

    private void uploadImageToStorage(final String path) {

        Uri file = Uri.fromFile(new File(path));
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference imgRef = storageRef.child("images/" + file.getLastPathSegment());
        UploadTask uploadTask = imgRef.putFile(file);

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

                    sendMessage(imageURL, true);

                } else {

                    Toast.makeText(ChatActivity.this, "Upload failed", Toast.LENGTH_LONG).show();

                }
            }
        });
    }


    private void updateToken(String token) {
        Token token1 = new Token(token, senderId);
        db.collection("tokens").document()
                .set(token1);
    }

    private String getPathFromUri(Uri selectedImageUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver()
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


    private void fetchMessages() {
        if (chatId != null) {

            DocumentReference docRefs = db.collection("chats").document(chatId);
            docRefs.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {

                        Chat chat = snapshot.toObject(Chat.class);
                        messages = chat.getMessages();
                        Collections.sort(messages, new Comparator<Message>() {

                            @Override
                            public int compare(Message o1, Message o2) {
                                if (o1.getTime() > o2.getTime()) {
                                    return 1;
                                }
                                if (o1.getTime() < o2.getTime()) {
                                    return -1;
                                }
                                return 0;
                            }
                        });

                        Log.d(TAG, "messages length:" + messages.size());
                        chatAdapter.setMessages(messages);
                        recyclerView.scrollToPosition(messages.size() - 1);


                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });


        }
    }

    private void initConversation() {
        db.collection("chats")
                .whereEqualTo("sessionId", medicalSessionId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : list) {
                                chat = d.toObject(Chat.class);
                                chatId = d.getId();
                                fetchMessages();
                            }

                            Log.d(TAG, "Init onSuccess: ");

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }


    private void sendMessage(final String message, boolean isImage) {
        if (chatId == null) {
            Chat chat = new Chat(medicalSessionId, new ArrayList<Message>());
            db.collection("chats")
                    .document(medicalSessionId)
                    .set(chat)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            chatId = medicalSessionId;
                            saveMessage(message, isImage);
                            fetchMessages();
                            send_edit_text.setText("");
                        }
                    });

        } else {
            saveMessage(message, isImage);
            send_edit_text.setText("");
        }

    }

    private void saveMessage(String message, boolean isImage) {

        Message message1 = new Message(chatId, senderId, receiverId, new Date().getTime());
        if (isImage) {
            message1.setImageUrl(message);
        } else {
            message1.setMessage(message);
        }

        db.collection("chats")
                .document(medicalSessionId)
                .update("messages", FieldValue.arrayUnion(message1))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ChatActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Message Saving Failed ", e);
                        Toast.makeText(ChatActivity.this, "Message Sending Failed", Toast.LENGTH_LONG).show();
                    }
                });


        String senderUserName = senderIsPatient ? patientInfo.getName() : doctorInfo.getName();
        if (notify) {
            sendNotification(receiverId, senderUserName, message);
        }
        notify = false;

    }

    private void sendNotification(String receiver, String username, String message) {

        db.collection("tokens").whereEqualTo("userId", receiver)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Token token = snapshot.toObject(Token.class);
                            Data data = new Data(senderId, medicalSessionId,
                                    R.mipmap.ic_launcher, username + "\n" + message,
                                    "New Message", receiver);
                            Sender sender = new Sender(data, token.getToken());

                            apiService.sendNotication(sender).enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                          //  Toast.makeText(ChatActivity.this, "Failed", Toast.LENGTH_LONG).show();

                                        }
                                    }

                                    //   Toast.makeText(ChatActivity.this, "Notification send", Toast.LENGTH_LONG).show();


                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });

                        }

                    }
                });
    }


    private void setProfilePic() {
        final UserTypePrefManager userTypePrefManager = new UserTypePrefManager(getApplicationContext());

        Log.d(TAG, "setProfilePic: Called");
        if (userTypePrefManager.getUserType() == (0)) {
            //current auth user is patient
            senderIsPatient = true;

            db.collection("doctors").whereEqualTo("id", receiverId).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {


                    Log.d(TAG, "doctor sd profile: " + task.getResult().size());

                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {

                            doctorInfo = queryDocumentSnapshot.toObject(DoctorInfo.class);

                            Log.d(TAG, " doctor profile: " + doctorInfo);

                            user_name.setText(doctorInfo.getName());
                            if (doctorInfo.getImageURL() == null) {
                                profile_image.setImageResource(R.drawable.user);
                            } else {
                                Glide.with(ChatActivity.this).load(doctorInfo.getImageURL()).into(profile_image);
                            }
                            break;

                        }

                    }
                }
            });

            db.collection("patients").whereEqualTo("id", senderId)
                    .limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.d(TAG, "patient sd profile: " + task.getResult().size());


                    if (task.isSuccessful()) {


                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {

                            patientInfo = queryDocumentSnapshot.toObject(PatientInfo.class);
                            break;


                        }

                    }
                }
            });


        } else {

            //current auth user is doctor
            senderIsPatient = false;

            db.collection("patients").whereEqualTo("id", receiverId)
                    .limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.d(TAG, "patient sd profile: " + task.getResult().size());


                    if (task.isSuccessful()) {


                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {

                            patientInfo = queryDocumentSnapshot.toObject(PatientInfo.class);

                            Log.d(TAG, " patient profile: " + patientInfo);

                            user_name.setText(patientInfo.getName());
                            if (patientInfo.getImageURL() == null) {
                                profile_image.setImageResource(R.drawable.user);
                            } else {
                                Glide.with(ChatActivity.this).load(patientInfo.getImageURL()).into(profile_image);
                            }
                            break;

                        }

                    }
                }
            });


            db.collection("doctors").whereEqualTo("id", senderId).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {


                    Log.d(TAG, "doctor sd profile: " + task.getResult().size());

                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {

                            doctorInfo = queryDocumentSnapshot.toObject(DoctorInfo.class);

                            break;

                        }

                    }
                }
            });


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}
