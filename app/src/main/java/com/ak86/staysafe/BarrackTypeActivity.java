package com.ak86.staysafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class BarrackTypeActivity extends AppCompatActivity {

    private EditText barrackTypeFd;
    private Button btnAdd;
    private DatabaseReference writeBarrackTypeDR, listBarrackTypeDR;
    private TableLayout barrackTypeTable;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barrack_type);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Manage Barrack Types");
        barrackTypeFd  = findViewById(R.id.editTextBarrackType);
        btnAdd  = findViewById(R.id.btnAddBarrackType);
        progressBar = findViewById(R.id.progressBarBarrackType);
        barrackTypeTable = findViewById(R.id.barrackTypeTable);
        listBarrackTypeDR = FirebaseDatabase.getInstance().getReference();
        listBarrackTypeDR.child("barrackType").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listAllBarrackTypesInTable(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeBarrackTypeDR = FirebaseDatabase.getInstance().getReference();
                writeBarrackTypeDR.child("barrackType").child(Validator.encodeForFirebaseKey(barrackTypeFd.getText().toString())).setValue(Validator.encodeForFirebaseKey(barrackTypeFd.getText().toString().trim()));
                barrackTypeFd.setText("");
            }
        });
    }

    private void initializeTableHeader(){
        final TableLayout.LayoutParams lp =
                new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0,5,5,0);
        TableRow headerRow = new TableRow(getApplicationContext());
        TextView slno = new TextView(getApplicationContext());
        slno.setText("  S.No  ");
        slno.setTextColor(Color.parseColor("white"));
        slno.setGravity(Gravity.CENTER);
        headerRow.addView(slno);
        TextView barrackType = new TextView(getApplicationContext());
        barrackType.setText(" Barrack Type ");
        barrackType.setTextColor(Color.parseColor("white"));
        barrackType.setGravity(Gravity.CENTER);
        headerRow.addView(barrackType);
        TextView action = new TextView(getApplicationContext());
        action.setText("  Actions  ");
        action.setTextColor(Color.parseColor("white"));
        action.setGravity(Gravity.CENTER);
        headerRow.addView(action);
        headerRow.setLayoutParams(lp);
        headerRow.setBackgroundColor(Color.argb(200,33,150,243));
        barrackTypeTable.addView(headerRow,lp);

    }

    private void listAllBarrackTypesInTable(DataSnapshot barrackTypesSnapshot){
        barrackTypeTable.removeAllViews();
        initializeTableHeader();
        int slNoCounter = 1;
        final TableLayout.LayoutParams lp =
                new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0,5,5,0);
        for(final DataSnapshot snapshot : barrackTypesSnapshot.getChildren()){
            final String type =  Validator.decodeFromFirebaseKey(snapshot.getValue().toString());
            TableRow tableRow = new TableRow(getApplicationContext());
            /*********************** SERIAL NUMBER ************************/
            TextView textViewSlNo = new TextView(getApplicationContext());
            textViewSlNo.setText(String.valueOf(slNoCounter));
            textViewSlNo.setTextColor(Color.argb(255,33,150,243));
            textViewSlNo.setGravity(Gravity.CENTER);
            slNoCounter++;
            tableRow.addView(textViewSlNo);
            /*********************** BARRACK TYPE ************************/
            TextView textViewBarrackType = new TextView(getApplicationContext());
            textViewBarrackType.setText(type);
            textViewBarrackType.setTextColor(Color.argb(255,33,150,243));
            textViewBarrackType.setGravity(Gravity.CENTER);
            tableRow.addView(textViewBarrackType);
            /***********************  ADD A DELETE BUTTON************************/
            Button actionButton = new Button(getApplicationContext());
            actionButton.setText("Delete");
            actionButton.setTextSize(8);
            actionButton.setElevation(1);
            actionButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            actionButton.setTextColor(Color.argb(255,33,150,243));
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //deleteBlock(type.getBlockName());
                }
            });
            tableRow.addView(actionButton);
            //A long click pops up an edit menu
            tableRow.setLayoutParams(lp);
            tableRow.setBackgroundColor(Color.argb(100,236,235,232));
            progressBar.setVisibility(View.GONE);
            barrackTypeTable.addView(tableRow,lp);
        }

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}