package com.example.goodspot.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goodspot.FirebaseDatabaseHelper;
import com.example.goodspot.Model.Marker;
import com.example.goodspot.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    //Layout
    private GoogleMap mMap;
    private ImageButton btnType,btnLocation,btnSearch;
    private Spinner spinnerType;
    private Toolbar mToolbar;
    private TextView textView;
    private EditText searchText;

    //Location
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;

    //Markers
    public static List<Marker> allMarkers;
    String nameit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        btnType = findViewById(R.id.btnType);
        btnLocation = findViewById(R.id.addLocation);
        btnSearch = findViewById(R.id.btnSearch);
        spinnerType = findViewById(R.id.spinnerType);
        mToolbar = findViewById(R.id.toolbar);
        textView = findViewById(R.id.tView);
        searchText = findViewById(R.id.search_name);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Check Permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
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
            @Override
            public void DataIsInserted() {
            }
        });



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fetchLastLocation();
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

        //Selection de la catégorie
        btnType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //nameit="";
                mMap.clear(); //Reset des markers
                for (Marker marker : allMarkers){
                    //nameit+=marker.getType()+", "+spinnerType.getSelectedItem().toString()+" : "+marker.getLongitude()+"-"+marker.getLatitude()+"-"+marker.getName();
                    if (marker.getType().equals(spinnerType.getSelectedItem().toString())){ // Vérification du type du marker correspond au choix selectionné
                        displayMarkers(marker);
                    }
                    else if (spinnerType.getSelectedItem().toString().equals("Tous")){ //Exception pour la selection "Tous" car ne correspond pas à un type
                        displayMarkers(marker);
                    }
                }
                //textView.setText(nameit);
                //Toast.makeText(MapsActivity.this,nameit,Toast.LENGTH_SHORT).show();
                fetchLastLocation(); //Recentrer sur la localisation après la selection
            }
        });

        //Ajout d'un marker
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddMarkerActivity.class);
                int val= allMarkers.size()+1;
                i.putExtra("values",val);
                startActivity(i);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cnt=0;
                nameit="";
                for (Marker marker : allMarkers){
                    nameit+=marker.getName().toLowerCase().contains(searchText.getText().toString().toLowerCase()) + " - " + marker.getName().toLowerCase() + "/" + searchText.getText().toString().toLowerCase() + "; ";
                    if (marker.getName().toLowerCase().contains(searchText.getText().toString().toLowerCase())){
                        displayMarkers(marker);
                        cnt+=1;
                    }
                }
                if (cnt == 0){
                    Toast.makeText(MapsActivity.this,"Aucun spot trouvé pour la recherche correspondante",Toast.LENGTH_LONG);
                }else {
                    fetchLastLocation();
                }
                textView.setText(nameit);
            }
        });



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
        if (id == R.id.quit_but) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void fetchLastLocation() {
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),6f));
                }
            }
        });
    }

    //Affichage pour un marker donné
    private void displayMarkers(Marker marker){
        LatLng m1 = new LatLng(Double.parseDouble(marker.getLongitude()),Double.parseDouble(marker.getLatitude()));
        //Affichage de l'icone en fonction de son type
        //nameit+=" ; \n";
        switch (marker.getType()){
            case "Chateau":
                mMap.addMarker(new MarkerOptions().position(m1).title(marker.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.castle)).snippet("Description : \n" + marker.getDescription() + "\n\n"
                        + "Catégorie : \t" + marker.getType()));
                break;
            case "Lac":
                mMap.addMarker(new MarkerOptions().position(m1).title(marker.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.lake)).snippet("Description : \n" + marker.getDescription() + "\n\n"
                        + "Catégorie : \t" + marker.getType()));
                break;
            case "Montagne":
                mMap.addMarker(new MarkerOptions().position(m1).title(marker.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.mountain)).snippet("Description : \n" + marker.getDescription() + "\n\n"
                        + "Catégorie : \t" + marker.getType()));
                break;
            case "Cascade":
                mMap.addMarker(new MarkerOptions().position(m1).title(marker.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.waterfall)).snippet("Description : \n" + marker.getDescription() + "\n\n"
                        + "Catégorie : \t" + marker.getType()));
                break;
            case "Forêt":
                mMap.addMarker(new MarkerOptions().position(m1).title(marker.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.forest)).snippet("Description : \n" + marker.getDescription() + "\n\n"
                        + "Catégorie : \t" + marker.getType()));
                break;
            case "Mer":
                mMap.addMarker(new MarkerOptions().position(m1).title(marker.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.sea)).snippet("Description : \n" + marker.getDescription() + "\n\n"
                        + "Catégorie : \t" + marker.getType()));
                break;
            default:
                break;
        }
    }
}
