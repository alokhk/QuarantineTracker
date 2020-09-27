package com.ak86.quarantinetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class AddBlockActivity extends AppCompatActivity {

    private DatabaseReference  databaseReference;
    private EditText fdBlockName, fdBlockDescr;
    private NumberPicker numberPicker;
    private Spinner spinner;
    private ProgressBar progressBar;
    private Button button_createNewUser;
    int NUMBER_OF_VALUES = 100; //num of values in the picker
    int PICKER_RANGE = 1;
    String[] displayedValues  = new String[NUMBER_OF_VALUES];
    ArrayList<String> usersList = new ArrayList<>();
    Boolean okFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_block);
        fdBlockName = (EditText) findViewById(R.id.personNameFd);
        fdBlockDescr = (EditText) findViewById(R.id.blockDescr);
        numberPicker = findViewById(R.id.numberPicker);
        progressBar = findViewById(R.id.progressBar2);
        button_createNewUser = findViewById(R.id.btnCreateNewBlock);
        button_createNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewBlock();
            }
        });
        for(int i=0; i<NUMBER_OF_VALUES; i++)
            displayedValues[i] = String.valueOf(PICKER_RANGE * (i+1));
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(displayedValues.length-1);
        numberPicker.setDisplayedValues(displayedValues);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot usersSnapshot : snapshot.child("users").getChildren()){
                    User user = usersSnapshot.getValue(User.class);
                    usersList.add(user.getEmailId());
                    //Log.println(Log.ASSERT, "USERS", user.getUsername());
                    //Map<String, Object> td = (HashMap<String, Object>) usersSnapshot.getValue();
                    //List<Object> values = new ArrayList<>(td.values());
                }
                setupSpinner();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setupSpinner()
    {
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,usersList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void createNewBlock(){

         String blockName, blockDescr, blockInCharge = null;
         int blockCapacity;

         blockName = fdBlockName.getText().toString().trim();
         blockDescr = fdBlockDescr.getText().toString().trim();
         blockCapacity = numberPicker.getValue() + 1;
         if(spinner != null && spinner.getSelectedItem() != null) {
            blockInCharge = spinner.getSelectedItem().toString();
         }

         if(TextUtils.isEmpty(blockName) || TextUtils.isEmpty(blockDescr) || TextUtils.isEmpty(blockInCharge)){
             Toast.makeText(getApplicationContext(), "Please fill up all details above", Toast.LENGTH_LONG).show();
             okFlag = false;
         } else if(okFlag){
             if (progressBar != null) {
                 progressBar.setVisibility(View.VISIBLE);
             }
             Block newBlock = new Block(blockName, blockDescr, blockInCharge, blockCapacity, 0,Date.from(Instant.EPOCH), Date.from(Instant.EPOCH));
             databaseReference.child("blocks").child(blockName).setValue(newBlock)
                     .addOnSuccessListener(new OnSuccessListener<Void>(){
                 @Override
                 public void onSuccess(Void aVoid) {
                     Log.println(Log.ASSERT,"Write", "Success");
                     progressBar.setVisibility(View.GONE);
                     alertDialog();
                 }
             });
         }
        //databaseReference.child("blocks").child(blockName).
         Log.println(Log.ASSERT,"TEST", blockName + blockDescr+blockCapacity+ blockInCharge);
    }

    private void alertDialog(){
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage("Successfully created new block : " + fdBlockName.getText().toString());
        dialog.setTitle("Success");
        dialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

    }
}