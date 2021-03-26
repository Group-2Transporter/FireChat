package com.infofoundation.firechat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.infofoundation.firechat.adapter.MessageAdapter;
import com.infofoundation.firechat.beans.Message;
import com.infofoundation.firechat.beans.User;
import com.infofoundation.firechat.fragment.ChatMessageBottomSheet;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    CircleImageView messageImage,send,voice;
    TextView messageName,state;
    ImageView attach,camera;
    EditText etMessage;
    LinearLayout ll;
    ListView lv;
    Toolbar toolbar;
    StorageReference storageReference;
    String currentUserId,receiverId;
    ArrayList<Message>al;
    ArrayAdapter<Message>adapter;
    DatabaseReference messageReference,counterReference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messagechat_activity);
        initComponent();
        Intent in = getIntent();
        final User user = (User) in.getSerializableExtra("user");
        receiverId = user.getUid();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Picasso.get().load(user.getImgUri()).placeholder(R.drawable.logo).into(messageImage);
        messageName.setText(user.getName());
        if(user.getState()!=null){
            if(user.getState().equals("online")){
                state.setText("Online");
            }else{
                state.setText(user.getDate()+" "+user.getTime());
                state.setTextColor(getResources().getColor(R.color.black));
            }
        }
        messageReference = FirebaseDatabase.getInstance().getReference("Messages");
        storageReference = FirebaseStorage.getInstance().getReference("imageMessages");
        counterReference = FirebaseDatabase.getInstance().getReference("Message Counter");
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
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etMessage.getText().toString();
                if(TextUtils.isEmpty(message)){
                    return;
                }
                etMessage.setText("");
                final String messageId = messageReference.push().getKey();
                Calendar cDate= Calendar.getInstance();
                SimpleDateFormat sd = new SimpleDateFormat("MMM dd , yyy");
                String date = sd.format(cDate.getTime());
                sd = new SimpleDateFormat("hh:mm a");
                String time = sd.format(cDate.getTime());
                long timeStamp = cDate.getTimeInMillis();
                final Message msg = new Message(date,time,currentUserId,receiverId,messageId,"text",message,timeStamp);
                messageReference.child(currentUserId).child(receiverId).child(messageId).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            messageReference.child(receiverId).child(currentUserId).child(messageId).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        counterReference.child(receiverId).child(currentUserId).child(messageId).setValue("");
                                    }else{
                                        String errorMessage = task.getException().toString();
                                        Toast.makeText(MessageActivity.this,errorMessage,Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        messageReference.child(currentUserId).child(receiverId).orderByChild("timeStamp").addChildEventListener(new ChildEventListener() {
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
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_GET_CONTENT);
                in.setType("image/*");
                startActivityForResult(Intent.createChooser(in,"select"),111);
            }
        });
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatMessageBottomSheet dialog = new ChatMessageBottomSheet(currentUserId,receiverId);
                dialog.show(getSupportFragmentManager(),"send documents");
            }
        });
    }

    private void initComponent() {
        messageImage = findViewById(R.id.messageImage);
        messageName = findViewById(R.id.messageName);
        send = findViewById(R.id.messageSend);
        attach = findViewById(R.id.attachFile);
        camera = findViewById(R.id.messageCamera);
        etMessage = findViewById(R.id.messageEditText);
        voice = findViewById(R.id.messageVoice);
        toolbar = findViewById(R.id.messageToolBar);
        state = findViewById(R.id.messageOnlineState);
        lv = findViewById(R.id.lv);
        ll = findViewById(R.id.ll);
        setSupportActionBar(toolbar);
        al = new ArrayList<>();
        adapter = new MessageAdapter(MessageActivity.this,al);
        lv.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==111&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            Uri imageUri = data.getData();
            final String messageId = messageReference.push().getKey();
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
                            final Message msg = new Message(date,time,currentUserId,receiverId,messageId,"image",imageUrl,timeStamp);
                            messageReference.child(currentUserId).child(receiverId).child(messageId).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        messageReference.child(receiverId).child(currentUserId).child(messageId).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(!task.isSuccessful()){
                                                    String error = task.getException().toString();
                                                    Toast.makeText(MessageActivity.this,error, Toast.LENGTH_SHORT).show();
                                                }else{
                                                    counterReference.child(receiverId).child(currentUserId).child(messageId).setValue("");
                                                }
                                            }
                                        });
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
