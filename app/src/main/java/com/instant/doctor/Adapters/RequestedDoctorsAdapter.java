package com.instant.doctor.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.instant.doctor.PatientPersonalInfoActivity;
import com.instant.doctor.R;
import com.instant.doctor.models.DoctorInfo;
import com.instant.doctor.models.MedicalSession;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestedDoctorsAdapter extends RecyclerView.Adapter<RequestedDoctorsAdapter.MyViewHolder> {

    private Context mcontext;
    private List<MedicalSession> medicalSessions;
    private OnDoctorSelected mListener;

    public RequestedDoctorsAdapter(Context mcontext,OnDoctorSelected listener ) {
        this.mcontext = mcontext;
        mListener = listener;
        this.medicalSessions = new ArrayList<>();
    }


    @NonNull
    @Override
    public RequestedDoctorsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.doctor_item, parent, false);

        return new RequestedDoctorsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final MedicalSession medicalSession = medicalSessions.get(position);
        holder.name.setText(medicalSession.getDoctor_name());
        if (medicalSession.getDoctor_photoUrl() == null) {
            holder.image.setImageResource(R.drawable.ic_unselected_doctor);
        } else {
            Glide.with(mcontext)
                    .load(medicalSession.getDoctor_photoUrl())
                    .into(holder.image);
        }

        holder.specialization.setText(medicalSession.getDoctor_Specialization());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.doctorSelected(medicalSession.getId(), medicalSession.getDoctor_id());
            }
        });

//        if(medicalSession.getDoctor_status().equals("online")){
//            holder.status.setVisibility(View.VISIBLE);
//        }else {
//            holder.status.setVisibility(View.GONE);
//        }

    }


    @Override
    public int getItemCount() {
        return medicalSessions.size();
    }


    public void setRequestedDoc(List<MedicalSession> data) {
        medicalSessions = data;
        notifyDataSetChanged();
    }

    public void updateMedicalSession(MedicalSession session, int index) {
        medicalSessions.remove(index);
        medicalSessions.add(index, session);
        notifyItemChanged(index);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public CircleImageView image;
        public TextView specialization;
        CircleImageView status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.doctor_username);
            image = itemView.findViewById(R.id.profile_image);
            specialization = itemView.findViewById(R.id.doctor_specialization);
            status = itemView.findViewById(R.id.status_online);


        }


    }

    public interface OnDoctorSelected{
        void doctorSelected(String medicalSessionId, String doctorId);
    }
}