package com.ak86.staysafe;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
/*********************************This is an Admin function. Once blocks are created, they can be deleted or edited*******************************************************************************************************/
public class ManageBlockActivity extends AppCompatActivity {

    private TableLayout tableBlocksList;
    private DatabaseReference blockListReference;
    private ArrayList<String> usersList = new ArrayList<>();
    private ArrayList<String> barrackTypeList = new ArrayList<>();
    private Spinner newBlockIC, newBlockDescr;
    private View editBlock;
    private ProgressBar progressBar;
    private int NUMBER_OF_VALUES = 100; //num of values in the picker
    private int PICKER_RANGE = 1;
    String[] displayedValues  = new String[NUMBER_OF_VALUES];
    private Block blockToBeUpdated;
/*****************On Create, read list of block and display them on the screen.*************************************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_block);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Manage Blocks");
        Objects.requireNonNull(getSupportActionBar()).setSubtitle(Html.fromHtml("<small>Long Press to Edit</small>"));
        tableBlocksList = findViewById(R.id.tableBlocksList);
        progressBar = findViewById(R.id.progressBarManageBlock);
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
/****************************init the headers programatically*************************************************************************************/
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
/***************Now fill up the table**************************************************************************************************/
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
            textViewBlockName.setText(Validator.decodeFromFirebaseKey(block.getBlockName()));
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
            /***********************  ADD A DELETE BUTTON************************/
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
            //A long click pops up an edit menu
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
            progressBar.setVisibility(View.GONE);
            tableBlocksList.addView(tableRow,lp);
        }
    }
/****************long press popup to edit*************************************************************************************************/
    private void popupAndEditBlock(final String blockName, DataSnapshot snapshot){
        /*********************populate with existing data. read from cloud and fill up********************************************************************************************/
        LayoutInflater li = LayoutInflater.from(ManageBlockActivity.this);
        editBlock = li.inflate(R.layout.edit_block_popup, null);
        final TextView newBlockName = editBlock.findViewById(R.id.editTextBlockName);
        final NumberPicker newCapacity = editBlock.findViewById(R.id.numberPickerNewCapacity);
        newBlockDescr = editBlock.findViewById(R.id.spinnerBlockDescr);
        newBlockIC = editBlock.findViewById(R.id.spinnerNewBlockIC);
        for(int i=0; i<NUMBER_OF_VALUES; i++)
            displayedValues[i] = String.valueOf(PICKER_RANGE * (i+1));
        newCapacity.setMinValue(0);
        newCapacity.setMaxValue(displayedValues.length-1);
        newCapacity.setDisplayedValues(displayedValues);
        final DatabaseReference userListDR = FirebaseDatabase.getInstance().getReference();
        userListDR.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot diffUsers : snapshot.getChildren()){
                    User aUser = diffUsers.getValue(User.class);
                    usersList.add(Validator.decodeFromFirebaseKey(aUser.getEmailId()));
                }
                setupUserSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference barrackTypeDR = FirebaseDatabase.getInstance().getReference();
        barrackTypeDR.child("barrackType").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                barrackTypeList.clear();
                for (DataSnapshot diffBarracktypes : snapshot.getChildren()){
                    barrackTypeList.add(Validator.decodeFromFirebaseKey(diffBarracktypes.getValue().toString()));
                }
                setupBarrackTypeSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        for(DataSnapshot eachBlockInList : snapshot.getChildren()){
            blockToBeUpdated = eachBlockInList.getValue(Block.class);
                if(blockToBeUpdated.getBlockName().equals(blockName)){
                    newBlockName.setText(Validator.decodeFromFirebaseKey(blockToBeUpdated.getBlockName()));
                    newBlockDescr.setSelection(barrackTypeList.indexOf(blockToBeUpdated.getBlockDescr()));
                    newCapacity.setValue(blockToBeUpdated.getBlockCapacity()-1);
                    newBlockIC.setSelection(usersList.indexOf(blockToBeUpdated.getBlockInCharge()));
            }
        }

        /******When user changes the data existing, then first look at the changes. Then move the people data to ***********************************************************************************************************/
        /******a corresponding node. mov record function, then create a new block with new data, copy details from existing***********************************************************************************************************/
        /******lastly delete the old copies***********************************************************************************************************/
        AlertDialog.Builder builder = new AlertDialog.Builder(ManageBlockActivity.this);
        builder.setView(editBlock);
        builder.setCancelable(false)
                .setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(TextUtils.isEmpty(newBlockDescr.getSelectedItem().toString()) || TextUtils.isEmpty(newBlockIC.getSelectedItem().toString())){
                                    Toast.makeText(getApplicationContext(), "Please wait till list of barrack types and users load", Toast.LENGTH_LONG).show();
                                } else {
                                    final DatabaseReference updateBlockDR = FirebaseDatabase.getInstance().getReference();
                                    if(!blockName.equals(newBlockName.getText().toString())){
                                        moveRecord(updateBlockDR.child(blockName.concat("People")),updateBlockDR.child(newBlockName.getText().toString().concat("People")));
                                        updateBlockDR.child(blockName.concat("People")).removeValue();
                                    }
                                    Block updatedBlock = new Block();
                                    updatedBlock.setBlockName(Validator.encodeForFirebaseKey(newBlockName.getText().toString()));
                                    updatedBlock.setBlockDescr(newBlockDescr.getSelectedItem().toString());
                                    updatedBlock.setBlockCapacity(newCapacity.getValue()+1);
                                    updatedBlock.setBlockInCharge(newBlockIC.getSelectedItem().toString());
                                    updatedBlock.setBlockOccupied(blockToBeUpdated.getBlockOccupied());
                                    updatedBlock.setQuarantineStartDate(blockToBeUpdated.getQuarantineStartDate());
                                    updatedBlock.setMedicalDate(blockToBeUpdated.getMedicalDate());
                                    updatedBlock.setQuarantineEndDate(blockToBeUpdated.getQuarantineEndDate());
                                    updateBlockDR.child("blocks").child(updatedBlock.getBlockName()).setValue(updatedBlock);
                                    if(!blockName.equals(newBlockName.getText().toString())) {
                                        updateBlockDR.child("blocks").child(blockName).removeValue();
                                    }
                                }

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

    private void setupUserSpinner()
    {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,usersList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newBlockIC.setAdapter(arrayAdapter);
    }

    private void setupBarrackTypeSpinner()
    {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,barrackTypeList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newBlockDescr.setAdapter(arrayAdapter);
    }
/************Delete the selected block. After deleting the block, delete corresponding people node also*****************************************************************************************************/
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
/************people node is a top level node, as in under "/", so use a func to move record keeping data same *****************************************************************************************************/
/************to a new node with updated Key as in /CLK -> /CLH*****************************************************************************************************/
    private void moveRecord(final DatabaseReference fromPath, final DatabaseReference toPath) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            System.out.println("Copy failed");
                        } else {
                            System.out.println("Success");

                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.block_menu,menu);
        MenuItem addBlock = menu.findItem(R.id.addNewBlock);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.addNewBlock :
                Intent intent = new Intent(getApplicationContext(), AddBlockActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}