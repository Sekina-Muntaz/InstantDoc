package com.instant.doctor.fragments.Patient;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.instant.doctor.Adapters.DoctorAdapter;
import com.instant.doctor.Adapters.MedicalNotesAdapter;
import com.instant.doctor.R;
import com.instant.doctor.models.MedicalNote;
import com.instant.doctor.models.MedicalSession;

import java.util.ArrayList;
import java.util.List;

public class DisplayMedicalNotesFragment extends Fragment implements MedicalNotesAdapter.OnDownloadPressed {
    public static final String TAG = "MedicalNotesFragment";
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL = 209;
    private long downloadID;
    private String mMedicalNoteDownloadUrl;
    private String mMedicalNoteTitle;


    RecyclerView recyclerView;
    List<MedicalNote> medicalNoteList;
    private RecyclerView.Adapter adapter;
    FirebaseFirestore db;

    TextView tv_NoMedicalNote;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.display_medical_notes_fragment,container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Medical Notes");

        tv_NoMedicalNote=view.findViewById(R.id.noNote);


        recyclerView = view.findViewById(R.id.display_medical_notes_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        medicalNoteList = new ArrayList<>();
        adapter = new MedicalNotesAdapter(getActivity(),medicalNoteList, this);

        recyclerView.setAdapter(adapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);

        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading ...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        db = FirebaseFirestore.getInstance();
        String patientId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("medicalNotes")
                .whereEqualTo("patientId",patientId)
                .orderBy("time", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                progressDialog.dismiss();
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    Log.d(TAG, "data fetched: "+list.size());


                    for (DocumentSnapshot d : list) {
                       MedicalNote notes = d.toObject(MedicalNote.class);
                        Log.d(TAG, "Document Id: "+ d.getId());
//                        notes.setId(d.getId());
                        medicalNoteList.add(notes);
                    }
                    adapter.notifyDataSetChanged();
                }else {
                    tv_NoMedicalNote.setVisibility(View.VISIBLE);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.e(TAG, "data fetch failed: ",e);
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(onDownloadComplete);
    }

    private void startDownload(String url, String medicalNote) {

        //request permissions;;
        if (requestWritePermission()) {


            DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            Uri download_Uri = Uri.parse(url);

            DownloadManager.Request request = new DownloadManager.Request(download_Uri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setAllowedOverRoaming(false);
            request.setTitle("Downloading Medical Note");
            request.setDescription("Download of " + medicalNote +" in progress...");
            request.setVisibleInDownloadsUi(true);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/medical_notes/"+ medicalNote+".pdf");

            downloadID = downloadManager.enqueue(request);

        }
    }
//    @Override
//    public void onstartDownload(String url, String medicalNoteName) {
//
//        mMedicalNoteDownloadUrl = RetrofitClient.BASE_URL + "/files/books/" + url;
//        mMedicalNoteTitle = medicalNoteName;
//        //  Toast.makeText(getActivity(), "Starting download "+mRecipeDownloadUrl,Toast.LENGTH_LONG).show();
//
//
//        startDownload(mMedicalNoteDownloadUrl, medicalNoteName);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_WRITE_EXTERNAL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (mMedicalNoteDownloadUrl != null) {
                        startDownload(mMedicalNoteDownloadUrl, "Medical Note");
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), " Enable this permission to allow download to proceed",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    private boolean requestWritePermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_WRITE_EXTERNAL);
            return false;

        } else {
            return true;
        }
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                Toast.makeText(getActivity(), "Download Completed", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void startDownload(String url) {
        mMedicalNoteDownloadUrl = url;
       startDownload(mMedicalNoteDownloadUrl, "Medical Note");
    }
}
