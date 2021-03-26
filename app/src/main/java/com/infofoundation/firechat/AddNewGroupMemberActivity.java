package com.infofoundation.firechat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infofoundation.firechat.adapter.CreateNewGroupAdapter;
import com.infofoundation.firechat.beans.Group;
import com.infofoundation.firechat.beans.User;


import java.util.ArrayList;
import java.util.HashMap;

public class AddNewGroupMemberActivity extends AppCompatActivity {
    FloatingActionButton btnNext;
    RecyclerView rv;
    Toolbar toolbar;
    DatabaseReference contactReference,userReference,groupReference;
    String currentUser;
    CreateNewGroupAdapter adapter;
    ProgressDialog pd;
    ArrayList<User>memberList;
    Group group;
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group_activity);
        Intent in = getIntent();
        group = (Group) in.getSerializableExtra("group");

        initComponent();
        contactReference = FirebaseDatabase.getInstance().getReference("Contacts");
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        groupReference = FirebaseDatabase.getInstance().getReference("Group");
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memberList = adapter.getSelectUserList();
                HashMap<String,Object>hm = new HashMap<>();
                for(User u: memberList){
                    groupReference.child(group.getGroupId()).child("members").child(u.getUid()).setValue(u.getName());
                    groupReference.child(group.getGroupId()).child(u.getUid()).setValue("");
                }
                hm.put("","");
                groupReference.child(group.getGroupId()).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<User> options= new FirebaseRecyclerOptions.Builder<User>()
                .setIndexedQuery(contactReference.child(currentUser),userReference,User.class)
                .build();
        adapter = new CreateNewGroupAdapter(options,group);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void initComponent() {
        toolbar = findViewById(R.id.groupToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rv = findViewById(R.id.grpRv);
        btnNext = findViewById(R.id.btnNext);
    }
}
