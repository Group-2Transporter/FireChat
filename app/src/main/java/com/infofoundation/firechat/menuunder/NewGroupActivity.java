package com.infofoundation.firechat.menuunder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.infofoundation.firechat.R;
import com.infofoundation.firechat.adapter.CreateNewGroupAdapter;
import com.infofoundation.firechat.beans.User;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewGroupActivity extends AppCompatActivity {
    FloatingActionButton btnNext;
    RecyclerView rv;
    CircleImageView circleImage,circleImageIcon;
    Toolbar toolbar;
    DatabaseReference contactReference,userReference,groupReference;
    StorageReference storageReference;
    String currentUser;
    CreateNewGroupAdapter adapter;
    ProgressDialog pd;
    Uri imageUri;
    ArrayList<User>memberList;
    User getCurrentUserDetails;
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group_activity);
        initComponent();
        contactReference = FirebaseDatabase.getInstance().getReference("Contacts");
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        groupReference = FirebaseDatabase.getInstance().getReference("Group");
        storageReference = FirebaseStorage.getInstance().getReference("Group Icon");
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userReference.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    getCurrentUserDetails = snapshot.getValue(User.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memberList = adapter.getSelectUserList();
                AlertDialog ab = new AlertDialog.Builder(NewGroupActivity.this).create();
                View v = LayoutInflater.from(NewGroupActivity.this).inflate(R.layout.update_profile_setting,null);
                ab.setView(v);
                ab.show();
                circleImage = v.findViewById(R.id.circleImage);
                circleImageIcon = v.findViewById(R.id.circleImageIcon);
                circleImageIcon.setVisibility(View.GONE);
                final EditText etGroupName = v.findViewById(R.id.etName);
                final EditText etDescription = v.findViewById(R.id.etStatus);
                etGroupName.setHint("Group Name");
                etDescription.setText("");
                etDescription.setHint("Description of Group");
                Button btnCreate = v.findViewById(R.id.btnUpdateProfile);
                btnCreate.setText("Create");
                circleImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)
                                .start(NewGroupActivity.this);
                    }
                });
                btnCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(imageUri!=null) {
                            final String groupName = etGroupName.getText().toString();
                            final String groupDescription = etDescription.getText().toString();
                            if(TextUtils.isEmpty(groupName)){
                                etGroupName.setError("Please Enter Group Name");
                                return;
                            }
                            pd = new ProgressDialog(NewGroupActivity.this);
                            pd.setTitle("Creating a new group");
                            pd.setMessage("Wait...");
                            pd.show();
                            final String groupId = groupReference.push().getKey();
                            StorageReference filePath = storageReference.child(groupId+".jpg");
                            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUri =uri.toString();
                                            groupReference.child(groupId).child(currentUser).setValue("");
                                            for(User u: memberList){
                                                groupReference.child(groupId).child("members").child(u.getUid()).setValue(u.getName());
                                                groupReference.child(groupId).child(u.getUid()).setValue("");
                                            }
                                            groupReference.child(groupId).child("members").child(currentUser).setValue(getCurrentUserDetails.getName());
                                            Calendar calendar = Calendar.getInstance();
                                            SimpleDateFormat sd = new SimpleDateFormat("MMM dd , yyy hh:mm a");
                                            String createdAt= sd.format(calendar.getTime());
                                            HashMap<String,Object>hm = new HashMap<>();
                                            hm.put("name",groupName);
                                            hm.put("icon",imageUri);
                                            hm.put("description",groupDescription);
                                            hm.put("createdBy",currentUser);
                                            hm.put("createdAt",createdAt);
                                            hm.put("groupId",groupId);
                                            groupReference.child(groupId).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    pd.dismiss();
                                                    pd.setCancelable(false);
                                                    if(task.isSuccessful()){
                                                       finish();
                                                    }else{
                                                        String error = task.getException().toString();
                                                        Toast.makeText(NewGroupActivity.this,error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        }
                                    });
                                }
                            });


                        }
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
        adapter = new CreateNewGroupAdapter(options);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pd = new ProgressDialog(this);
        pd.setTitle("Image Loading...");
        pd.setMessage("Wait...");
        pd.show();
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                circleImage.setImageURI(imageUri);
                pd.dismiss();
            }
            else{
                pd.dismiss();
            }
        }
    }
}
