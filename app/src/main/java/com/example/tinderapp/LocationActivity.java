package com.example.tinderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.OnMapReadyCallback;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.fragment.app.FragmentActivity;


public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {

    Button btLocation;
    TextView textView1,textView2,textView3,textView4,textView5;
    FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap myGoogleMap;

    private FirebaseAuth mAuth;
    private DatabaseReference usersDb;
    public String currentUID;
    private DatabaseReference myRef;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();


        btLocation = (Button) findViewById(R.id.bt_location);
        textView1 =(TextView) findViewById(R.id.text_view1);
        textView2 =(TextView) findViewById(R.id.text_view2);
        textView3 =(TextView) findViewById(R.id.text_view3);
        textView4 =(TextView) findViewById(R.id.text_view4);
        textView5 =(TextView) findViewById(R.id.text_view5);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(LocationActivity.this);
        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checking permission


            }
        });

    }



    public void onMapReady(GoogleMap googleMap) {

        myGoogleMap = googleMap;

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mAuth = FirebaseAuth.getInstance();
                currentUID = mAuth.getCurrentUser().getUid();
                String lat = dataSnapshot.child("Users").child(currentUID).child("location").child("latitude").getValue().toString();
                String lon = dataSnapshot.child("Users").child(currentUID).child("location").child("longitude").getValue().toString();
                double myLatitude = Double.parseDouble(lat);
                double myLongitude = Double.parseDouble(lon);
                LatLng latLng = new LatLng(myLatitude,myLongitude);
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here.");
                myGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                myGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                myGoogleMap.addMarker(markerOptions);

/*
                for(DataSnapshot ds : dataSnapshot.child("Users").getChildren()){
                    String userName = ds.child("name").getValue().toString();
                    double latitude = Double.parseDouble(ds.child("location").child("latitude").getValue().toString());
                    double longitude = Double.parseDouble(ds.child("location").child("longitude").getValue().toString());
                    double distance = distance(myLatitude,myLongitude,latitude,longitude);
                    LatLng latLngFB = new LatLng(latitude,longitude);
                    MarkerOptions markerOptionsFB = new MarkerOptions().position(latLngFB).title(userName + "\r" + Math.round(distance) + "km");
                    myGoogleMap.addMarker(markerOptionsFB);
                }
*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 44:
                if(grantResults.length>0 && grantResults[0]  == PackageManager.PERMISSION_GRANTED){
                //    getLocation();
                }
                break;


        }
    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515* 1.609344;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}