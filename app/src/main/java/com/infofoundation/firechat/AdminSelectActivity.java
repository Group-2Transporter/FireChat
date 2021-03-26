package com.infofoundation.firechat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infofoundation.firechat.adapter.AdminMemberListAdapter;
import com.infofoundation.firechat.beans.Group;
import com.infofoundation.firechat.beans.User;
import com.infofoundation.firechat.fragment.AdminBottom;


public class AdminSelectActivity extends AppCompatActivity {
    FloatingActionButton btnNext;
    RecyclerView rv;
    Toolbar toolbar;
    DatabaseReference groupReference,userReference,memberReference;
    String currentUserId;
    AdminMemberListAdapter adapter;
    Group group;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group_activity);
        Intent in = getIntent();
        group = (Group) in.getSerializableExtra("group");
        initComponent();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        groupReference = FirebaseDatabase.getInstance().getReference("Group");
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        memberReference = FirebaseDatabase.getInstance().getReference("Group").child(group.getGroupId()).child("members");

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<User> options= new FirebaseRecyclerOptions.Builder<User>()
                .setIndexedQuery(memberReference,userReference,User.class)
                .build();
        adapter = new AdminMemberListAdapter(options);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter.startListening();
        adapter.onItemClickListener(new AdminMemberListAdapter.OnRecyclerViewClickListener() {
            @Override
            public void onItemClick(User user, int position) {
                AdminBottom ab = new AdminBottom(user,groupReference,memberReference,group,currentUserId);
                ab.show(getSupportFragmentManager(),"");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void initComponent() {
        toolbar = findViewById(R.id.groupToolbar);
        toolbar.setTitle("Create New Admin");
        toolbar.setSubtitle("Make admin before Exit group");
        setSupportActionBar(toolbar);
        rv = findViewById(R.id.grpRv);
        btnNext = findViewById(R.id.btnNext);
        btnNext.setVisibility(View.GONE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
