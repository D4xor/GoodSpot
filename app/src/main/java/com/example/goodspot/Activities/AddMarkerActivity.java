package com.example.goodspot.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.goodspot.FirebaseDatabaseHelper;
import com.example.goodspot.Model.Marker;
import com.example.goodspot.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

public class AddMarkerActivity extends AppCompatActivity {

    static final int REQUEST_PHOTO = 2;

    private Uri contentUri;

    //Variables
    private String uriUrl;
    private int count;

    //FireBase Storage
    private StorageReference storageReference;

    //Class Marker
    private Marker mk;

    //Layout
    private EditText name;
    private EditText desc;
    private Spinner spinner;
    private ImageButton adds,backs;
    private Button getPic;
    private Toolbar mToolbar;
    private ImageView ivPhoto;
    private LinearLayout infos;

    //Location
    private Location mCurrentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);
        mk = new Marker();


        name =findViewById(R.id.name);
        desc=findViewById(R.id.desc);
        spinner=findViewById(R.id.spinner2);
        adds=findViewById(R.id.addit);
        mToolbar = findViewById(R.id.toolbar);
        ivPhoto = findViewById(R.id.ivPhotoImage);
        infos = findViewById(R.id.infos);
        getPic = findViewById(R.id.getPic);

        storageReference = FirebaseStorage.getInstance().getReference("Photos");


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
                    Intent i = getIntent();
                    count = i.getIntExtra("values",0);
                    uploadFile();
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

        getPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_PHOTO && resultCode == Activity.RESULT_OK && data != null && data.getData() != null){
            contentUri = data.getData();
            ivPhoto.setImageURI(contentUri);
            infos.setVisibility(View.VISIBLE);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Selection de l'image dans le téléphone
    private void chooseFile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,REQUEST_PHOTO);
    }

    //Otenir l'extension de l'image
    private String getFileExtension(Uri tUri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(tUri));
    }

    //Upload le fichier sur Firebase Storage + Upload dans FirebaseStorage
    private void uploadFile(){
        if(contentUri != null){
            final StorageReference fileRef = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(contentUri));
            fileRef.putFile(contentUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                uriUrl = uri.toString();
                                mk.setName(name.getText().toString());
                                mk.setDescription(desc.getText().toString());
                                mk.setType(spinner.getSelectedItem().toString());
                                mk.setLongitude(String.valueOf(mCurrentLocation.getLatitude()));
                                mk.setLatitude(String.valueOf(mCurrentLocation.getLongitude()));
                                mk.setPhotolink(uriUrl+".jpg");
                                //Ajout de l'objet Marker
                                new FirebaseDatabaseHelper().addMarkers(mk,count,new FirebaseDatabaseHelper.DataStatus() {
                                    @Override
                                    public void DataIsLoaded(List<Marker> markers, List<String> keys) {
                                    }
                                    @Override
                                    public void DataIsInserted() {
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }
}
