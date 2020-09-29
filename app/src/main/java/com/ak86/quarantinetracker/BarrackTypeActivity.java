package com.ak86.quarantinetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BarrackTypeActivity extends AppCompatActivity {

    EditText barrackTypeFd;
    Button btnAdd;
    DatabaseReference writeBarrackTypeDR, listBarrackTypeDR;
    TableLayout barrackTypeTable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barrack_type);
        barrackTypeFd  = findViewById(R.id.editTextBarrackType);
        btnAdd  = findViewById(R.id.btnAddBarrackType);
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
                writeBarrackTypeDR.child("barrackType").child(barrackTypeFd.getText().toString()).setValue(barrackTypeFd.getText().toString().trim());
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
        TextView blockName = new TextView(getApplicationContext());
        blockName.setText(" Barrack Type ");
        blockName.setTextColor(Color.parseColor("white"));
        blockName.setGravity(Gravity.CENTER);
        headerRow.addView(blockName);
        TextView action = new TextView(getApplicationContext());
        action.setText("  Actions  ");
        action.setTextColor(Color.parseColor("white"));
        action.setGravity(Gravity.CENTER);
        headerRow.addView(action);
        headerRow.setLayoutParams(lp);
        headerRow.setBackgroundColor(Color.argb(200,63,124,172));
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
            final String type = (String) snapshot.getValue();
            TableRow tableRow = new TableRow(getApplicationContext());
            /*********************** SERIAL NUMBER ************************/
            TextView textViewSlNo = new TextView(getApplicationContext());
            textViewSlNo.setText(String.valueOf(slNoCounter));
            textViewSlNo.setGravity(Gravity.CENTER);
            slNoCounter++;
            tableRow.addView(textViewSlNo);
            /*********************** BLOCK NAME ************************/
            TextView textViewBlockName = new TextView(getApplicationContext());
            textViewBlockName.setText(type);
            textViewBlockName.setGravity(Gravity.CENTER);
            tableRow.addView(textViewBlockName);
            /***********************  ADD A DELETE BUTTON************************/
            Button actionButton = new Button(getApplicationContext());
            actionButton.setText("Delete");
            actionButton.setTextSize(8);
            actionButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //deleteBlock(type.getBlockName());
                }
            });
            tableRow.addView(actionButton);
            //A long click pops up an edit menu
            tableRow.setLayoutParams(lp);
            tableRow.setBackgroundColor(Color.argb(100,63,124,172));
            barrackTypeTable.addView(tableRow,lp);
        }

    }
}