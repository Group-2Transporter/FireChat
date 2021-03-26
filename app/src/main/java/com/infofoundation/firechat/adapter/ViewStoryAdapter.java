package com.infofoundation.firechat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infofoundation.firechat.R;
import com.infofoundation.firechat.ViewStoryActivity;
import com.infofoundation.firechat.beans.Status;
import com.infofoundation.firechat.beans.User;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewStoryAdapter extends FirebaseRecyclerAdapter<User, ViewStoryAdapter.ViewStoryViewHolder> {
    DatabaseReference statusReference;
    ArrayList<Status>viewStoryList;
    Context context;
    public ViewStoryAdapter(@NonNull FirebaseRecyclerOptions<User> options, Context context) {
        super(options);
        this.context = context;
        statusReference = FirebaseDatabase.getInstance().getReference("Status");


    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewStoryViewHolder holder, final int i, @NonNull final User user) {
        statusReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.tvUserName.setText(user.getName());
                    Picasso.get().load(user.getImgUri()).placeholder(R.drawable.logo2).into(holder.civProfile);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Iterator<DataSnapshot>itr = snapshot.getChildren().iterator();
                            viewStoryList = new ArrayList<>();
                            while (itr.hasNext()){
                                DataSnapshot ds = itr.next();
                                Status status = ds.getValue(Status.class);
                                viewStoryList.add(status);
                            }
                            Intent in = new Intent(context, ViewStoryActivity.class);
                            in.putExtra("status",viewStoryList);
                            context.startActivity(in);
                        }
                    });
                } else {
                    holder.tvUserName.setVisibility(View.GONE);
                    holder.civProfile.setVisibility(View.GONE);
                    holder.itemView.setVisibility(View.GONE);
                    holder.rl.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @NonNull
    @Override
    public ViewStoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_view_adapter_list,parent,false);
        return new ViewStoryViewHolder(v);
    }

    public  class ViewStoryViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civProfile;
        TextView tvUserName;
        RelativeLayout rl;

        public ViewStoryViewHolder(@NonNull View itemView) {
            super(itemView);
            civProfile = itemView.findViewById(R.id.civProfile);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            rl = itemView.findViewById(R.id.rl);
        }
    }
}
