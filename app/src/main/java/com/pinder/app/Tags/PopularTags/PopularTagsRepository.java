package com.pinder.app.Tags.PopularTags;

import androidx.lifecycle.LiveData;

import java.util.List;

public class PopularTagsRepository {
    private static LiveData<List<PopularTagsObject>> allPopularTags;
    private static PopularTagsRepository instance = null;

    public static synchronized PopularTagsRepository getInstance() {
        if (instance == null) {
            instance = new PopularTagsRepository();
        }
        return instance;
    }

    public LiveData<List<PopularTagsObject>> getAllPopularTags() {
        allPopularTags = new PopularTagsFirebase().getInstance().getAllPopularTags();
        return allPopularTags;
    }
}
