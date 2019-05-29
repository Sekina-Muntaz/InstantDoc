package com.instant.doctor.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.instant.doctor.ChatActivity;
import com.instant.doctor.R;
import com.instant.doctor.models.MedicalSession;
import com.instant.doctor.models.PatientInfo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.MyViewHolder> {

    private Context mcontext;
    private List<MedicalSession> sessions;
    OnPatientSelected mListener;

    public PatientAdapter(Context mcontext, List<MedicalSession> mSessions, OnPatientSelected listener) {
        this.mcontext = mcontext;
        this.sessions = mSessions;
        mListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final MedicalSession session= sessions.get(position);
        holder.name.setText(session.getPatient_name());

        holder.image.setImageResource(R.drawable.ic_unselected_patient);
//
//        if (patient.getImageURL()== null){

//        }else{
//            Glide.with(mcontext).load(patient.getImageURL()).into(holder.image);
//        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.patientSelected(session.getPatient_id(), session.getId());
            }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(mcontext).inflate(R.layout.patient_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
       public TextView name;
       public CircleImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name =itemView.findViewById(R.id.patient_username);
            image=itemView.findViewById(R.id.profile_image);

        }

//        public void bind(PatientInfo patient){
//            name.setText(patient.getName());
//        }
    }


    public interface OnPatientSelected {
        void  patientSelected(String patientId,String medicalSessionId );
    }
}
