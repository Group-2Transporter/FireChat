package com.infofoundation.firechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.infofoundation.firechat.beans.Message;
import com.squareup.picasso.Picasso;
import com.infofoundation.firechat.R;

import java.util.ArrayList;

public class GroupMemberMessageAdapter extends ArrayAdapter {
    Context context;
    ArrayList<Message>al;
    public GroupMemberMessageAdapter(Context context, ArrayList<Message>al){
        super(context, R.layout.info_message,al);
        this.context = context;
        this.al = al;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Message msg = al.get(position);
        View v = LayoutInflater.from(getContext()).inflate(R.layout.info_message,null);
        TextView tvTime = v.findViewById(R.id.tvTime);
        TextView tvMessage = v.findViewById(R.id.tvMessage);
        ImageView imageMessage = v.findViewById(R.id.imageMessage);

        tvTime.setText(msg.getTime()+" "+msg.getDate());
        if(msg.getType().equals("text")){
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText(msg.getMessage());
        }else if (msg.getType().equals("image")){
            imageMessage.setVisibility(View.VISIBLE);
            Picasso.get().load(msg.getMessage()).into(imageMessage);

        }else if (msg.getType().equals("pdf") || msg.getType().equals("word")){
            if(msg.getType().equals("pdf")){
                imageMessage.setVisibility(View.VISIBLE);
                imageMessage.setBackgroundResource(R.drawable.pdf);
            }else if(msg.getType().equals("word")){
                imageMessage.setVisibility(View.VISIBLE);
                imageMessage.setBackgroundResource(R.drawable.word);
            }
        }

        return v;
    }
}
