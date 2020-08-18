package com.pinder.app.repository;

import androidx.lifecycle.MutableLiveData;

import com.pinder.app.models.TagsObject;
import com.pinder.app.persistance.TagsFirebase;
import com.pinder.app.persistance.TagsFirebaseDao;

import java.util.List;

public class TagsRepository implements TagsFirebaseDao {
    private MutableLiveData<List<TagsObject>> tagList = new MutableLiveData<List<TagsObject>>();
    public static TagsRepository instance = null;

    public static synchronized TagsRepository getInstance() {
        if (instance == null) {
            instance = new TagsRepository();
        }
        return instance;
    }

    @Override
    public MutableLiveData<List<TagsObject>> getAllTags() {
        tagList = TagsFirebase.getInstance().getAllTags();
        return tagList;
    }

    @Override
    public void deleteTag(TagsObject tag) {
        TagsFirebase.getInstance().deleteTag(tag);
    }

    @Override
    public void addTag(TagsObject tag) {
        TagsFirebase.getInstance().addTag(tag);
    }
}