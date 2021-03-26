package com.infofoundation.firechat.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.infofoundation.firechat.R;
import com.infofoundation.firechat.beans.Group;
import com.infofoundation.firechat.beans.User;
import com.squareup.picasso.Picasso;


import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminBottom extends BottomSheetDialogFragment {
    User user;
    DatabaseReference groupReference,memberReference;
    Group group;
    String currentUser;

    public AdminBottom(User user, DatabaseReference groupReference, DatabaseReference memberReference, Group group, String currentUser) {
        this.user = user;
        this.groupReference = groupReference;
        this.memberReference = memberReference;
        this.group = group;
        this.currentUser = currentUser;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_admin,container,false);
        CircleImageView civProfile = v.findViewById(R.id.civAdmin);
        TextView tvName = v.findViewById(R.id.tvUserName);
        Button btnExit = v.findViewById(R.id.btnExit);
        Button btnCancel = v.findViewById(R.id.btnCancel);
        Picasso.get().load(user.getImgUri()).placeholder(R.drawable.logo2).into(civProfile);
        tvName.setText(user.getName());
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,Object>hm = new HashMap<>();
                hm.put("createdBy",user.getUid());

                groupReference.child(group.getGroupId()).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            groupReference.child(group.getGroupId()).child(currentUser).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    memberReference.child(currentUser).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(getContext(), "You Exit group", Toast.LENGTH_SHORT).show();
                                                dismiss();
                                                getActivity().finish();
                                            }else{
                                                Toast.makeText(getContext(),task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }else{
                            Toast.makeText(getContext(),task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        return v;
    }
}
