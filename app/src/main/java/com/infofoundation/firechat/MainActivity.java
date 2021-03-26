package com.infofoundation.firechat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infofoundation.firechat.adapter.TabAccessorAdapter;
import com.infofoundation.firechat.beans.Status;
import com.infofoundation.firechat.beans.User;
import com.infofoundation.firechat.menuunder.FindFriendsActivity;
import com.infofoundation.firechat.menuunder.NewGroupActivity;
import com.infofoundation.firechat.menuunder.UpdateProfile;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ViewPager vp;
    TabLayout tb;
    TabAccessorAdapter adapter;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String currentUserId;
    DatabaseReference userReference;
    CircleImageView mainImage;
    TextView tvFunChat;
    DatabaseReference statusReference,contactsReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        adapter = new TabAccessorAdapter(getSupportFragmentManager(),1);
        vp.setAdapter(adapter);
        tb.setupWithViewPager(vp);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser == null){
            sendUserToLoginActivity();
        }
        else{
            statusReference = FirebaseDatabase.getInstance().getReference("Status");
            contactsReference = FirebaseDatabase.getInstance().getReference("Contacts");
            updateProfileStatusCheck();
            updateUserOnlineState("online");
            deleteStatusOfFriends();
        }
    }

    private void deleteStatusOfFriends() {
        String currentUserId = currentUser.getUid();
        deleteStatusOlderThanTwentyFourHour(currentUserId);
        contactsReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    final Iterator<DataSnapshot>itr = snapshot.getChildren().iterator();
                    while (itr.hasNext()){
                        DataSnapshot ds = itr.next();
                        final String userId = ds.getKey();
                        deleteStatusOlderThanTwentyFourHour(userId);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteStatusOlderThanTwentyFourHour(final String userId) {
        statusReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Iterator<DataSnapshot> itr2 = snapshot.getChildren().iterator();
                    while(itr2.hasNext()) {
                        DataSnapshot ds = itr2.next();
                        Status status = ds.getValue(Status.class);
                        Calendar calendar = Calendar.getInstance();
                        long cTime = calendar.getTimeInMillis();
                        long statusTime = status.getTimeStamp();
                        if (cTime - statusTime >= 86400000) {
                            statusReference.child(userId).child(status.getStatusId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateUserOnlineState("offline");
    }

    private void updateProfileStatusCheck() {
        currentUserId = currentUser.getUid();
        userReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("name").exists()){
                    User u = snapshot.getValue(User.class);
                    Picasso.get().load(u.getImgUri()).placeholder(R.drawable.logo2).into(mainImage);
                    tvFunChat.setText(u.getName());
                }else{
                    sendUserToUpdateProfile();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendUserToUpdateProfile() {
        Intent sendUserUpdateProfile = new Intent(MainActivity.this, UpdateProfile.class);
        startActivity(sendUserUpdateProfile);
    }

    private void sendUserToLoginActivity() {
        Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
        loginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginActivity);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.search){

        }else if(id == R.id.findFriend){
            sendUserToFindFriend();

        }else if(id == R.id.group){
            Intent in = new Intent(MainActivity.this, NewGroupActivity.class);
            startActivity(in);

        }else if(id == R.id.updateProfile) {
           sendUserToUpdateProfile();
        }else if(id == R.id.signOut){
            updateUserOnlineState("offline");
            mAuth.signOut();
            sendUserToLoginActivity();
        }else if(id == R.id.delete){

            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle("Delete Account");
            ad.setMessage("Are you sure ");

            ad.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    AuthUI.getInstance()
                            .delete(MainActivity.this)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    sendUserToLoginActivity();
                                }
                            });
                }
            });
            ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(MainActivity.this, "canceled", Toast.LENGTH_SHORT).show();
                }
            });
            ad.show();


        }

        return super.onOptionsItemSelected(item);
    }

    private void sendUserToFindFriend() {
        Intent sendToUserFreind = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(sendToUserFreind);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(currentUser!=null){
            updateUserOnlineState("offline");
        }
    }

    private void updateUserOnlineState(String state){
        if(currentUser!=null) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sd = new SimpleDateFormat("MMM dd , yyy");
            String date = sd.format(calendar.getTime());
            sd = new SimpleDateFormat("hh:mm a");
            String time = sd.format(calendar.getTime());
            HashMap<String, Object> hm = new HashMap<>();
            hm.put("date", date);
            hm.put("time", time);
            hm.put("state", state);
            userReference.child(currentUserId).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        String error = task.getException().toString();
                        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void initComponent() {
        toolbar = findViewById(R.id.mainToolBar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FunChat");
        vp =findViewById(R.id.mainViewPager);
        tb = findViewById(R.id.mainTabLayout);
        mainImage = findViewById(R.id.mainImage);
        tvFunChat = findViewById(R.id.funChat);
    }
}