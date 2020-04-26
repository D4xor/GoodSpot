package com.example.goodspot.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.goodspot.ListAdapter;
import com.example.goodspot.Model.ItemsMark;
import com.example.goodspot.Model.Marker;
import com.example.goodspot.R;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ItemsMark> itemsMarks;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        itemsMarks = new ArrayList<>();
        for (Marker marker : MapsActivity.allMarkers){
            String type = marker.getType();
            switch (type){
                case "Chateau":
                    itemsMarks.add(new ItemsMark(marker.getName(),marker.getDescription(),marker.getType(),R.drawable.castle));
                    break;
                case "Lac":
                    itemsMarks.add(new ItemsMark(marker.getName(),marker.getDescription(),marker.getType(),R.drawable.lake));
                    break;
                case "Montagne":
                    itemsMarks.add(new ItemsMark(marker.getName(),marker.getDescription(),marker.getType(),R.drawable.mountain));
                    break;
                case "Cascade":
                    itemsMarks.add(new ItemsMark(marker.getName(),marker.getDescription(),marker.getType(),R.drawable.waterfall));
                    break;
                case "Forêt":
                    itemsMarks.add(new ItemsMark(marker.getName(),marker.getDescription(),marker.getType(),R.drawable.forest));
                    break;
                case "Mer":
                    itemsMarks.add(new ItemsMark(marker.getName(),marker.getDescription(),marker.getType(),R.drawable.sea));
                    break;
                default:
                    break;
            }
        }

        recyclerView = findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new ListAdapter(itemsMarks);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
