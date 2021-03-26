package com.infofoundation.firechat.menuunder;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infofoundation.firechat.R;
import com.infofoundation.firechat.adapter.FriendListAdapter;
import com.infofoundation.firechat.beans.User;


public class FindFriendsActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView rv;
    ImageView imgBack,imgSearch;
    TextView tvFindFriends;
    EditText etSearch;
    DatabaseReference reference;
    String name = "";
    FirebaseRecyclerAdapter<User, FriendListAdapter.FriendViewHolder> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_freinds);
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        initComponent();
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.setVisibility(View.VISIBLE);
                tvFindFriends.setVisibility(View.GONE);
                etSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        name = charSequence.toString();
                        onStart();
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<User> options;
        if(name.equals("")) {
            options = new FirebaseRecyclerOptions.Builder<User>()
                    .setQuery(reference, User.class)
                    .build();
        }else{
            options = new FirebaseRecyclerOptions.Builder<User>()
                    .setQuery(reference.orderByChild("name").startAt(name).endAt(name+"\uf8ff"), User.class)
                    .build();
        }

        adapter = new FriendListAdapter(options,this);
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
        toolbar = findViewById(R.id.findFriendToolbar);
        setSupportActionBar(toolbar);
        imgBack = findViewById(R.id.imgBack);
        imgSearch = findViewById(R.id.search);
        etSearch = findViewById(R.id.etSearch);
        tvFindFriends = findViewById(R.id.tvFindFriend);
        rv = findViewById(R.id.rv);
    }
}
