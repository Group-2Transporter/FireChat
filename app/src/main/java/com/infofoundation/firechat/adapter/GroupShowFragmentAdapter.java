package com.infofoundation.firechat.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.infofoundation.firechat.GroupInfoActivity;
import com.infofoundation.firechat.R;
import com.infofoundation.firechat.beans.Group;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;

public class GroupShowFragmentAdapter extends FirebaseRecyclerAdapter<Group, GroupShowFragmentAdapter.GroupChatViewHolder> {
    OnRecyclerViewClickListener listener;
    String currentUser;
    DatabaseReference groupReference;
    StorageReference storageReference;
    public GroupShowFragmentAdapter(@NonNull FirebaseRecyclerOptions<Group> options) {
        super(options);
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        groupReference = FirebaseDatabase.getInstance().getReference("Group");
        storageReference = FirebaseStorage.getInstance().getReference("Group Icon");
    }

    @Override
    protected void onBindViewHolder(@NonNull GroupChatViewHolder holder, int i, @NonNull final Group group) {
        holder.name.setText(group.getName());
        holder.status.setText(group.getDescription());
        Picasso.get().load(group.getIcon()).placeholder(R.drawable.logo2).into(holder.circleImageView);
        if(currentUser.equals(group.getCreatedBy())){
            holder.imgSetting.setVisibility(View.VISIBLE);
        }else{
            holder.imgSetting.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public GroupChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_group,parent,false);
        return new GroupChatViewHolder(v);
    }

    public  class  GroupChatViewHolder extends RecyclerView.ViewHolder{
        TextView name,status;
        CircleImageView circleImageView;
        ImageView imgSetting;
        LinearLayout ll;
        public GroupChatViewHolder(@NonNull View v) {
            super(v);
            name = v.findViewById(R.id.Listmail);
            status = v.findViewById(R.id.ListStatus);
            circleImageView = v.findViewById(R.id.ListCircleImage);
            imgSetting = v.findViewById(R.id.imgSetting);
            ll = v.findViewById(R.id.ll2);
            imgSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(itemView.getContext(),imgSetting);
                    Menu menu = popupMenu.getMenu();
                    menu.add("Info");
                    menu.add("Delete Group");

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(final MenuItem item) {
                            int position = getAdapterPosition();
                            final Group group = getItem(position);
                            String title = item.getTitle().toString();
                            if(title.equals("Info")){
                                Intent info = new Intent(itemView.getContext(), GroupInfoActivity.class);
                                info.putExtra("info",group);
                                itemView.getContext().startActivity(info);
                            }else if(title.equals("Delete Group")){
                                AlertDialog.Builder ab = new AlertDialog.Builder(itemView.getContext());
                                ab.setTitle("Delete Group");
                                ab.setMessage("Are you Sure");
                                ab.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        groupReference.child(group.getGroupId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                   storageReference.child(group.getGroupId()+".jpg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                       @Override
                                                       public void onSuccess(Void aVoid) {
                                                           Toast.makeText(itemView.getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                                       }
                                                   }).addOnFailureListener(new OnFailureListener() {
                                                       @Override
                                                       public void onFailure(@NonNull Exception e) {
                                                           Toast.makeText(itemView.getContext(), "Something wrong", Toast.LENGTH_SHORT).show();
                                                       }
                                                   });
                                                }
                                            }
                                        });
                                    }
                                });
                                ab.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                });
                                ab.show();
                            }

                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(position!= RecyclerView.NO_POSITION && listener!=null){
                        listener.onItemClick(getItem(position),position);
                    }
                }
            });
        }
    }
    public interface OnRecyclerViewClickListener{
        void onItemClick(Group group,int position);
    }
    public void setOnclickListener(OnRecyclerViewClickListener listener){
        this.listener = listener;
    }
}
