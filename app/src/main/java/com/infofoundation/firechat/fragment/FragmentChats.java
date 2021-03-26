package com.infofoundation.firechat.fragment;

import android.content.Intent;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infofoundation.firechat.ContactsActivity;
import com.infofoundation.firechat.LoginActivity;
import com.infofoundation.firechat.R;
import com.infofoundation.firechat.adapter.ChatAdapter;
import com.infofoundation.firechat.beans.User;


public class FragmentChats extends Fragment {
    DatabaseReference messageReference,userReference;
    String currentUserId ;
    RecyclerView rv;
    FloatingActionButton chatContact;
    FirebaseRecyclerAdapter<User, ChatAdapter.ContactViewHolder> adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chats_fragment,null);
        rv = v.findViewById(R.id.rv);
        chatContact = v.findViewById(R.id.chatContact);
        chatContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getContext(), ContactsActivity.class);
                getActivity().startActivity(in);
            }
        });
        messageReference = FirebaseDatabase.getInstance().getReference("Messages");
        userReference = FirebaseDatabase.getInstance().getReference("Users");

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                    .setIndexedQuery(messageReference.child(currentUserId),userReference,User.class)
                    .build();
            adapter = new ChatAdapter(options,getContext());
            rv.setAdapter(adapter);
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter.startListening();
        }else{
           sendUserToLoginActivity();
        }
    }

    private void sendUserToLoginActivity() {
        Intent loginActivity = new Intent(getContext(), LoginActivity.class);
        loginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginActivity);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
            adapter.stopListening();
    }
}
