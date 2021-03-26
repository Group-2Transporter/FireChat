package com.infofoundation.firechat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infofoundation.firechat.R;
import com.infofoundation.firechat.beans.ChatRequest;
import com.infofoundation.firechat.beans.User;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;

public class ReceivedRequestAdapter extends FirebaseRecyclerAdapter<ChatRequest, ReceivedRequestAdapter.ReceivedViewHolder> {
   Context context;
   DatabaseReference reference;
   String currentUser;
    public ReceivedRequestAdapter(@NonNull FirebaseRecyclerOptions<ChatRequest> options, Context context) {
        super(options);
        this.context = context;
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onBindViewHolder(@NonNull final ReceivedViewHolder holder, int i, @NonNull final ChatRequest chatRequest) {
        final String requestId = getRef(i).getKey();
        reference.child(requestId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User user  = snapshot.getValue(User.class);
                    holder.tvName.setText(user.getName());
                    holder.tvStatus.setText(user.getStatus());
                    Picasso.get().load(user.getImgUri()).placeholder(R.drawable.logo2).into(holder.image);
                    holder.btnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final DatabaseReference contactReference = FirebaseDatabase.getInstance().getReference().child("Contacts");
                            contactReference.child(currentUser).child(requestId).child("contact").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        contactReference.child(requestId).child(currentUser).child("contact").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(context, "Contact Saved Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }

                            });
                            final DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference().child("ChatRequest");
                            chatReference.child(currentUser).child(requestId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        chatReference.child(requestId).child(currentUser).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){

                                                }
                                            }
                                        });
                                    }
                                }
                            });

                        }
                    });
                    holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference().child("ChatRequest");
                            chatReference.child(currentUser).child(requestId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        chatReference.child(requestId).child(currentUser).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(context, "Cancel friend Request", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });


                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public ReceivedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.received_request,parent,false);
        return new ReceivedViewHolder(v);
    }

    public class ReceivedViewHolder extends RecyclerView.ViewHolder{
        Button btnConfirm,btnCancel;
        CircleImageView image;
        TextView tvName,tvStatus;
        public ReceivedViewHolder(View v){
            super(v);
            btnConfirm = v.findViewById(R.id.btnReceivedConfirm);
            btnCancel = v.findViewById(R.id.btnReceivedCancel);
            image = v.findViewById(R.id.receivedCircleImage);
            tvName = v.findViewById(R.id.receivedName);
            tvStatus = v.findViewById(R.id.receivedStatus);
        }
    }

}
