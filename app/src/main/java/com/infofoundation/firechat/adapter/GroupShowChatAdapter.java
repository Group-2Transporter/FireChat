package com.infofoundation.firechat.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.infofoundation.firechat.R;
import com.infofoundation.firechat.beans.Message;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupShowChatAdapter extends ArrayAdapter {
    Context context;
    ArrayList<Message>al;
    String currentUser;
    public GroupShowChatAdapter(Context context, ArrayList<Message>al){
        super(context, R.layout.chat_message_item_list,al);
        this.context = context;
        this.al = al;
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.group_chat_message_item_list,parent,false);
        ImageView imageSend = itemView.findViewById(R.id.imageRight);
        ImageView imageReceive = itemView.findViewById(R.id.imageLeft);
        RelativeLayout leftRl = itemView.findViewById(R.id.leftRl);
        RelativeLayout rightRl = itemView.findViewById(R.id.rightRl);
        TextView chatFriendMessage = itemView.findViewById(R.id.tvFriendMessage);
        TextView chatFriendTime = itemView.findViewById(R.id.tvFriendTime);
        TextView chatMyMessage = itemView.findViewById(R.id.tvMyMessage);
        TextView chatMyTime = itemView.findViewById(R.id.tvMyTime);
        TextView state = itemView.findViewById(R.id.messageOnlineState);
        CircleImageView civSenderIcon = itemView.findViewById(R.id.civGrp);
        final Message message = al.get(position);


        if(message.getType().equals("text")){
            if(currentUser.equals(message.getFrom())){
                imageSend.setVisibility(View.INVISIBLE);
                rightRl.setVisibility(View.VISIBLE);
                chatMyTime.setText(message.getTime());
                chatMyMessage.setText(message.getMessage());
                leftRl.setVisibility(View.INVISIBLE);
            }else {
                imageReceive.setVisibility(View.INVISIBLE);
                leftRl.setVisibility(View.VISIBLE);
                chatFriendTime.setText(message.getTime());
                chatFriendMessage.setText(message.getMessage());
                rightRl.setVisibility(View.INVISIBLE);
                Picasso.get().load(message.getSenderIcon()).placeholder(R.drawable.logo2).into(civSenderIcon);
                civSenderIcon.setVisibility(View.VISIBLE);
            }
        }else if(message.getType().equals("image")){
            if(currentUser.equals(message.getFrom())){
                chatMyMessage.setVisibility(View.INVISIBLE);
                leftRl.setVisibility(View.INVISIBLE);
                rightRl.setVisibility(View.VISIBLE);
                chatMyTime.setText(message.getTime());
                imageSend.setVisibility(View.VISIBLE);
                imageSend.getLayoutParams().height=650;
                imageSend.getLayoutParams().width=750;
                Picasso.get().load(message.getMessage()).into(imageSend);

            }else {
                chatFriendMessage.setVisibility(View.INVISIBLE);
                leftRl.setVisibility(View.VISIBLE);
                rightRl.setVisibility(View.INVISIBLE);
                imageReceive.setVisibility(View.VISIBLE);
                chatFriendTime.setText(message.getTime());
                imageReceive.getLayoutParams().height=650;
                imageReceive.getLayoutParams().width=750;
                Picasso.get().load(message.getMessage()).into(imageReceive);
                Picasso.get().load(message.getSenderIcon()).placeholder(R.drawable.logo2).into(civSenderIcon);
                civSenderIcon.setVisibility(View.VISIBLE);
            }
        }else if(message.getType().equals("pdf")||message.getType().equals("word")){
            if(currentUser.equals(message.getFrom())){
                chatMyMessage.setVisibility(View.INVISIBLE);
                leftRl.setVisibility(View.INVISIBLE);
                rightRl.setVisibility(View.VISIBLE);
                chatMyTime.setText(message.getTime());
                imageSend.setVisibility(View.VISIBLE);
                imageSend.getLayoutParams().height=300;
                imageSend.getLayoutParams().width=300;
                if(message.getType().equals("pdf")){
                    imageSend.setBackgroundResource(R.drawable.pdf);
                }else{
                    imageSend.setBackgroundResource(R.drawable.word);
                }
                imageSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getMessage()));
                        context.startActivity(in);
                    }
                });
            }else {
                Picasso.get().load(message.getSenderIcon()).placeholder(R.drawable.logo2).into(civSenderIcon);
                civSenderIcon.setVisibility(View.VISIBLE);
                chatFriendMessage.setVisibility(View.INVISIBLE);
                leftRl.setVisibility(View.VISIBLE);
                rightRl.setVisibility(View.INVISIBLE);
                imageReceive.setVisibility(View.VISIBLE);
                chatFriendTime.setText(message.getTime());
                imageReceive.getLayoutParams().height=300;
                imageReceive.getLayoutParams().width=300;
                if(message.getType().equals("pdf")){
                    imageReceive.setBackgroundResource(R.drawable.pdf);
                }else{
                    imageReceive.setBackgroundResource(R.drawable.word);
                }
                imageReceive .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getMessage()));
                        context.startActivity(in);
                    }
                });
            }
        }

        return itemView;
    }
}
