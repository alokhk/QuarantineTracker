package com.ak86.quarantinetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BlockDetailActivity extends AppCompatActivity {

    private DatabaseReference blocksDatabaseReference;
    private TextView blockIC, blockCapacity, blockOccupied;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("selectedBlockName"));
        blockIC =  findViewById(R.id.detailBlockICNameFd);
        blockCapacity = findViewById(R.id.detailBlockCapacityFd);
        blockOccupied = findViewById(R.id.detailBlockOccupiedFd);
        blocksDatabaseReference = FirebaseDatabase.getInstance().getReference().child("blocks").child(getIntent().getStringExtra("selectedBlockName"));
        blocksDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                blockIC.setText(snapshot.getValue(Block.class).getBlockInCharge());
                blockCapacity.setText(String.valueOf(snapshot.getValue(Block.class).getBlockCapacity()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}