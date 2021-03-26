package com.infofoundation.firechat.menuunder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infofoundation.firechat.R;
import com.infofoundation.firechat.beans.User;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;

public class SendFriendRequest extends AppCompatActivity {
    Toolbar toolbar;
    TextView tvName,tvStatus;
    CircleImageView circleImageView;
    Button btnRequest,btnSendMessage;
    DatabaseReference reference;
    String sender;
    String receiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_friend_request);
        initContent();

        reference = FirebaseDatabase.getInstance().getReference().child("ChatRequest");
        sender = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Intent in = getIntent();
        User user = (User) in.getSerializableExtra("user");
        receiver = user.getUid();
        tvName.setText(user.getName());
        tvStatus.setText(user.getStatus());
        Picasso.get().load(user.getImgUri()).placeholder(R.drawable.logo2).into(circleImageView);
        checkRequestStatus();
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btnStatus = btnRequest.getText().toString();
                if(btnStatus.equalsIgnoreCase("Cancel Request")){
                   cancelRequest();
                }else if(btnStatus.equalsIgnoreCase("Send Request")){
                    sendRequest();
                }
            }
        });

    }
    private void cancelRequest() {
        reference.child(sender).child(receiver).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    reference.child(receiver).child(sender).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            btnRequest.setText("Send Request");
                            toolbar.setTitle("Send Request");
                        }
                    });
                }
            }
        });
    }
    private void checkRequestStatus() {
        reference.child(sender).child(receiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String requestType = snapshot.child("requestType").getValue().toString();
                    if(requestType.equals("sent")){
                        btnRequest.setText("Cancel Request");
                        toolbar.setTitle("Cancel Request");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendRequest() {
        reference.child(sender).child(receiver).child("requestType").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                   reference.child(receiver).child(sender).child("requestType").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                            btnRequest.setText("Cancel Request");
                            toolbar.setTitle("Cancel Request");
                       }
                   });
                }
            }
        });
    }

    private void initContent() {
        tvName = findViewById(R.id.tvName);
        tvStatus = findViewById(R.id.tvStatus);
        circleImageView = findViewById(R.id.sendCircleImage);
        btnRequest = findViewById(R.id.btnSendRequest);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        toolbar = findViewById(R.id.sendToolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Send Request");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
