package com.infofoundation.firechat.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.infofoundation.firechat.fragment.FragmentChats;
import com.infofoundation.firechat.fragment.FragmentGroup;
import com.infofoundation.firechat.fragment.FragmentRequest;
import com.infofoundation.firechat.fragment.FragmentStatus;


public class TabAccessorAdapter extends FragmentPagerAdapter {
    public TabAccessorAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:fragment = new FragmentChats();break;
            case 1:fragment = new FragmentGroup();break;
            case 2:fragment = new FragmentStatus();break;
            case 3:fragment = new FragmentRequest();break;
        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position){
            case 0 : title = "Chats";break;
            case 1 : title = "Group";break;
            case 2 : title = "Status";break;
            case 3 : title = "Request";break;
        }
        return title;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
