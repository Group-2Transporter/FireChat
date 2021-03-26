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

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infofoundation.firechat.GroupChatActivity;
import com.infofoundation.firechat.R;
import com.infofoundation.firechat.adapter.GroupShowFragmentAdapter;
import com.infofoundation.firechat.beans.Group;


public class FragmentGroup extends Fragment {
    RecyclerView rv;
    String currentUserId;
    DatabaseReference groupReference;
    GroupShowFragmentAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.group_fragment,null);
        rv = v.findViewById(R.id.g_rv);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            groupReference = FirebaseDatabase.getInstance().getReference("Group");
        }
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseRecyclerOptions<Group> options = new FirebaseRecyclerOptions.Builder<Group>()
                    .setQuery(groupReference.orderByChild(currentUserId).equalTo(""), Group.class)
                    .build();
            adapter = new GroupShowFragmentAdapter(options);
            adapter.setOnclickListener(new GroupShowFragmentAdapter.OnRecyclerViewClickListener() {
                @Override
                public void onItemClick(Group group, int position) {
                    Intent in = new Intent(getContext(), GroupChatActivity.class);
                    in.putExtra("group", group);
                    startActivity(in);
                }
            });
            rv.setAdapter(adapter);
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        adapter.stopListening();
    }
}
