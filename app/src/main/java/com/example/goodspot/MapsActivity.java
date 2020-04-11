package com.example.goodspot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    //Layout
    private GoogleMap mMap;
    private ImageButton btnType;
    private Spinner spinnerType;
    private Toolbar mToolbar;

    //Location
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    //Markers
    public static List<Marker> allMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        btnType = findViewById(R.id.btnType);
        spinnerType = findViewById(R.id.spinnerType);
        mToolbar =findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Check Permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }

        //Lecture des éléments de la base de données
        new FirebaseDatabaseHelper().readMarkers(new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Marker> markers, List<String> keys) {
                MapsActivity.allMarkers = markers;
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    //Option Menu de la ToolBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getLocation();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true); //Affichage du bouton de localisation de Google Maps
        }

        //Fonction utilisée pour formater le snippet du marker
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(com.google.android.gms.maps.model.Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(com.google.android.gms.maps.model.Marker marker) {
                LinearLayout info = new LinearLayout(MapsActivity.this);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(MapsActivity.this);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(MapsActivity.this);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        btnType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear(); //Reset des markers
                for (Marker marker : allMarkers){
                    if (marker.getType().equals(spinnerType.getSelectedItem().toString())){ // Vérification du type du marker correspond au choix selectionné
                        displayMarkers(marker);
                    }
                    else if (spinnerType.getSelectedItem().toString().equals("Tous")){ //Exception pour la selection "Tous" car ne correspond pas à un type
                        displayMarkers(marker);
                    }
                }
                getLocation(); //Recentrer sur la localisation après la selection
            }
        });
    }

    //Obtenir la localisation de l'appareil
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
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),10f));
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

    //Affichage pour un marker donné
    private void displayMarkers(Marker marker){
        LatLng m1 = new LatLng(Double.parseDouble(marker.getLongitude()),Double.parseDouble(marker.getLatitude()));
        //Affichage de l'icone en fonction de son type
        switch (marker.getType()){
            case "Chateau":
                mMap.addMarker(new MarkerOptions().position(m1).title(marker.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.castle)).snippet("Description : \n" + marker.getDescription() + "\n\n"
                        + "Catégorie : \t" + marker.getType()));
                break;
            case "Lac":
                mMap.addMarker(new MarkerOptions().position(m1).title(marker.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.lake)).snippet("Description : \n" + marker.getDescription() + "\n\n"
                        + "Catégorie : \t" + marker.getType()));
                break;
            default:
                break;
        }
    }
}
