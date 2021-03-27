package com.infofoundation.firechat.menuunder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.infofoundation.firechat.MainActivity;
import com.infofoundation.firechat.R;
import com.infofoundation.firechat.beans.User;
import com.infofoundation.firechat.databinding.UpdateProfileSettingBinding;
import com.squareup.picasso.Picasso;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfile extends AppCompatActivity {
    DatabaseReference reference;
    String currentUserId;
    User user;
    ProgressDialog pd;
    StorageReference storageReference;
    UpdateProfileSettingBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UpdateProfileSettingBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserId = FirebaseAuth.getInstance().getUid();
        dataRetrieveStatus();
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Image");
        binding.btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = binding.etName.getText().toString();
                String status = binding.etStatus.getText().toString();
                if(TextUtils.isEmpty(userName)){
                    binding.etName.setError("Please Enter Name");
                    return;
                }
                if(TextUtils.isEmpty(status)){
                        status = "Hi I Am Using Fun Chat";
                    }
                HashMap<String,String>hm = new HashMap<>();
                hm.put("name",userName);
                hm.put("status",status);
                hm.put("uid",currentUserId);
                if(user.getImgUri()!=null){
                    hm.put("imgUri",user.getImgUri());
                }
                reference.child(currentUserId).setValue(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(UpdateProfile.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                            Intent sendUserToMain = new Intent(UpdateProfile.this, MainActivity.class);
                            sendUserToMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(sendUserToMain);
                            finish();
                        }

                        else{
                            String message = task.getException().toString();
                            Toast.makeText(UpdateProfile.this,message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        binding.circleImageIcon.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(UpdateProfile.this);
            }
        });

    }

    private void dataRetrieveStatus() {
        reference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    user = snapshot.getValue(User.class);
                    binding.etName.setText(user.getName());
                    binding.etStatus.setText(user.getStatus());
                    Picasso.get().load(user.getImgUri()).placeholder(R.drawable.logo).into(binding.circleImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                final Uri imageUri = result.getUri();
                StorageReference filePath = storageReference.child(currentUserId+".jpg");
                filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {
                                String  imgUri = uri.toString();
                                reference.child(currentUserId).child("imgUri").setValue(imgUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        pd.dismiss();
                                        if(task.isSuccessful()){
                                            binding.circleImage.setImageURI(imageUri);
                                            Toast.makeText(UpdateProfile.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            String message = task.getException().toString();
                                            Toast.makeText(UpdateProfile.this,message, Toast.LENGTH_SHORT).show();
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
