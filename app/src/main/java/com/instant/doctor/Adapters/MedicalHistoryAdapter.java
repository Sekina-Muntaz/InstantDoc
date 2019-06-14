package com.instant.doctor.Adapters;

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

public class MedicalHistoryAdapter extends RecyclerView.Adapter<MedicalHistoryAdapter.MedicalHistoryViewHolder> {
    private Context mcontext;
    private List<MedicalNote> medicalNotes;
    MedicalNotesAdapter.OnDownloadPressed mListener;

    public MedicalHistoryAdapter(Context mcontext, List<MedicalNote> medicalNotes) {
        this.mcontext = mcontext;
        this.medicalNotes = medicalNotes;

    }



    @NonNull
    @Override
    public MedicalHistoryAdapter.MedicalHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.medicalhistory_item, parent, false);

        return new MedicalHistoryAdapter.MedicalHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicalHistoryViewHolder holder, int position) {
        final MedicalNote notes= medicalNotes.get(position);

        holder.docName.setText(notes.getDoctorName());
        holder.date.setText(new Date().toString());
        holder.diagnosis.setText(notes.getDiagnosis());
        holder.prescription.setText(notes.getPrescription());
    }


    @Override
    public int getItemCount() {
        return medicalNotes.size();
    }

    public class MedicalHistoryViewHolder extends RecyclerView.ViewHolder {
        public TextView docName;
        public TextView date;
        public TextView diagnosis;
        public TextView prescription;





        public MedicalHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            docName = itemView.findViewById(R.id.docNameValue);
            date = itemView.findViewById(R.id.dateValue);
            diagnosis=itemView.findViewById(R.id.diagnosis);
            prescription=itemView.findViewById(R.id.prescriptionValue);




        }


    }}
