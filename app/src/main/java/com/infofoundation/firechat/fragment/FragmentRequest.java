package com.infofoundation.firechat.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infofoundation.firechat.R;
import com.infofoundation.firechat.adapter.ReceivedRequestAdapter;
import com.infofoundation.firechat.beans.ChatRequest;


public class FragmentRequest extends Fragment {
    DatabaseReference chatReference;
    RecyclerView rv;
    String currentUser;
    FirebaseRecyclerAdapter<ChatRequest, ReceivedRequestAdapter.ReceivedViewHolder> adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.request_fragment,null);
        chatReference = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        rv = v.findViewById(R.id.rv);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<ChatRequest> options = new FirebaseRecyclerOptions.Builder<ChatRequest>()
                .setQuery(chatReference.child("ChatRequest").child(currentUser).orderByChild("requestType").equalTo("received"),ChatRequest.class)
                .build();
        adapter = new ReceivedRequestAdapter(options,getContext());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
