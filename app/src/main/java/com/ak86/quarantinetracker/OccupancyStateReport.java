package com.ak86.quarantinetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;

public class OccupancyStateReport extends AppCompatActivity {

    private TableLayout reportTable;
    private File imagePath;
    private int slNoCounter = 1;
    final TableLayout.LayoutParams lp =
            new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occupancy_state_report);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        reportTable = findViewById(R.id.reportTable);
        initializeTable();
        readDataFromFirebase();
    }

    private void initializeTable() {
        lp.setMargins(0, 5, 5, 0);
        TableRow headerRow = new TableRow(getApplicationContext());
        TextView slno = new TextView(getApplicationContext());
        slno.setText(" S.No ");
        slno.setTextColor(Color.parseColor("white"));
        slno.setGravity(Gravity.CENTER);
        headerRow.addView(slno);
        TextView blockName = new TextView(getApplicationContext());
        blockName.setText("Name of Quarantine Bks");
        blockName.setTextColor(Color.parseColor("white"));
        blockName.setGravity(Gravity.CENTER);
        headerRow.addView(blockName);
        TextView occupancy = new TextView(getApplicationContext());
        occupancy.setText("Occupancy State");
        occupancy.setTextColor(Color.parseColor("white"));
        occupancy.setGravity(Gravity.CENTER);
        headerRow.addView(occupancy);
        TextView startFrom = new TextView(getApplicationContext());
        startFrom.setText("Quarantine\n Commenced From");
        startFrom.setTextColor(Color.parseColor("white"));
        startFrom.setGravity(Gravity.CENTER);
        headerRow.addView(startFrom);
        TextView endOn = new TextView(getApplicationContext());
        endOn.setText("Quarantine\n Ending On");
        endOn.setTextColor(Color.parseColor("white"));
        endOn.setGravity(Gravity.CENTER);
        headerRow.addView(endOn);
        TextView medExam = new TextView(getApplicationContext());
        medExam.setText("Date of Med Exam\n By MO");
        medExam.setTextColor(Color.parseColor("white"));
        medExam.setGravity(Gravity.CENTER);
        headerRow.addView(medExam);
        TextView remarks = new TextView(getApplicationContext());
        remarks.setText("Remarks");
        remarks.setTextColor(Color.parseColor("white"));
        remarks.setGravity(Gravity.CENTER);
        headerRow.addView(remarks);
        headerRow.setBackgroundColor(Color.argb(200, 33, 150, 243));
        headerRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        reportTable.addView(headerRow, lp);
    }

    private void readDataFromFirebase(){
        DatabaseReference reportDR = FirebaseDatabase.getInstance().getReference();
        reportDR.child("blocks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot blocks : snapshot.getChildren()){
                    Block blockInfo = blocks.getValue(Block.class);
                    fillDataInTable(blockInfo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void fillDataInTable(Block block){

        final TableRow tableRow = new TableRow(getApplicationContext());

        TextView textViewSlNo = new TextView(getApplicationContext());
        textViewSlNo.setText(String.valueOf(slNoCounter));
        textViewSlNo.setTextColor(Color.argb(255,33,150,243));
        textViewSlNo.setGravity(Gravity.CENTER);
        slNoCounter++;
        tableRow.addView(textViewSlNo);

        TextView blockName = new TextView(getApplicationContext());
        blockName.setText(Validator.decodeFromFirebaseKey(block.getBlockName()));
        blockName.setTextColor(Color.argb(255,33,150,243));
        blockName.setGravity(Gravity.CENTER);
        tableRow.addView(blockName);

        TextView occupancy = new TextView(getApplicationContext());
        occupancy.setText(String.valueOf(block.getBlockOccupied()));
        occupancy.setTextColor(Color.argb(255,33,150,243));
        occupancy.setGravity(Gravity.CENTER);
        tableRow.addView(occupancy);

        TextView startDate = new TextView(getApplicationContext());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        startDate.setText(sdf.format(block.getQuarantineStartDate()));
        startDate.setTextColor(Color.argb(255,33,150,243));
        startDate.setGravity(Gravity.CENTER);
        tableRow.addView(startDate);

        TextView endDate = new TextView(getApplicationContext());
        endDate.setText(sdf.format(block.getQuarantineEndDate()));
        endDate.setTextColor(Color.argb(255,33,150,243));
        endDate.setGravity(Gravity.CENTER);
        tableRow.addView(endDate);

        TextView medDate = new TextView(getApplicationContext());
        medDate.setText(sdf.format(block.getMedicalDate()));
        medDate.setTextColor(Color.argb(255,33,150,243));
        medDate.setGravity(Gravity.CENTER);
        tableRow.addView(medDate);

        tableRow.setMinimumHeight(50);
        tableRow.setElevation(4);
        tableRow.setBackgroundColor(Color.argb(100,236,235,232));
        tableRow.setLayoutParams(lp);
        reportTable.addView(tableRow, lp);
    }


}