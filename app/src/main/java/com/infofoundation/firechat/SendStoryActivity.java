package com.infofoundation.firechat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.infofoundation.firechat.beans.Status;


import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SendStoryActivity extends AppCompatActivity {
    ImageView ivStatus;
    EditText etText;
    FloatingActionButton btnSend;
    DatabaseReference storyReference;
    StorageReference storageReference;
    String currentUserId;
    ProgressDialog pd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_status);
        Intent in = getIntent();
        final Uri imageUri = Uri.parse(in.getStringExtra("imageUri"));
        initComponent();
        databaseReferenceInit();
        ivStatus.setImageURI(imageUri);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 final String text = etText.getText().toString();
                if(TextUtils.isEmpty(text)){
                    etText.setText("");
                }
                final String statusId = storyReference.push().getKey();
                StorageReference filePath = storageReference.child(statusId+".jpg");
                pd = new ProgressDialog(SendStoryActivity.this);
                pd.setTitle("Uploading");
                pd.setMessage("Wait...");
                pd.show();
                pd.setCancelable(false);
                filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat sd = new SimpleDateFormat("MMM dd,yyy");
                                String date = sd.format(calendar.getTime());
                                sd = new SimpleDateFormat("hh:mm a");
                                String time = sd.format(calendar.getTime());
                                Long timeStamp = calendar.getTimeInMillis();
                                Status status = new Status(date,time,imageUrl,currentUserId, text,"image",statusId,timeStamp);
                                storyReference.child(currentUserId).child(statusId).setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        pd.dismiss();
                                        if(task.isSuccessful()){
                                            finish();
                                        }else{
                                            Toast.makeText(SendStoryActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });


    }

    private void databaseReferenceInit() {
        storyReference = FirebaseDatabase.getInstance().getReference("Status");
        storageReference = FirebaseStorage.getInstance().getReference("Status");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void initComponent() {
        ivStatus = findViewById(R.id.ivStatus);
        etText = findViewById(R.id.etText);
        btnSend = findViewById(R.id.btnSend);
    }
}
