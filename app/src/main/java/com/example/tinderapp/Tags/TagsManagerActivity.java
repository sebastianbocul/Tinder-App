package com.example.tinderapp.Tags;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;
import com.example.tinderapp.LoginActivity;
import com.example.tinderapp.R;
import com.example.tinderapp.SettingsActivity;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TagsManagerActivity extends AppCompatActivity {



    private FirebaseAuth mAuth;
    private String currentUserId;
    private Context context= TagsManagerActivity.this;
    private StorageReference filePath;
    // get seekbar from view
    private TextView ageRangeTextView,maxDistanceTextView;
    private CrystalRangeSeekbar ageRangeSeeker;
    private CrystalSeekbar maxDistanceSeeker;
    private TagsManagerAdapter adapter;
    private ArrayList<String> myTagsList = new ArrayList<>();
    private Button addTagButton;
    private EditText tagsEditText;
    private RadioGroup mRadioGroup;
    private String ageMin,ageMax,distanceMax;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_manager);
        mAuth = FirebaseAuth.getInstance();
        currentUserId= mAuth.getCurrentUser().getUid();
        ageRangeSeeker = findViewById(R.id.ageRangeSeeker);
        ageRangeTextView = findViewById(R.id.ageRangeTextView);
        maxDistanceSeeker = findViewById(R.id.distanceSeeker);
        maxDistanceTextView = findViewById(R.id.maxDistanceTextView);
        fillTagsAdapter();
        addTagButton=findViewById(R.id.addButton);
        tagsEditText = findViewById(R.id.tagsEditText);
        mRadioGroup=findViewById(R.id.radioGroup);
        // set listener
        ageRangeSeeker.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                ageRangeTextView.setText("Age range: " + minValue.toString() + " - " +maxValue.toString());
                ageMin=minValue.toString();
                ageMax=maxValue.toString();
            }
        });

        // set listener
        maxDistanceSeeker.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue) {
                maxDistanceTextView.setText("Max distance: " + String.valueOf(minValue) + " km");
                distanceMax=minValue.toString();
            }
        });

        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTagButtonFunction();
            }
        });


    }

    private void addTagButtonFunction() {
        if(tagsEditText.getText().toString().length()==0){
            Toast.makeText(TagsManagerActivity.this,"Fill tag name",Toast.LENGTH_SHORT).show();
            return;
        }


        if(mRadioGroup.getCheckedRadioButtonId()==-1){
            Toast.makeText(TagsManagerActivity.this, "Choose gender to find", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedId = mRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        String tagName = tagsEditText.getText().toString();
        String gender= radioButton.getText().toString();
        String mAgeMax = ageMax;
        String mAgeMin=ageMin;
        Map tag = new HashMap();
        tag.put("name",tagName);
        tag.put("ageMax",mAgeMax);
        tag.put("ageMin",mAgeMin);
        tag.put("lookForGender",gender);
        System.out.println("ABBBBBBBBBBBBBBBB   " +  tag);


    }


    public void logoutUser() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(TagsManagerActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        return;
    }



    private void deleteDatabaseAndStorage(){

        filePath = FirebaseStorage.getInstance().getReference().child("images").child(currentUserId);
        StorageReference storageRef = filePath;
        // Delete the userStorage
        storageRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            item.delete();
                        }
                        logoutUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });


    }
    private void fillTagsAdapter() {
        //  super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        // data to populate the RecyclerView with





        DatabaseReference ds = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("tags");
        ArrayList<String> myTags = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.tagsRecyclerView);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(TagsManagerActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        ds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        myTags.add("#"+ ds.getKey());
                        myTagsList.add(ds.getKey());

                    }
                    adapter = new TagsManagerAdapter(TagsManagerActivity.this, myTagsList);
                    recyclerView.setAdapter(adapter);
                    adapter.setClickListener(new TagsManagerAdapter.ItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                        }

                        @Override
                        public void onDeleteClick(int position) {
                            System.out.println("AAABBSBA " +position);
                            removeItem(position);
                        }


                    });

                }
                else {
                    myTags.add("Add tags in options first!");
                    adapter = new TagsManagerAdapter(TagsManagerActivity.this, myTagsList);
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
    public void removeItem(int position) {
        myTagsList.remove(position);
        adapter.notifyItemRemoved(position);
    }

}
