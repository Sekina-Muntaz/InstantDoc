package com.instant.doctor.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.instant.doctor.ChatActivity;
import com.instant.doctor.PatientPersonalInfoActivity;
import com.instant.doctor.R;
import com.instant.doctor.models.DoctorAvailability;
import com.instant.doctor.models.DoctorInfo;
import com.instant.doctor.models.PatientInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.MyViewHolder> {

    private Context mcontext;
    private List<DoctorInfo> doctorList;

    public DoctorAdapter(Context mcontext, List<DoctorInfo> mDoctorInfo) {
        this.mcontext = mcontext;
        this.doctorList = mDoctorInfo;
    }


    @NonNull
    @Override
    public DoctorAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.doctor_item, parent, false);

        return new DoctorAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final DoctorInfo doctor = doctorList.get(position);
        holder.name.setText(doctor.getName());
        holder.specialization.setText(doctor.getSpecialization());

        Map<String, ArrayList<String>> availabilites = doctor.getAvailability();

        if (availabilites != null) {

            if (availabilites.containsKey(getCurrentDayofTheWeek())) {
                ArrayList<String> times = availabilites.get(getCurrentDayofTheWeek());

                boolean availabilitySet = false;
                for (String time : times) {
                    if (time.equals(getCurrentTime())) {
                        //do something
                        holder.availability.setText("available");
                        availabilitySet = true;
                        break;

                    }
                }


                if (!availabilitySet) {
                    Collections.sort(times);
                    String currentTime = getCurrentTime();
                    boolean labelSet = false;
                    for (String time : times) {
                        if (time.compareTo(currentTime) > 0) {
                            //do something
                            labelSet = true;
                            holder.availability.setText("Next Available Time: " + time + " hrs");
                            break;

                        }
                    }

                    if(!labelSet){
                        holder.availability.setText("Not Available Today ");
                    }

                }
            } else {

                holder.availability.setText("Not Available Today ");
            }
        }

//
        if (doctor.getImageURL() == null) {
            holder.image.setImageResource(R.drawable.ic_unselected_doctor);
        } else {
            Glide.with(mcontext).load(doctor.getImageURL()).into(holder.image);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, PatientPersonalInfoActivity.class);
                intent.putExtra("doctor", doctor);
                Log.d("DoctorAdapter", "doctor clicked: " + doctor.getName());
//                intent.putExtra("doctorName",doctor.getName());
                mcontext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public void setDoctorList(List<DoctorInfo> doctors){
        doctorList = doctors;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, availability;
        public CircleImageView image;
        public TextView specialization;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.doctor_username);
            image = itemView.findViewById(R.id.profile_image);
            specialization = itemView.findViewById(R.id.doctor_specialization);

            availability = itemView.findViewById(R.id.doctor_availability);


        }

//        public void bind(PatientInfo patient){
//            name.setText(patient.getName());
//        }
    }


    public interface OnSelectDoctorListener {
        void doctorSelected(String doctorId, String doctorName);
    }

    public void setAvailability() {


    }

    private String getCurrentDayofTheWeek() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String[] days = {"SUN", "MON", "TUE", "WED", "THUR", "FRI", "SAT"};
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        return days[dayOfWeek - 1];
    }

    private String getCurrentTime() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String hrstr = String.valueOf(hour);

        if (hour < 10) {
            hrstr = "0" + hour;
        }
        String time;


        time = hrstr + "00";
        return time;
    }
}
