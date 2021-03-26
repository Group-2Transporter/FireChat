package com.infofoundation.firechat.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.infofoundation.firechat.R;
import com.infofoundation.firechat.beans.Message;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatMessageBottomSheet extends BottomSheetDialogFragment {
    String currentUserId,groupId,userIcon;
    StorageReference storageReference;
    DatabaseReference groupReference;
    ProgressDialog pd;
    public GroupChatMessageBottomSheet(String currentUserId, String groupId, String userIcon) {
        this.currentUserId = currentUserId;
        this.groupId = groupId;
        this.userIcon =userIcon;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.attach_bottom_list,container,false);
        CircleImageView civPdf = v.findViewById(R.id.civPdf);
        CircleImageView civWord = v.findViewById(R.id.civWord);
        storageReference = FirebaseStorage.getInstance().getReference("Document Messages");
        groupReference = FirebaseDatabase.getInstance().getReference("Group").child(groupId);
        civPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_GET_CONTENT);
                in.setType("application/pdf");
                startActivityForResult(Intent.createChooser(in,"send pdf file"),111);
            }
        });
        civWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_GET_CONTENT);
                in.setType("application/msword");
                startActivityForResult(Intent.createChooser(in,"send word file"),444);

            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==111 && resultCode==getActivity().RESULT_OK && data!=null && data.getData()!=null){
            Uri fileUri = data.getData();
            sendUserToDocumentMessages(fileUri,"pdf");
        }else if(requestCode==444 && resultCode==getActivity().RESULT_OK && data!=null && data.getData()!=null) {
            Uri fileUri = data.getData();
            sendUserToDocumentMessages(fileUri,"word");
        }
    }
    private void sendUserToDocumentMessages(Uri fileUri, final String type){
        pd = new ProgressDialog(getContext());
        pd.setTitle("Sending Documents");
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.show();
        final String messageId = groupReference.push().getKey();
        StorageReference filePath = storageReference.child(messageId+"."+type);
        filePath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String fileUri = uri.toString();
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sd = new SimpleDateFormat("MMM dd,yyy");
                        String date = sd.format(calendar.getTime());
                        sd = new SimpleDateFormat("hh:mm a");
                        String time = sd.format(calendar.getTime());
                        long timeStamp = calendar.getTimeInMillis();
                        final Message message = new Message();
                        message.setDate(date);
                        message.setTime(time);
                        message.setMessage(fileUri);
                        message.setMessageId(messageId);
                        message.setTimeStamp(timeStamp);
                        message.setType(type);
                        message.setFrom(currentUserId);
                        message.setSenderIcon(userIcon);
                        groupReference.child("message").child(messageId).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.dismiss();
                                if(!task.isSuccessful()){
                                    Toast.makeText(getContext(),task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

            }
        });

    }
}
