package com.infofoundation.firechat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.infofoundation.firechat.adapter.GroupMemberMessageAdapter;
import com.infofoundation.firechat.adapter.MemberListAdapter;
import com.infofoundation.firechat.beans.Group;
import com.infofoundation.firechat.beans.Message;
import com.infofoundation.firechat.beans.User;
import com.squareup.picasso.Picasso;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Iterator;

public class GroupInfoActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView backDrop;
    FloatingActionButton addParticipant,btnExit,edit;
    RecyclerView rv;
    Group group;
    ImageView back;
    MemberListAdapter adapter;
    DatabaseReference userReference,memberReference,groupReference;
    String currentUserId;
    ArrayList<Message>memberMessageList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_info_activity);
        Intent in = getIntent();

        // intent get from groupShowAdapter
        group = (Group) in.getSerializableExtra("info");
        initComponent();
        Picasso.get().load(group.getIcon()).into(backDrop);

        // group member id
        groupReference = FirebaseDatabase.getInstance().getReference("Group").child(group.getGroupId());
        memberReference = FirebaseDatabase.getInstance().getReference("Group").child(group.getGroupId()).child("members");
        userReference = FirebaseDatabase.getInstance().getReference("Users");

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        //activity finish code
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(currentUserId.equals(group.getCreatedBy())){
            //delete on swipe method
            removeMember();
        }else{
            addParticipant.setVisibility(View.GONE);
        }


        // member exit code
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentUserId.equals(group.getCreatedBy())){
                    //admin exit group and make new admin
                    Intent in = new Intent(GroupInfoActivity.this, AdminSelectActivity.class);
                    in.putExtra("group",group);
                    startActivity(in);
                }else{
                    // non admin member remove self code
                    selfExit();
                }

            }
        });

        //change group Icon
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(GroupInfoActivity.this);
            }
        });

        // add member
        addParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(GroupInfoActivity.this, AddNewGroupMemberActivity.class);
                in.putExtra("group",group);
                startActivity(in);
            }
        });

    }


    private void selfExit() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("Exit Group ");
        ab.setMessage("Are you sure");
        ab.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                groupReference.child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        memberReference.child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(GroupInfoActivity.this, "You Exit group", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    }
                });
            }
        });
        ab.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        ab.show();
    }

    //remove member
    private void removeMember() {
        new ItemTouchHelper(new DeleteOnSwipe(0, ItemTouchHelper.RIGHT| ItemTouchHelper.LEFT))
                .attachToRecyclerView(rv);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //options created
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setIndexedQuery(memberReference,userReference,User.class)
                .build();
        adapter = new MemberListAdapter(options);

        // details of member data
        adapter.onClickUserListener(new MemberListAdapter.OnClickRecyclerViewListener() {
            @Override
            public void onItemClick(final User user, int position) {
                DatabaseReference messageReference = FirebaseDatabase.getInstance().getReference("Group").child(group.getGroupId()).child("message");
                messageReference.orderByChild("from").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            memberMessageList = new ArrayList<>();
                            Iterator<DataSnapshot> itr = snapshot.getChildren().iterator();
                            while (itr.hasNext()){
                                DataSnapshot ds = itr.next();
                                Message msg = ds.getValue(Message.class);
                                memberMessageList.add(msg);
                            }
                            // show data of member with messages
                            ShowDataInListView(memberMessageList,user);
                        }
                        else {
                            Toast.makeText(GroupInfoActivity.this, "No Messages sent by "+user.getName(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        rv.setAdapter(adapter);
        adapter.startListening();

    }

    private void ShowDataInListView(ArrayList<Message> memberMessageList, User user) {
        final AlertDialog ab = new AlertDialog.Builder(GroupInfoActivity.this).create();
        View v = LayoutInflater.from(GroupInfoActivity.this).inflate(R.layout.info_message_group_list,null);
        ab.setView(v);

        TextView tvName = v.findViewById(R.id.tvName);
        ImageView cancel = v.findViewById(R.id.cancel);
        ListView lv = v.findViewById(R.id.lv);
        tvName.setText("Message Sent By "+user.getName());
       GroupMemberMessageAdapter adapter = new GroupMemberMessageAdapter(GroupInfoActivity.this,memberMessageList);
       lv.setAdapter(adapter);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ab.dismiss();
            }
        });
        ab.show();


    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        groupReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initComponent() {
        back = findViewById(R.id.back);
        toolbar = findViewById(R.id.toolbar);
        edit = findViewById(R.id.edit);
        backDrop = findViewById(R.id.backdrop);
        addParticipant = findViewById(R.id.addParticipant);
        rv = findViewById(R.id.rv);
        btnExit = findViewById(R.id.btnExit);
        toolbar.setTitle(group.getName());
        setSupportActionBar(toolbar);


    }



    //class for delete on swipe
    public class DeleteOnSwipe extends ItemTouchHelper.SimpleCallback{

        public DeleteOnSwipe(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            final User u = adapter.getItem(position);
            memberReference.child(u.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        groupReference.child(u.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Snackbar.make(rv,""+u.getName()+" Removed", Snackbar.LENGTH_LONG)
                                        .setAction("Undo", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                memberReference.child(u.getUid()).setValue(u.getName()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            groupReference.child(u.getUid()).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        Snackbar.make(rv,"Member Added", Snackbar.LENGTH_LONG);
                                                                    }else{
                                                                        Toast.makeText(GroupInfoActivity.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }
                                        }).show();
                            }
                        });
                    }
                }
            });
        }
    }


    // Group icon set

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Image Loading...");
        pd.setMessage("Wait...");
        pd.show();
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final Uri imageUri = result.getUri();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference("Group Icon");
                StorageReference filePath = storageReference.child(group.getGroupId()+".jpg");
                filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {
                                String  imgUri = uri.toString();
                                groupReference.child("icon").setValue(imgUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        pd.dismiss();
                                        if(task.isSuccessful()){
                                            backDrop.setImageURI(imageUri);
                                            Toast.makeText(GroupInfoActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            String message = task.getException().toString();
                                            Toast.makeText(GroupInfoActivity.this,message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });

            }
            else{
                pd.dismiss();
            }
        }
    }

}
