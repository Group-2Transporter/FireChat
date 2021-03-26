package com.infofoundation.firechat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.infofoundation.firechat.adapter.GroupShowChatAdapter;
import com.infofoundation.firechat.beans.Group;
import com.infofoundation.firechat.beans.Message;
import com.infofoundation.firechat.beans.User;
import com.infofoundation.firechat.fragment.GroupChatMessageBottomSheet;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity {
    CircleImageView groupIcon,send,voice;
    ImageView attach,camera;
    EditText etMessage;
    Toolbar toolbar;
    TextView groupName,groupDes;
    String currentUserId,senderImageUrl;
    ListView lv;
    LinearLayout ll;
    DatabaseReference groupReference,userReference;
    StorageReference storageReference;
    ArrayList<Message>al;
    GroupShowChatAdapter adapter;
    ProgressDialog pd;
    LinearLayout info;
    Group group;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groupmessagechat_activity);

        //get Intent
        Intent in = getIntent();
        final Group group = (Group) in.getSerializableExtra("group");



        initComponent();

        //check group members and information
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(GroupChatActivity.this, GroupInfoActivity.class);
                in.putExtra("info",group);
                startActivity(in);
            }
        });

        //set group Info
        groupName.setText(group.getName());
        Picasso.get().load(group.getIcon()).placeholder(R.drawable.logo2).into(groupIcon);

        //Getting current UserId and GroupReference
        groupReference = FirebaseDatabase.getInstance().getReference("Group").child(group.getGroupId());
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference("imageMessages");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //on text icon changer
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().isEmpty()){
                    voice.setVisibility(View.VISIBLE);
                    send.setVisibility(View.INVISIBLE);
                }else{
                    voice.setVisibility(View.INVISIBLE);
                    send.setVisibility(View.VISIBLE);
                }
            }
        });

        // current user image send
        userReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User u = snapshot.getValue(User.class);
                    senderImageUrl = u.getImgUri();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //send message in group code
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etMessage.getText().toString();
                if(TextUtils.isEmpty(message)){
                    return;
                }
                etMessage.setText("");
                final String messageId = groupReference.push().getKey();
                Calendar cDate= Calendar.getInstance();
                SimpleDateFormat sd = new SimpleDateFormat("MMM dd , yyy");
                String date = sd.format(cDate.getTime());
                sd = new SimpleDateFormat("hh:mm a");
                String time = sd.format(cDate.getTime());
                long timeStamp = cDate.getTimeInMillis();
                final Message msg = new Message();
                msg.setDate(date);
                msg.setTime(time);
                msg.setFrom(currentUserId);
                msg.setMessage(message);
                msg.setMessageId(messageId);
                msg.setType("text");
                msg.setTimeStamp(timeStamp);
                msg.setSenderIcon(senderImageUrl);
                groupReference.child("message").child(messageId).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            if(task.isSuccessful()){

                            }else{
                                String errorMessage = task.getException().toString();
                                Toast.makeText(GroupChatActivity.this,errorMessage,Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });


        // data receive and send to showing messages
        groupReference.child("message").orderByChild("timeStamp").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    Message msg = snapshot.getValue(Message.class);
                    al.add(msg);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged (@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // camera send message
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_GET_CONTENT);
                in.setType("image/*");
                startActivityForResult(Intent.createChooser(in,"select"),111);
            }
        });


        //attach send message code
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GroupChatMessageBottomSheet dialog = new GroupChatMessageBottomSheet(currentUserId,group.getGroupId(),senderImageUrl);
                dialog.show(getSupportFragmentManager(),"send documents");
            }
        });


        //finish activity
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        groupReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                    finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initComponent() {
        groupIcon = findViewById(R.id.groupImage);
        groupName = findViewById(R.id.groupName);
        send = findViewById(R.id.groupSend);
        voice = findViewById(R.id.groupVoice);
        attach = findViewById(R.id.groupAttachFile);
        camera = findViewById(R.id.groupCamera);
        etMessage = findViewById(R.id.groupEditText);
        info = findViewById(R.id.llClick);
        toolbar = findViewById(R.id.groupToolbar);
        groupDes = findViewById(R.id.groupDescription);
        groupDes.setText("Click for more information");
        lv = findViewById(R.id.lv);
        ll = findViewById(R.id.groupll);
        al = new ArrayList<>();
        setSupportActionBar(toolbar);
        adapter = new GroupShowChatAdapter(this,al);
        lv.setAdapter(adapter);

    }

    // Camera image recived code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==111&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            pd = new ProgressDialog(this);
            pd.setTitle("Sending Image");
            pd.setMessage("wait...");
            pd.setCancelable(false);
            pd.show();
            Uri imageUri = data.getData();
            final String messageId = groupReference.push().getKey();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sd = new SimpleDateFormat("MMM dd , yyy");
            final String date = sd.format(calendar.getTime());
            sd = new SimpleDateFormat("hh:mm a");
            final String time = sd.format(calendar.getTime());
            final long timeStamp = calendar.getTimeInMillis();
            StorageReference filePath = storageReference.child(messageId+".jpeg");
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            final Message msg = new Message();
                            msg.setType("image");
                            msg.setFrom(currentUserId);
                            msg.setDate(date);
                            msg.setTime(time);
                            msg.setSenderIcon(senderImageUrl);
                            msg.setTimeStamp(timeStamp);
                            msg.setMessageId(messageId);
                            msg.setMessage(imageUrl);
                            groupReference.child("message").child(messageId).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    pd.dismiss();
                                    if(!task.isSuccessful()){
                                        String error = task.getException().toString();
                                        Toast.makeText(GroupChatActivity.this,error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });
                }
            });

        }
    }
}
