package com.instant.doctor.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.instant.doctor.R;
import com.instant.doctor.models.MedicalNote;

import java.util.Date;
import java.util.List;

public class MedicalNotesAdapter extends RecyclerView.Adapter<MedicalNotesAdapter.MyViewHolder> {


    private Context mcontext;
    private List<MedicalNote> medicalNotes;
    OnDownloadPressed mListener;

    public MedicalNotesAdapter(Context mcontext, List<MedicalNote> medicalNotes,OnDownloadPressed listener) {
        this.mcontext = mcontext;
        this.medicalNotes = medicalNotes;
        mListener = listener;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final MedicalNote notes= medicalNotes.get(position);
//        holder.sessionNo.setText(notes.getSessionId());

        holder.dateValue.setText(new Date(notes.getTime()).toString());
        holder.docName.setText(notes.getDoctorName());
        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.startDownload(notes.getMedicalNoteUrl());
            }
        });

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.medicalnotes_item, parent, false);

        return new MyViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return medicalNotes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
       // public TextView sessionNo;
        public TextView docName;
        public TextView dateValue;
        public ImageButton downloadButton;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

//            sessionNo = itemView.findViewById(R.id.sessionNo);
            docName = itemView.findViewById(R.id.docName);
            dateValue=itemView.findViewById(R.id.dateValue);
            downloadButton=itemView.findViewById(R.id.download);


        }


    }


    public interface OnDownloadPressed{
        void startDownload(String url);
    }
}

