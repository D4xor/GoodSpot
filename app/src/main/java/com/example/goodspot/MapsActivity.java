package com.example.goodspot;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private String mName,mType;
    private double mLongitude,mLatitude;

    //Location
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    //Markers
    private LatLng mLatLng;
    private static ArrayList<MarkerOptions> mMarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Location
        //getLocation();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
    }
    /*currentLocation = location;
                    mLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng,16));
                    mMarkers.add(new MarkerOptions().position(mLatLng));
                    mMap.addMarker(new MarkerOptions()
                            .position(mLatLng)
                            .flat(true)
                            .title("Vous êtes ici")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.home)));*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getLocation();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }


        //Lecture des éléments de la base de données
        new FirebaseDatabaseHelper().readMarkers(new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Marker> markers, List<String> keys) {
                for (int i = 0; i < markers.size(); i++){
                    //Récupération des données
                    mType = markers.get(i).getType();
                    mName = markers.get(i).getName();
                    mLongitude = Double.parseDouble(markers.get(i).getLongitude());
                    mLatitude = Double.parseDouble(markers.get(i).getLatitude());

                    //Affichage du marker en fonction de son type
                    LatLng m1 = new LatLng(mLongitude,mLatitude);
                    if (mType.equals("lake")){
                        mMap.addMarker(new MarkerOptions().position(m1).title(mName).icon(BitmapDescriptorFactory.fromResource(R.drawable.lake)));
                    }
                    else if (mType.equals("castle")){
                        mMap.addMarker(new MarkerOptions().position(m1).title(mName).icon(BitmapDescriptorFactory.fromResource(R.drawable.castle)));
                    }

                }
            }
        });


    }

    public void Exit(View view){
        finish();
    }

    private void getLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            currentLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),16));
                        }
                        else{
                            Toast.makeText(MapsActivity.this,"Impossible d'obtenir la localisation",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e("tag","SecurityException");
        }
    }

}
