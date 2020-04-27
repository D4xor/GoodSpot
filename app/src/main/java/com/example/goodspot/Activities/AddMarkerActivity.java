package com.example.goodspot.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.goodspot.FirebaseDatabaseHelper;
import com.example.goodspot.Model.Marker;
import com.example.goodspot.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class AddMarkerActivity extends AppCompatActivity {

    //Layout
    private EditText name;
    private EditText desc;
    private Spinner spinner;
    private ImageButton adds,backs;
    private Toolbar mToolbar;

    //Location
    private Location mCurrentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);

        name =findViewById(R.id.name);
        desc=findViewById(R.id.desc);
        spinner=findViewById(R.id.spinner2);
        adds=findViewById(R.id.addit);
        backs=findViewById(R.id.backToIt);
        mToolbar = findViewById(R.id.toolbar);


        //Affichage Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();


        //Ajout du marker - Envoie à la base de donnée
        adds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!name.getText().toString().equals("") && !desc.getText().toString().equals("")){
                    Marker mk = new Marker();
                    mk.setName(name.getText().toString());
                    mk.setDescription(desc.getText().toString());
                    mk.setType(spinner.getSelectedItem().toString());
                    mk.setLongitude(String.valueOf(mCurrentLocation.getLatitude()));
                    mk.setLatitude(String.valueOf(mCurrentLocation.getLongitude()));
                    mk.setPhotolink("Unknown");
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
                }else if (name.getText().toString().equals("") && desc.getText().toString().equals("")){
                    Toast.makeText(AddMarkerActivity.this,"Champ \"nom\" et \"description\"  sont vides, veuillez les remplir ",Toast.LENGTH_LONG).show();
                }else if(name.getText().toString().equals("")){
                    Toast.makeText(AddMarkerActivity.this,"Champ \"nom\" est vide, veuillez le remplir ",Toast.LENGTH_LONG).show();
                }else if(desc.getText().toString().equals("")){
                    Toast.makeText(AddMarkerActivity.this,"Champ \"description\" est vide, veuillez le remplir ",Toast.LENGTH_LONG).show();
                }

            }
        });

        //Bouton de retour à la "MainActivity"
        backs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

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
                    mCurrentLocation = location;
                }
            }
        });
    }
}
