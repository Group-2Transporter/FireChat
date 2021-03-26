package com.infofoundation.firechat;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infofoundation.firechat.adapter.MyStoryDeleteAdapter;
import com.infofoundation.firechat.beans.Status;


public class StatusDeleteActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView rv;
    MyStoryDeleteAdapter adapter;
    String currentUserId;
    DatabaseReference statusReference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_fragment);
        initComponent();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        statusReference = FirebaseDatabase.getInstance().getReference("Status").child(currentUserId);

    }

    private void initComponent() {
        toolbar = findViewById(R.id.toolbar);
        rv = findViewById(R.id.contactRv);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Status> options = new FirebaseRecyclerOptions.Builder<Status>()
                .setQuery(statusReference,Status.class)
                .build();
        adapter = new MyStoryDeleteAdapter(options,this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
