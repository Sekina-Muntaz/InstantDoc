package com.instant.doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
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
import com.instant.doctor.Adapters.ChatAdapter;
import com.instant.doctor.fragments.Doctor.PrescriptionDiagnosisFragment;
import com.instant.doctor.models.Chat;
import com.instant.doctor.models.DoctorInfo;
import com.instant.doctor.models.Message;
import com.instant.doctor.models.PatientInfo;
import com.instant.doctor.utils.UserTypePrefManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = ChatActivity.class.getSimpleName();
    CircleImageView profile_image;
    TextView user_name;
    FirebaseUser firebaseUser;

    FirebaseFirestore db;
    ImageButton button_send, button_attach;
    EditText send_edit_text;


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

        profile_image = findViewById(R.id.profile_image);
        user_name = findViewById(R.id.username);
        button_send = findViewById(R.id.button_send);
        button_attach = findViewById(R.id.button_attach);

        send_edit_text = findViewById(R.id.edit_text_send);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        senderId = firebaseUser.getUid();

        db = FirebaseFirestore.getInstance();
        messages = new ArrayList<>();


        initConversation();

        setProfilePic();

        RecyclerView recyclerView = findViewById(R.id.chat_recyclerview);
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

                if (message.trim().isEmpty()) {
                    Toast.makeText(ChatActivity.this, "You cant send an empty message", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendMessage(message);

            }
        });


        button_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                PrescriptionDiagnosisFragment newFragment = new PrescriptionDiagnosisFragment();
                Bundle bundle = new Bundle();
                bundle.putString("sessionId", medicalSessionId);
                bundle.putSerializable("patient", patientInfo);
                bundle.putSerializable("doctor", doctorInfo);
                newFragment.setArguments(bundle);

                FragmentTransaction transaction = fragmentManager.beginTransaction();
//                newFragment.show(transaction, "Prescription");


              transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                // To make it fullscreen, use the 'content' root view as the container
                // for the fragment, which is always the root view for the activity
                transaction.add(android.R.id.content, newFragment)
                        .addToBackStack(null).commit();
            }
        });


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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendMessage(final String message) {
        if (chatId == null) {
            Chat chat = new Chat(medicalSessionId, new ArrayList<Message>());
            db.collection("chats")
                    .document(medicalSessionId)
                    .set(chat)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            chatId = medicalSessionId;
                            saveMessage(message);
                            fetchMessages();
                            send_edit_text.setText("");
                        }
                    });

        } else {
            saveMessage(message);
            send_edit_text.setText("");
        }

    }

    private void saveMessage(String message) {
        Message message1 = new Message(chatId, senderId, receiverId, message, new Date().getTime());
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

    }


    private void setProfilePic() {
        final UserTypePrefManager userTypePrefManager = new UserTypePrefManager(getApplicationContext());

        Log.d(TAG, "setProfilePic: Called");
        if (userTypePrefManager.getUserType() == (0)) {
            //current auth user is patient
            db.collection("doctors").whereEqualTo("id", receiverId).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {


                    Log.d(TAG, "doctor sd profile: "+ task.getResult().size());

                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {

                             doctorInfo = queryDocumentSnapshot.toObject(DoctorInfo.class);

                            Log.d(TAG, " doctor profile: "+ doctorInfo);

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

        } else {

            //current auth user is doctor
            db.collection("patients").whereEqualTo("id", receiverId)
                    .limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.d(TAG, "patient sd profile: "+ task.getResult().size());


                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {

                            patientInfo = queryDocumentSnapshot.toObject(PatientInfo.class);

                            Log.d(TAG, " patient profile: "+ patientInfo);

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


                    Log.d(TAG, "doctor sd profile: "+ task.getResult().size());

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
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
