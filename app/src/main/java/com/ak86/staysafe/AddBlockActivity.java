package com.ak86.staysafe;

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

    private DatabaseReference usersListDR, barrackTypesListDR;
    private EditText fdBlockName;
    private NumberPicker numberPicker;
    private Spinner spinnerUsers, spinnerBarrackType;
    private ProgressBar progressBar;
    private Button button_createNewUser;
    int NUMBER_OF_VALUES = 100; //num of values in the picker
    int PICKER_RANGE = 1;
    String[] displayedValues  = new String[NUMBER_OF_VALUES];
    ArrayList<String> usersList = new ArrayList<>();
    ArrayList<String> barrackTypesList = new ArrayList<>();
    Boolean okFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_block);
        fdBlockName = (EditText) findViewById(R.id.personNameFd);
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
        getSupportActionBar().setTitle("Add New Block");
        usersListDR = FirebaseDatabase.getInstance().getReference();
        usersListDR.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot usersSnapshot : snapshot.getChildren()){
                    User user = usersSnapshot.getValue(User.class);
                    usersList.add(Validator.decodeFromFirebaseKey(user.getEmailId()));
                    //Log.println(Log.ASSERT, "USERS", user.getUsername());
                    //Map<String, Object> td = (HashMap<String, Object>) usersSnapshot.getValue();
                    //List<Object> values = new ArrayList<>(td.values());
                }
                setupUserSpinner();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        barrackTypesListDR = FirebaseDatabase.getInstance().getReference();
        barrackTypesListDR.child("barrackType").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot barrackTypesSnapshot : snapshot.getChildren()){
                    barrackTypesList.add(Validator.decodeFromFirebaseKey(barrackTypesSnapshot.getValue().toString()));
                }
                setupBarrackTypesSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setupUserSpinner()
    {
        spinnerUsers = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,usersList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUsers.setAdapter(arrayAdapter);
    }

    private void setupBarrackTypesSpinner()
    {
        spinnerBarrackType = (Spinner) findViewById(R.id.spinnerBarrackType);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,barrackTypesList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBarrackType.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void createNewBlock(){

         String blockName, blockDescr =null, blockInCharge = null;
         int blockCapacity;

         blockName = Validator.encodeForFirebaseKey(fdBlockName.getText().toString().trim());
         if(spinnerBarrackType !=null && spinnerBarrackType.getSelectedItem() != null){
             blockDescr = spinnerBarrackType.getSelectedItem().toString().trim();
         }
         blockCapacity = numberPicker.getValue() + 1;
         if(spinnerUsers != null && spinnerUsers.getSelectedItem() != null) {
            blockInCharge = spinnerUsers.getSelectedItem().toString().trim();
         }

         if(TextUtils.isEmpty(blockName) || TextUtils.isEmpty(blockDescr) || TextUtils.isEmpty(blockInCharge)){
             Toast.makeText(getApplicationContext(), "Please fill up all details above", Toast.LENGTH_LONG).show();
             okFlag = false;
         } else if(okFlag){
             if (progressBar != null) {
                 progressBar.setVisibility(View.VISIBLE);
             }
             Block newBlock = new Block(blockName, blockDescr, blockInCharge, blockCapacity, 0, new Date(), new Date(), new Date());
             usersListDR.child("blocks").child(blockName).setValue(newBlock)
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
        dialog.setMessage("Successfully created new block : " + (fdBlockName.getText().toString()));
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