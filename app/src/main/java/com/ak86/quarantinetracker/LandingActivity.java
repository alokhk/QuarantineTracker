package com.ak86.quarantinetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LandingActivity extends AppCompatActivity {

    private DatabaseReference blocksDatabaseReference;
    private List<Block> blocksList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter blockAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressBar progressBar4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        progressBar4 = findViewById(R.id.progressBar4);
        recyclerView = findViewById(R.id.recyclerView);
        blocksDatabaseReference = FirebaseDatabase.getInstance().getReference().child("blocks");
        blocksDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot blocksSnapshot : snapshot.getChildren()){
                    Block block = blocksSnapshot.getValue(Block.class);
                    blocksList.add(block);
                    //Log.println(Log.ASSERT,"BLOCKNAME : ", block.getBlockName());
                    layoutManager =  new LinearLayoutManager(getApplicationContext());
                    blockAdapter = new BlockDetailsAdapter(blocksList);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(blockAdapter);
                    if(progressBar4 != null){
                        progressBar4.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.side_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addNewBlock :
                Intent intent = new Intent(getApplicationContext(), AddBlockActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}