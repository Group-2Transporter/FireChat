package com.infofoundation.firechat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infofoundation.firechat.MessageActivity;
import com.squareup.picasso.Picasso;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.infofoundation.firechat.R;
import com.infofoundation.firechat.beans.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends FirebaseRecyclerAdapter<User, ChatAdapter.ContactViewHolder> {
    String currentUser;
    DatabaseReference messageCounterReference;
    Context context;
    public ChatAdapter(@NonNull FirebaseRecyclerOptions<User> options, Context context) {
        super(options);
        this.context = context;
        messageCounterReference = FirebaseDatabase.getInstance().getReference("Message Counter");
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onBindViewHolder(@NonNull final ContactViewHolder holder, int i, @NonNull final User user) {
        holder.email.setText(user.getName());
        holder.status.setText(user.getStatus());
        Picasso.get().load(user.getImgUri()).placeholder(R.drawable.logo2).into(holder.circleImageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageCounterReference.child(currentUser).child(user.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(context,task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Intent in = new Intent(context, MessageActivity.class);
                in.putExtra("user",user);
                context.startActivity(in);
            }
        });
        if(user.getState()!=null){
            if(user.getState().equals("online")){
                holder.imgOnlineState.setVisibility(View.VISIBLE);
                holder.state.setVisibility(View.GONE);
            }else{
                holder.state.setText("Last seen : "+user.getDate()+" "+user.getTime());
            }
        }
        messageCounterReference.child(currentUser).child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    long total = snapshot.getChildrenCount();
                    if(total>0){
                        holder.btnCounter.setVisibility(View.VISIBLE);
                        holder.btnCounter.setText(""+total);
                    }else{
                        holder.btnCounter.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_freind,parent,false);
        return new ContactViewHolder(v);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder{
        TextView email,status,state;
        CircleImageView circleImageView;
        ImageView imgOnlineState;
        Button btnCounter;
        public ContactViewHolder(@NonNull View v) {
            super(v);
            email = v.findViewById(R.id.Listmail);
            status = v.findViewById(R.id.ListStatus);
            circleImageView = v.findViewById(R.id.ListCircleImage);
            state = v.findViewById(R.id.tvOnlineStatus);
            imgOnlineState = v.findViewById(R.id.imgOnlineStatus);
            btnCounter = v.findViewById(R.id.btnCounter);

        }
    }
}
