package com.ak86.quarantinetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class EditDeleteBlockActivity extends AppCompatActivity {

    private TableLayout tableBlocksList;
    private DatabaseReference blockListReference;
    private ArrayList<String> usersList = new ArrayList<>();
    private Spinner newBlockIC;
    private View editBlock;
    private int NUMBER_OF_VALUES = 100; //num of values in the picker
    private int PICKER_RANGE = 1;
    String[] displayedValues  = new String[NUMBER_OF_VALUES];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_delete_block);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        tableBlocksList = findViewById(R.id.tableBlocksList);
        blockListReference = FirebaseDatabase.getInstance().getReference().child("blocks");
        blockListReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                populateBlocksListInTable(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void intializeTableHeader(){
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
        blockName.setText(" Block Name ");
        blockName.setTextColor(Color.parseColor("white"));
        blockName.setGravity(Gravity.CENTER);
        headerRow.addView(blockName);
        TextView blockDescr = new TextView(getApplicationContext());
        blockDescr.setText(" Description ");
        blockDescr.setTextColor(Color.parseColor("white"));
        blockDescr.setGravity(Gravity.CENTER);
        headerRow.addView(blockDescr);
        TextView blockCapacity = new TextView(getApplicationContext());
        blockCapacity.setText("  Size  ");
        blockCapacity.setTextColor(Color.parseColor("white"));
        blockCapacity.setGravity(Gravity.CENTER);
        headerRow.addView(blockCapacity);
        TextView noOccupied = new TextView(getApplicationContext());
        noOccupied.setText(" Occupied ");
        noOccupied.setTextColor(Color.parseColor("white"));
        noOccupied.setGravity(Gravity.CENTER);
        headerRow.addView(noOccupied);
        TextView blockIC = new TextView(getApplicationContext());
        blockIC.setText("   Block IC   ");
        blockIC.setTextColor(Color.parseColor("white"));
        blockIC.setGravity(Gravity.CENTER);
        headerRow.addView(blockIC);
        TextView action = new TextView(getApplicationContext());
        action.setText("  Actions  ");
        action.setTextColor(Color.parseColor("white"));
        action.setGravity(Gravity.CENTER);
        headerRow.addView(action);
        headerRow.setLayoutParams(lp);
        headerRow.setBackgroundColor(Color.argb(200,63,124,172));
        tableBlocksList.addView(headerRow,lp);

    }

    private void populateBlocksListInTable(final DataSnapshot snapshot){
        tableBlocksList.removeAllViews();
        intializeTableHeader();
        int slNoCounter = 1;
        final TableLayout.LayoutParams lp =
                new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0,5,5,0);
        for(final DataSnapshot blocksSnapshot : snapshot.getChildren()){
            final Block block = blocksSnapshot.getValue(Block.class);
            TableRow tableRow = new TableRow(getApplicationContext());
            /*********************** SERIAL NUMBER ************************/
            TextView textViewSlNo = new TextView(getApplicationContext());
            textViewSlNo.setText(String.valueOf(slNoCounter));
            textViewSlNo.setGravity(Gravity.CENTER);
            slNoCounter++;
            tableRow.addView(textViewSlNo);
            /*********************** BLOCK NAME ************************/
            TextView textViewBlockName = new TextView(getApplicationContext());
            textViewBlockName.setText(block.getBlockName());
            textViewBlockName.setGravity(Gravity.CENTER);
            tableRow.addView(textViewBlockName);
            /*********************** BLOCK DESCR ************************/
            TextView textViewBlockDescr = new TextView(getApplicationContext());
            textViewBlockDescr.setText(block.getBlockDescr());
            textViewBlockDescr.setGravity(Gravity.CENTER);
            tableRow.addView(textViewBlockDescr);
            /*********************** BLOCK SIZE ************************/
            TextView textViewBlockCapacity = new TextView(getApplicationContext());
            textViewBlockCapacity.setText(String.valueOf(block.getBlockCapacity()));
            textViewBlockCapacity.setGravity(Gravity.CENTER);
            tableRow.addView(textViewBlockCapacity);
            /*********************** BLOCK OCCUPIED ************************/
            TextView textViewBlockOccupied = new TextView(getApplicationContext());
            textViewBlockOccupied.setText(String.valueOf(block.getBlockOccupied()));
            textViewBlockOccupied.setGravity(Gravity.CENTER);
            tableRow.addView(textViewBlockOccupied);
            /*********************** BLOCK IN CHARGE ************************/
            TextView textViewBlockIC = new TextView(getApplicationContext());
            textViewBlockIC.setText(block.getBlockInCharge());
            textViewBlockIC.setGravity(Gravity.CENTER);
            tableRow.addView(textViewBlockIC);
            /*********************** SERIAL NUMBER ************************/
            Button actionButton = new Button(getApplicationContext());
            actionButton.setText("Delete");
            actionButton.setTextSize(8);
            actionButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteBlock(block.getBlockName());
                }
            });
            tableRow.addView(actionButton);
            tableRow.setOnLongClickListener(new View.OnLongClickListener() {
                                                @Override
                                                public boolean onLongClick(View v) {
                                                    popupAndEditBlock(block.getBlockName(), snapshot);
                                                    return true;
                                                }
                                            }
            );
            tableRow.setLayoutParams(lp);
            tableRow.setBackgroundColor(Color.argb(100,63,124,172));
            tableBlocksList.addView(tableRow,lp);
        }
    }

    private void popupAndEditBlock(String blockName, DataSnapshot snapshot){
        LayoutInflater li = LayoutInflater.from(EditDeleteBlockActivity.this);
        editBlock = li.inflate(R.layout.edit_block_popup, null);
        TextView newBlockName = editBlock.findViewById(R.id.editTextBlockName);
        TextView newBlockDescr = editBlock.findViewById(R.id.editTextBlockDescr);
        NumberPicker newCapacity = editBlock.findViewById(R.id.numberPickerNewCapacity);
        newBlockIC = editBlock.findViewById(R.id.spinnerNewBlockIC);
        for(int i=0; i<NUMBER_OF_VALUES; i++)
            displayedValues[i] = String.valueOf(PICKER_RANGE * (i+1));
        newCapacity.setMinValue(0);
        newCapacity.setMaxValue(displayedValues.length-1);
        newCapacity.setDisplayedValues(displayedValues);
        DatabaseReference userListDR = FirebaseDatabase.getInstance().getReference();
        userListDR.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot diffUsers : snapshot.getChildren()){
                    User aUser = diffUsers.getValue(User.class);
                    usersList.add(aUser.getEmailId());
                }
                setupSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        for(DataSnapshot eachBlockInList : snapshot.getChildren()){
            Block blockToBeUpdated = eachBlockInList.getValue(Block.class);
                if(blockToBeUpdated.getBlockName().equals(blockName)){
                    newBlockName.setText(blockToBeUpdated.getBlockName());
                    newBlockDescr.setText(blockToBeUpdated.getBlockDescr());
                    newCapacity.setValue(blockToBeUpdated.getBlockCapacity()-1);
                    newBlockIC.setSelection(usersList.indexOf(blockToBeUpdated.getBlockInCharge()));
                    newBlockIC.setSelection(1);
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(EditDeleteBlockActivity.this);
        builder.setView(editBlock);
        builder.setCancelable(false)
                .setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void setupSpinner()
    {
        newBlockIC = editBlock.findViewById(R.id.spinnerNewBlockIC) ;
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,usersList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newBlockIC.setAdapter(arrayAdapter);
    }
    private void deleteBlock(final String blockName){
        android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(this);
        dialog.setMessage(" Confirm Delete? Details of People in Block will also be deleted! ");
        dialog.setTitle("Are you sure?");
        dialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final DatabaseReference deleteBlockDR = FirebaseDatabase.getInstance().getReference()
                                .child("blocks");
                        deleteBlockDR.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot blockSnapshot : snapshot.getChildren()){
                                    Block toBeDeletedBlock = blockSnapshot.getValue(Block.class);
                                    if(toBeDeletedBlock.getBlockName().equals(blockName)){
                                        deleteBlockDR.child(blockName).removeValue();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        final DatabaseReference deleteBlockPeopleDR = FirebaseDatabase.getInstance().getReference();
                        String fullyFormedStringName = blockName.concat("People");
                        deleteBlockPeopleDR.child(fullyFormedStringName).removeValue();
                        String test ="test";
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
        android.app.AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}