package com.infofoundation.firechat.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infofoundation.firechat.R;
import com.infofoundation.firechat.SendStoryActivity;
import com.infofoundation.firechat.StatusDeleteActivity;
import com.infofoundation.firechat.ViewStoryActivity;
import com.infofoundation.firechat.adapter.ViewStoryAdapter;
import com.infofoundation.firechat.beans.Status;
import com.infofoundation.firechat.beans.User;
import com.squareup.picasso.Picasso;


import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentStatus extends Fragment {
    RecyclerView rv;
    FloatingActionButton add;
    LinearLayout rl;
    ImageView more;
    DatabaseReference userReference,statusReference,contactReference;
    CircleImageView civProfile;
    String currentUserId;
    TextView tvUserName;
    ArrayList<Status>viewStoryList;
    ViewStoryAdapter adapter;
    Uri imageUri;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.status_fragment,null);
        civProfile = v.findViewById(R.id.civProfile);
        tvUserName = v.findViewById(R.id.tvUserName);
        rl = v.findViewById(R.id.rl);
        rv = v.findViewById(R.id.contactRv);
        add = v.findViewById(R.id.add);
        more = v.findViewById(R.id.more);
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        statusReference = FirebaseDatabase.getInstance().getReference("Status");
        contactReference = FirebaseDatabase.getInstance().getReference("Contacts");

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User u = snapshot.getValue(User.class);
                    Picasso.get().load(u.getImgUri()).placeholder(R.drawable.logo2).into(civProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getContext(), StatusDeleteActivity.class);
                startActivity(in);
            }
        });
        statusReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    tvUserName.setText("view your story");
                    Iterator<DataSnapshot>itr = snapshot.getChildren().iterator();
                    viewStoryList = new ArrayList<>();
                    while (itr.hasNext()){
                        DataSnapshot ds = itr.next();
                        Status status = ds.getValue(Status.class);
                        viewStoryList.add(status);
                    }
                }else{
                    tvUserName.setText("create your story");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .start(getContext(),FragmentStatus.this);
            }
        });


        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tvUserName.getText().toString().equalsIgnoreCase("view your story")){
                    Intent in = new Intent(getContext(), ViewStoryActivity.class);
                    in.putExtra("status",viewStoryList);
                    getActivity().startActivity(in);
                }else{
                    CropImage.activity()
                            .start(getContext(),FragmentStatus.this);
                }
            }
        });
        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setIndexedQuery(contactReference.child(currentUserId),userReference,User.class)
                .build();
        adapter = new ViewStoryAdapter(options,getContext());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.startListening();


    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == getActivity().RESULT_OK) {
                imageUri = result.getUri();
                Intent in = new Intent(getContext(), SendStoryActivity.class);
                in.putExtra("imageUri",""+imageUri);
                startActivity(in);

            }
        }
    }
}
