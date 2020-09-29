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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LandingActivity extends AppCompatActivity {

    private DatabaseReference blocksDatabaseReference;
    private List<Block> blocksList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter blockAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressBar progressBar4;
    private TextView totalBlocks, overallCapacity, totalQuarantined;
    private int blocksCount, capacityCount, quarantineCount;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        mAuth = FirebaseAuth.getInstance();
        totalBlocks = findViewById(R.id.totalQuarantineBlocksFd);
        overallCapacity = findViewById(R.id.totalCapacityFd);
        totalQuarantined = findViewById(R.id.totalQuarantinedFd);
        progressBar4 = findViewById(R.id.progressBar4);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.requestLayout();
        blocksDatabaseReference = FirebaseDatabase.getInstance().getReference().child("blocks");
        blocksDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                blocksCount = (int)snapshot.getChildrenCount();
                blocksList.clear();
                capacityCount = 0;
                quarantineCount = 0;
                for(DataSnapshot blocksSnapshot : snapshot.getChildren()){
                    Block block = blocksSnapshot.getValue(Block.class);
                    capacityCount = capacityCount + block.getBlockCapacity();
                    quarantineCount = quarantineCount + block.getBlockOccupied();
                    blocksList.add(block);
                    //Log.println(Log.ASSERT,"BLOCKNAME : ", block.getBlockName());
                    layoutManager =  new LinearLayoutManager(getApplicationContext());
                    blockAdapter = new BlockDetailsAdapter(blocksList);
                    recyclerView.removeAllViews();
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(blockAdapter);
                    totalBlocks.setText(String.valueOf(blocksCount));
                    overallCapacity.setText(String.valueOf(capacityCount));
                    totalQuarantined.setText(String.valueOf(quarantineCount));
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
        MenuItem addBlock = menu.findItem(R.id.addNewBlock);
        MenuItem editBlock = menu.findItem(R.id.editBlock);
        MenuItem deleteBlock = menu.findItem(R.id.deleteBlock);
        MenuItem addBarrackType = menu.findItem(R.id.addBarrackType);
        if(Objects.equals(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail(), "admin@39gtc.gr")){
            addBlock.setVisible(true);
            editBlock.setVisible(true);
            deleteBlock.setVisible(true);
            addBarrackType.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addNewBlock :
                Intent intent = new Intent(getApplicationContext(), AddBlockActivity.class);
                startActivity(intent);
                return true;
            case R.id.editBlock :
                Intent editIntent = new Intent(getApplicationContext(), EditDeleteBlockActivity.class);
                startActivity(editIntent);
                return true;
            case R.id.deleteBlock :
                Intent deleteIntent = new Intent(getApplicationContext(),EditDeleteBlockActivity.class);
                startActivity(deleteIntent);
                return true;
            case R.id.addBarrackType :
                Intent barrackTypeIntent = new Intent(getApplicationContext(),BarrackTypeActivity.class);
                startActivity(barrackTypeIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}