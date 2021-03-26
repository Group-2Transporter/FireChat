package com.infofoundation.firechat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infofoundation.firechat.beans.Group;
import com.squareup.picasso.Picasso;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.infofoundation.firechat.R;
import com.infofoundation.firechat.beans.User;

import java.util.ArrayList;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateNewGroupAdapter extends FirebaseRecyclerAdapter<User, CreateNewGroupAdapter.NewGroupViewHolder> {
    ArrayList<User>selectUserList;
    Group group;
    ArrayList<String>al;
    DatabaseReference groupReference;
    public CreateNewGroupAdapter(@NonNull FirebaseRecyclerOptions<User> options) {
        super(options);
        selectUserList = new ArrayList<User>();
        al = new ArrayList<>();
    }
    public CreateNewGroupAdapter(@NonNull FirebaseRecyclerOptions<User> options, Group group) {
        super(options);
        this.group = group;
        groupReference = FirebaseDatabase.getInstance().getReference("Group");
        selectUserList = new ArrayList<User>();
        al = new ArrayList<>();
        groupReference.child(group.getGroupId()).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Iterator<DataSnapshot>itr = snapshot.getChildren().iterator();
                    while (itr.hasNext()){
                        DataSnapshot ds = itr.next();
                        al.add(ds.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onBindViewHolder(@NonNull final NewGroupViewHolder holder, int i, @NonNull final User user) {

        Picasso.get().load(user.getImgUri()).placeholder(R.drawable.logo2).into(holder.civProfile);
        holder.tvUserName.setText(user.getName());
        holder.tvStatus.setText(user.getStatus());
        if(al!=null && al.contains(user.getUid())){
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }else {
            if (user.isCheck()) {
                holder.check.setVisibility(View.VISIBLE);
            } else {
                holder.check.setVisibility(View.GONE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        user.setCheck(!user.isCheck());
                        if (user.isCheck()) {
                            holder.check.setVisibility(View.VISIBLE);
                            selectUserList.add(user);
                        } else {
                            holder.check.setVisibility(View.GONE);
                            selectUserList.remove(user);
                        }
                    }
                });
            }
        }
    }

    @NonNull
    @Override
    public NewGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_group,parent,false);
        return new NewGroupViewHolder(v);
    }

    public class NewGroupViewHolder extends RecyclerView.ViewHolder{
        TextView tvUserName,tvStatus;
        CircleImageView civProfile;
        ImageView check;

        public NewGroupViewHolder(@NonNull View v) {
            super(v);
            tvUserName = v.findViewById(R.id.tvUserName);
            tvStatus = v.findViewById(R.id.tvUserStatus);
            civProfile = v.findViewById(R.id.civProfile);
            check = v.findViewById(R.id.checkGreen);
        }
    }
    public ArrayList<User> getSelectUserList(){
        return selectUserList;
    }
}
