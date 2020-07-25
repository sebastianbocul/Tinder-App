package com.pinder.app.Tags;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.pinder.app.Tags.MainTags.TagsFragment;
import com.pinder.app.Tags.PopularTags.PopularTagsFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNoOfTabs;

    public PagerAdapter(@NonNull FragmentManager fm, int NumberOfTabs) {
        super(fm);
        this.mNoOfTabs = NumberOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                PopularTagsFragment popularTagsFragment = new PopularTagsFragment();
                return popularTagsFragment;
            case 1:
                TagsFragment tagsManagerFragment = new TagsFragment();
                return tagsManagerFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}
