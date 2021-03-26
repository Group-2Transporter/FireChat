package com.infofoundation.firechat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infofoundation.firechat.menuunder.SendFriendRequest;
import com.squareup.picasso.Picasso;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.infofoundation.firechat.R;
import com.infofoundation.firechat.beans.User;

import java.util.ArrayList;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendListAdapter extends FirebaseRecyclerAdapter<User, FriendListAdapter.FriendViewHolder> {
    Context context;
    String currentUser;
    ArrayList<String>al;
    DatabaseReference contact;
    public FriendListAdapter(@NonNull FirebaseRecyclerOptions<User> options, Context context) {
        super(options);
        this.context = context;
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        contact = FirebaseDatabase.getInstance().getReference("Contacts").child(currentUser);
        contact.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    al = new ArrayList<>();
                   Iterator<DataSnapshot>itr = snapshot.getChildren().iterator();
                   while (itr.hasNext()){
                       DataSnapshot ds = itr.next();
                       String friends = ds.getKey();
                       al.add(friends);
                   }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onBindViewHolder(@NonNull FriendViewHolder holder, int i, @NonNull final User user) {
        if(!currentUser.equals(user.getUid())) {
            if(al!=null && al.contains(user.getUid())){
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }else {
                holder.email.setText(user.getName());
                holder.status.setText(user.getStatus());
                Picasso.get().load(user.getImgUri()).placeholder(R.drawable.logo2).into(holder.circleImageView);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent sendRequest = new Intent(context, SendFriendRequest.class);
                        sendRequest.putExtra("user", user);
                        context.startActivity(sendRequest);
                    }
                });
            }
        }else{
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
        if(user.getState()!=null){
            if(user.getState().equals("online")){
                holder.state.setVisibility(View.GONE);
            }else{
                holder.state.setText("Last seen : "+user.getDate()+" "+user.getTime());
            }
        }

    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_freind,parent,false);
        return new FriendViewHolder(v);
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder{
        TextView email,status,state;
        CircleImageView circleImageView;
        ImageView imgOnlineState;
        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.Listmail);
            status = itemView.findViewById(R.id.ListStatus);
            circleImageView = itemView.findViewById(R.id.ListCircleImage);
            state = itemView.findViewById(R.id.tvOnlineStatus);
            imgOnlineState = itemView.findViewById(R.id.imgOnlineStatus);
        }
    }
}
