package com.example.goodspot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;

public class AddMarker extends AppCompatActivity {

    //Layout
    private EditText name;
    private EditText desc;
    private Spinner spinner;

    Marker mark;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);

        name =(EditText)findViewById(R.id.name);
        desc=(EditText)findViewById(R.id.desc);
        spinner=(Spinner)findViewById(R.id.spinner2);

        //Lecture des éléments de la base de données
        new FirebaseDatabaseHelper().addMarkers(mark, new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Marker> markers, List<String> keys) {

            }
            @Override
            public void DataIsInserted() {

            }
        });
    }

}
