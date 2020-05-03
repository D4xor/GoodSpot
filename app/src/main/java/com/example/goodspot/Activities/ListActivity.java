package com.example.goodspot.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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

        //Affichage de la toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        itemsMarks = new ArrayList<>();
        for (Marker marker : MapsActivity.allMarkers){
            //itemsMarks.add(new ItemsMark(marker.getName(),marker.getDescription(),marker.getType(),marker.getPhotolink()));
            String type = marker.getType();
            switch (type){
                case "Chateau":
                    itemsMarks.add(new ItemsMark(marker.getName(),marker.getDescription(),marker.getType(),marker.getPhotolink(),R.drawable.castle));
                    break;
                case "Lac":
                    itemsMarks.add(new ItemsMark(marker.getName(),marker.getDescription(),marker.getType(),marker.getPhotolink(),R.drawable.lake));
                    break;
                case "Montagne":
                    itemsMarks.add(new ItemsMark(marker.getName(),marker.getDescription(),marker.getType(),marker.getPhotolink(),R.drawable.mountain));
                    break;
                case "Cascade":
                    itemsMarks.add(new ItemsMark(marker.getName(),marker.getDescription(),marker.getType(),marker.getPhotolink(),R.drawable.waterfall));
                    break;
                case "Forêt":
                    itemsMarks.add(new ItemsMark(marker.getName(),marker.getDescription(),marker.getType(),marker.getPhotolink(),R.drawable.forest));
                    break;
                case "Mer":
                    itemsMarks.add(new ItemsMark(marker.getName(),marker.getDescription(),marker.getType(),marker.getPhotolink(),R.drawable.sea));
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
}
