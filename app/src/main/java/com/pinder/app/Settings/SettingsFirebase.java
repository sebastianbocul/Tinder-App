package com.pinder.app.Settings;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsFirebase implements SettingsFirebaseDao {
    private SettingInfoObject bufferInfo = new SettingInfoObject();
    private MutableLiveData<String> date = new MutableLiveData<>();
    private MutableLiveData<Boolean> showMyLocation = new MutableLiveData<>();
    private MutableLiveData<Boolean> sortByDistance = new MutableLiveData<>();
    private static SettingsFirebase instance = null;

    public static SettingsFirebase getInstance() {
        if (instance == null) {
            instance = new SettingsFirebase();
            instance.loadDataFromDb();
        }
        return instance;
    }

    private void loadDataFromDb() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference myDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        myDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("SettingsFirebase", "onDataChange");
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("dateOfBirth").exists()) {
                        String dateOfBirth = dataSnapshot.child("dateOfBirth").getValue().toString();
                        date.postValue(dateOfBirth);
                        bufferInfo.setDate(dateOfBirth);
                    }
                    if (dataSnapshot.child("showMyLocation").exists()) {
                        Boolean showMyLocationBool = (Boolean) dataSnapshot.child("showMyLocation").getValue();
                        showMyLocation.postValue(showMyLocationBool);
                        bufferInfo.setShowMyLocation(showMyLocationBool);
                    }
                    if (dataSnapshot.child("sortByDistance").exists()) {
                        Boolean sortByDistanceBool = (Boolean) dataSnapshot.child("sortByDistance").getValue();
                        sortByDistance.postValue(sortByDistanceBool);
                        bufferInfo.setSortByDistance(sortByDistanceBool);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void updateMyDb(Boolean dateValid) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference myDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        if (showMyLocation.getValue() == null || sortByDistance.getValue() == null || date.getValue() == null)
            return;
        if (showMyLocation.getValue() != bufferInfo.getShowMyLocation()) {
            myDatabaseReference.child("showMyLocation").setValue(showMyLocation.getValue());
            bufferInfo.setShowMyLocation(showMyLocation.getValue());
        }
        if (sortByDistance.getValue() != bufferInfo.getSortByDistance()) {
            myDatabaseReference.child("sortByDistance").setValue(sortByDistance.getValue());
            bufferInfo.setSortByDistance(sortByDistance.getValue());
        }
        if (dateValid == false) {
            date.setValue(bufferInfo.getDate());
            return;
        }
        String dateOfBirth = date.getValue().toString();
        if (!dateOfBirth.equals(bufferInfo.getDate())) {
            myDatabaseReference.child("dateOfBirth").setValue(dateOfBirth);
            bufferInfo.setDate(dateOfBirth);
        }
    }

    @Override
    public LiveData<String> getDate() {
        return date;
    }

    @Override
    public LiveData<Boolean> getSortByDistance() {
        return sortByDistance;
    }

    @Override
    public LiveData<Boolean> getShowMyLocation() {
        return showMyLocation;
    }

    @Override
    public void setDate(String date) {
        this.date.setValue(date);
    }

    @Override
    public void setSortByDistance(Boolean bool) {
        this.sortByDistance.postValue(bool);
    }

    @Override
    public void setShowMyLocation(Boolean bool) {
        this.showMyLocation.postValue(bool);
    }
}
