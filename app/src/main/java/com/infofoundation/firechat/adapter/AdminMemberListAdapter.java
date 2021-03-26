package com.infofoundation.firechat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.infofoundation.firechat.R;
import com.infofoundation.firechat.beans.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminMemberListAdapter extends FirebaseRecyclerAdapter<User, AdminMemberListAdapter.MemberListViewHolder> {
    OnRecyclerViewClickListener listener;
    String currentUserId;
    public AdminMemberListAdapter(@NonNull FirebaseRecyclerOptions<User> options) {
        super(options);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onBindViewHolder(@NonNull MemberListViewHolder holder, int i, @NonNull User user) {
        if(!currentUserId.equals(user.getUid())) {
            holder.name.setText(user.getName());
            holder.status.setText(user.getStatus());
            Picasso.get().load(user.getImgUri()).placeholder(R.drawable.logo2).into(holder.circleImageView);
        }else{
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }
    }

    @NonNull
    @Override
    public MemberListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_info_group,parent,false);
        return new MemberListViewHolder(v);
    }

    public class MemberListViewHolder extends RecyclerView.ViewHolder{
        TextView name,status;
        CircleImageView circleImageView;
        ImageView imgStar;
        public MemberListViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.Listmail);
            status = itemView.findViewById(R.id.ListStatus);
            circleImageView = itemView.findViewById(R.id.ListCircleImage);
            imgStar = itemView.findViewById(R.id.imgStar);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    User user = getItem(position);
                    if(position!= RecyclerView.NO_POSITION && listener !=null){
                        listener.onItemClick(user,position);
                    }
                }
            });
        }
    }

    public  interface OnRecyclerViewClickListener{
        void onItemClick(User user,int position);
    }

    public void onItemClickListener(OnRecyclerViewClickListener listener){
        this.listener = listener;
    }

}
