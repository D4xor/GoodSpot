package com.example.goodspot;

import androidx.annotation.NonNull;

import com.example.goodspot.Model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private List<Marker> markers = new ArrayList<>();

    public FirebaseDatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("marker");
    }

    public interface DataStatus{
        void DataIsLoaded(List<Marker> markers, List<String> keys);
        void DataIsInserted();
        /*
        void DataIsUpdated();
        void DataIsDeleted();*/
    }

    public void readMarkers(final DataStatus dataStatus){
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                markers.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Marker marker = keyNode.getValue(Marker.class);
                    markers.add(marker);
                }
                dataStatus.DataIsLoaded(markers,keys);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void addMarkers(Marker marker,int count, final DataStatus dataStatus){
        String key = String.valueOf(count);
        mReference.child(key).setValue(marker).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dataStatus.DataIsInserted();
            }
        });
    }

}
