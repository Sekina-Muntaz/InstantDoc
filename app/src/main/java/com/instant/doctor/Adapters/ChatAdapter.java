package com.instant.doctor.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.instant.doctor.ChatActivity;
import com.instant.doctor.R;

import com.instant.doctor.models.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;

    private Context mcontext;
    private List<Message> messageList;
    private String userId;


    public ChatAdapter(Context mcontext) {
        this.mcontext = mcontext;
        this.messageList = new ArrayList<>();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_LEFT) {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_left, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == MSG_RIGHT) {
           View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_right, parent, false);
           return new MyViewHolder(view);
        }else {
            return null;
        }




    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getSenderId().equals(userId)) {
            return MSG_RIGHT;
        } else {
            return MSG_LEFT;
        }

    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message = messageList.get(position);

        if(message.getImageUrl() != null){
            holder.imageView.setVisibility(View.VISIBLE);
            holder.show_message.setVisibility(View.GONE);
            Glide.with(mcontext).load(message.getImageUrl()).into(holder.imageView);
        }else {
            holder.imageView.setVisibility(View.GONE);
            holder.show_message.setVisibility(View.VISIBLE);
            holder.show_message.setText(message.getMessage());
        }

        Date date = new Date(message.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat(" hh:mm");
        String fDate =dateFormat.format(date);
        holder.show_date.setText(fDate);
    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void addMessage(Message message) {
        messageList.add(message);
        notifyDataSetChanged();
    }

    public void setMessages(List<Message> messages) {
        messageList = messages;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView show_message;
        TextView show_date;
        ImageView imageView;
//        public CircleImageView image;
//        public TextView specialization;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            show_date= itemView.findViewById(R.id.show_date);
            imageView = itemView.findViewById(R.id.chat_image);
        }


    }
}
