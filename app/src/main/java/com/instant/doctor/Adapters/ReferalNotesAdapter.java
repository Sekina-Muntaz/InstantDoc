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
import com.instant.doctor.models.Referral;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReferalNotesAdapter extends RecyclerView.Adapter<ReferalNotesAdapter.MyViewHolder> {

    private Context mContext;
    private List<Referral> referralList;
    private MedicalNotesAdapter.OnDownloadPressed mListener;
    public ReferalNotesAdapter(Context context, MedicalNotesAdapter.OnDownloadPressed listener){
        mContext = context;
        referralList = new ArrayList<>();
        mListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.medicalnotes_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        final Referral notes= referralList.get(position);
//        holder.sessionNo.setText(notes.getSessionId());
        holder.dateValue.setText(new Date(notes.getTime()).toString());
        holder.docName.setText(notes.getDoctorName());
        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.startDownload(notes.getNotesUrl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return referralList.size();
    }

    public void setReferralList(List<Referral> referrals){
        referralList = referrals;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

//        public TextView sessionNo;
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

}
