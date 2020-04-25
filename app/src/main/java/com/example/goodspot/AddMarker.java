package com.example.goodspot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class AddMarker extends AppCompatActivity {

    //Layout
    private EditText name;
    private EditText desc;
    private Spinner spinner;
    private Button adds;

    //Location
    private Location mCurrentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    Marker mark;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);

        name =findViewById(R.id.name);
        desc=findViewById(R.id.desc);
        spinner=findViewById(R.id.spinner2);
        adds=findViewById(R.id.addit);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();


        adds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Marker mk = new Marker();
                mk.setName(name.getText().toString());
                mk.setDescription(desc.getText().toString());
                mk.setType(spinner.getSelectedItem().toString());
                mk.setLongitude(String.valueOf(mCurrentLocation.getLatitude()));
                mk.setLatitude(String.valueOf(mCurrentLocation.getLongitude()));
                mk.setPhotolink("unknown");
                Intent i = getIntent();
                int count = i.getIntExtra("values",0);
                new FirebaseDatabaseHelper().addMarkers(mk,count,new FirebaseDatabaseHelper.DataStatus() {
                    @Override
                    public void DataIsLoaded(List<Marker> markers, List<String> keys) {
                    }
                    @Override
                    public void DataIsInserted() {
                    }
                });
                finish();
            }
        });


    }

    private void fetchLastLocation() {
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mCurrentLocation = location;
                }
            }
        });
    }

}
