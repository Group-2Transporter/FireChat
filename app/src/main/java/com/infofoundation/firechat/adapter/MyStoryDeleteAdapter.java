package com.infofoundation.firechat.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.infofoundation.firechat.R;
import com.infofoundation.firechat.beans.Status;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;

public class MyStoryDeleteAdapter extends FirebaseRecyclerAdapter<Status, MyStoryDeleteAdapter.MyStoryDeleteViewHolder> {
    Context context;
    public MyStoryDeleteAdapter(@NonNull FirebaseRecyclerOptions<Status> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull final MyStoryDeleteViewHolder holder, int i, @NonNull final Status status) {
        Picasso.get().load(status.getImageUrl()).into(holder.civImage);
        holder.time.setText(status.getDate()+" "+status.getTime());
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context,holder.more);
                Menu menu = popupMenu.getMenu();
                menu.add("Delete");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String title = item.getTitle().toString();
                        if(title.equals("Delete")){
                            AlertDialog.Builder ab = new AlertDialog.Builder(context);
                            ab.setMessage("Are You Sure");
                            ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DatabaseReference statusReference = FirebaseDatabase.getInstance().getReference("Status").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(status.getStatusId());
                                    statusReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("Status").child(status.getStatusId()+".jpg");
                                    storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(context, "Status Removed", Toast.LENGTH_SHORT).show();
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
    }

    @NonNull
    @Override
    public MyStoryDeleteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_delete_view,parent,false);
        return new MyStoryDeleteViewHolder(v);
    }

    public class MyStoryDeleteViewHolder extends RecyclerView.ViewHolder{
        CircleImageView civImage;
        ImageView more;
        TextView time;
        LinearLayout ll;
        public MyStoryDeleteViewHolder(@NonNull View itemView) {
            super(itemView);
            civImage = itemView.findViewById(R.id.civProfile);
            more = itemView.findViewById(R.id.more);
            time = itemView.findViewById(R.id.tvUserName);
            ll = itemView.findViewById(R.id.ll);
        }
    }
}
