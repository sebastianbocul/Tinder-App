package com.pinder.app.Tags.PopularTags;

import android.app.Application;
import android.util.Log;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class PopularTagsFragmentViewModelTest {
    @Test
    public void getAllPopularTags() {
    }

    @Test
    public void sortCollection() {
        PopularTagsObject o1 = new PopularTagsObject("AAA",5);
        PopularTagsObject o2 = new PopularTagsObject("BBB",10);
        PopularTagsObject o3 = new PopularTagsObject("CCC",1);
        PopularTagsObject o4 = new PopularTagsObject("DDD",20);
        PopularTagsObject o5 = new PopularTagsObject("EEE",3);
        List<PopularTagsObject> input = new ArrayList<>();
        input.addAll(Arrays.asList(o1,o2,o3,o4,o5));
        List<PopularTagsObject> expected = new ArrayList<>();
        expected.add(o4);
        expected.add(o2);
        expected.add(o1);
        expected.add(o5);
        expected.add(o3);
        Log.d("TEST", "input: " + input.toString());
        Log.d("TEST", "expected: " + expected.toString());
      //  PopularTagsFragmentViewModel vm = new PopularTagsFragmentViewModel();
    }
}