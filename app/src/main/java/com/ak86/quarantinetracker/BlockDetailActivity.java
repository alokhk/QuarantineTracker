package com.ak86.quarantinetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/*
This is the BlockDetailActivity. This screen shows the content once you click on a Block Card on the Landing Screen.
At the very top, it shows BlockIC, the Block Capacity and current occupation.
It has an overflow menu to add a new person to the block.
once a new person is added, it autoupdates the same screen to show the current residents of the block and their details.
 */
public class BlockDetailActivity extends AppCompatActivity {

    private DatabaseReference listOfBlock_DR, peopleInQuarantineBlock_DR, blockUpdateDR, deletePersonDR;
    private FirebaseAuth mAuth;
    private TextView blockIC, blockCapacity, blockOccupied, textViewPersonName;
    private Button addButton, medDateButton;
    private List<Person> listPersons = new ArrayList<>();
    private String personName, currentUser, blockOwner;
    private DatePicker datePickerStartDate, datePickerMedDate;
    private Date startDate, medDate;
    private TableLayout tableLayout;
    private List<Date> inDates = new ArrayList<>();
    private int blockOccupiedForLandingPage;
    private Date endDateForLandingPage, medDateForLandingPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_detail);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Validator.decodeFromFirebaseKey(getIntent().getStringExtra("selectedBlockName")));
        blockIC =  findViewById(R.id.detailBlockICNameFd);
        blockCapacity = findViewById(R.id.detailBlockCapacityFd);
        blockOccupied = findViewById(R.id.detailBlockOccupiedFd);
        addButton = findViewById(R.id.btnAddPerson);
        medDateButton = findViewById(R.id.btnMedDt);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getEmail();
        tableLayout = findViewById(R.id.personDetailTable);
        final TableLayout.LayoutParams lp =
                new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0,5,5,0);
        lp.setMarginEnd(5);        //Get a reference to the Selected block name from the previous screen. Eg if the user click on CLH
        listOfBlock_DR = FirebaseDatabase.getInstance().getReference().child("blocks").child(Objects.requireNonNull(getIntent().getStringExtra("selectedBlockName")));
        //Take a snapshot and extract the block IC and the block capcity. Data is not much, because this node holds
        //only the block definition. People in the block are not stored in this node. CLH, CNT etc. ONLY DEFINITION
        listOfBlock_DR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Block block = snapshot.getValue(Block.class);
                blockOwner = Validator.decodeFromFirebaseKey(block.getBlockInCharge());
                blockIC.setText(Validator.decodeFromFirebaseKey(block.getBlockInCharge()));
                blockCapacity.setText(String.valueOf(block.getBlockCapacity()));
                if(!currentUser.equals(Validator.decodeFromFirebaseKey(block.getBlockInCharge()))){
                    addButton.setVisibility(View.GONE);
                    medDateButton.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Here we are listing the people in a particular block. Ex CLHPeople, CNT Barrack People etc
        //This a reference to the node of all the peoples in a block
        peopleInQuarantineBlock_DR = FirebaseDatabase.getInstance().getReference()
                .child(getIntent().getStringExtra("selectedBlockName")+"People");
        //Then attach a Value Event Listner. This is because we want the list/table to autoupdate everytime the data changes.
        peopleInQuarantineBlock_DR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                //Set the occupation status of the Block
                blockOccupied.setText(String.valueOf(snapshot.getChildrenCount()));
                blockOccupiedForLandingPage = (int)snapshot.getChildrenCount();
/******************************************CREATE TABLE HEADER******************************************************************/
                int slNoCounter = 1;
                tableLayout.removeAllViews();
                TableRow headerRow = new TableRow(getApplicationContext());
                TextView slno = new TextView(getApplicationContext());
                slno.setText("S.No");
                slno.setTextColor(Color.parseColor("white"));
                slno.setGravity(Gravity.CENTER);
                headerRow.addView(slno);
                TextView name = new TextView(getApplicationContext());
                name.setText("    Name    ");
                name.setTextColor(Color.parseColor("white"));
                name.setGravity(Gravity.CENTER);
                headerRow.addView(name);
                TextView startDate = new TextView(getApplicationContext());
                startDate.setText("  Start Date  ");
                startDate.setTextColor(Color.parseColor("white"));
                startDate.setGravity(Gravity.CENTER);
                headerRow.addView(startDate);
                TextView endDate = new TextView(getApplicationContext());
                endDate.setText("  End Date  ");
                endDate.setTextColor(Color.parseColor("white"));
                endDate.setGravity(Gravity.CENTER);
                headerRow.addView(endDate);
                TextView medDate = new TextView(getApplicationContext());
                medDate.setText("  Med Date  ");
                medDate.setTextColor(Color.parseColor("white"));
                medDate.setGravity(Gravity.CENTER);
                headerRow.addView(medDate);
                TextView coronaStatus = new TextView(getApplicationContext());
                coronaStatus.setText("Corona\nPositive");
                coronaStatus.setTextColor(Color.parseColor("white"));
                coronaStatus.setGravity(Gravity.CENTER);
                headerRow.addView(coronaStatus);
                if(currentUser.equals(blockOwner)){
                    TextView actions = new TextView(getApplicationContext());
                    actions.setText("   Actions  ");
                    actions.setTextColor(Color.parseColor("white"));
                    actions.setGravity(Gravity.CENTER);
                    headerRow.addView(actions);
                }
                headerRow.setBackgroundColor(Color.argb(200,33,150,243));
                tableLayout.addView(headerRow);
/****************************************CREATE TABLE HEADER ENDS******************************************************************/


/****************************************CREATE TABLE CONTENTS******************************************************************/
                //Everytime data changes, this is called. i.e. everytime data changes, take a snapshot
                for(DataSnapshot personSnapshot : snapshot.getChildren()){
                    //Extract the person
                    final Person person = personSnapshot.getValue(Person.class);
                    //create a new row in the table,
                    final TableRow tableRow = new TableRow(getApplicationContext());
                    //create a textview in the row for say Sl No
                    TextView textViewSlNo = new TextView(getApplicationContext());
                    //set the value of the sl no. here directly, in the subsequent columns, extract from person object
                    textViewSlNo.setText(String.valueOf(slNoCounter));
                    textViewSlNo.setTextColor(Color.argb(255,33,150,243));
                    //centre align
                    textViewSlNo.setGravity(Gravity.CENTER);
                    slNoCounter++;
                    //add the textbox to the row.
                    tableRow.addView(textViewSlNo);
                    TextView textViewName = new TextView(getApplicationContext());
                    textViewName.setText(Validator.decodeFromFirebaseKey(person.getName()));
                    textViewName.setTextColor(Color.argb(255,33,150,243));
                    textViewName.setGravity(Gravity.CENTER);
                    textViewName.setWidth((Resources.getSystem().getDisplayMetrics().widthPixels)/2);
                    tableRow.addView(textViewName);
                    TextView textViewStartDate = new TextView(getApplicationContext());
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
                    textViewStartDate.setText(sdf.format(person.getStartDate()));
                    textViewStartDate.setGravity(Gravity.CENTER);
                    textViewStartDate.setTextColor(Color.argb(255,33,150,243));
                    tableRow.addView(textViewStartDate);
                    TextView textViewEndDate = new TextView(getApplicationContext());
                    textViewEndDate.setText(sdf.format(person.getEndDate()));
                    textViewEndDate.setGravity(Gravity.CENTER);
                    textViewEndDate.setTextColor(Color.argb(255,33,150,243));
                    tableRow.addView(textViewEndDate);
                    TextView textViewMedDate = new TextView(getApplicationContext());
                    textViewMedDate.setText(sdf.format(person.getMedicalDate()));
                    textViewMedDate.setGravity(Gravity.CENTER);
                    textViewMedDate.setTextColor(Color.argb(255,33,150,243));
                    tableRow.addView(textViewMedDate);
                    final CheckBox coronaPositive = new CheckBox(getApplicationContext());
                    coronaPositive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            android.app.AlertDialog.Builder dialog=new android.app.AlertDialog.Builder(BlockDetailActivity.this);
                            dialog.setMessage("Are you sure you want to mark "+Validator.decodeFromFirebaseKey(person.getName())+ " as Corona Positive?");
                            dialog.setTitle("Alert!");
                            dialog.setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            DatabaseReference coronaPersonDR = FirebaseDatabase.getInstance().getReference();
                                            coronaPersonDR.child(getIntent().getStringExtra("selectedBlockName")+"People")
                                                    .child(person.getName()).child("coronaPositive").setValue(true);
                                            coronaPositive.setEnabled(false);
                                            manageCoronaPositivePerson(person.getName());
                                        }
                                    });
                            android.app.AlertDialog alertDialog = dialog.create();
                            alertDialog.show();
                        }
                    });
                    tableRow.setGravity(Gravity.CENTER_HORIZONTAL);
                    if (person.isCoronaPositive()){
                        coronaPositive.setEnabled(false);
                        tableRow.setBackgroundColor(Color.argb(100,255 ,55,3));
                    } else {
                        tableRow.setBackgroundColor(Color.argb(100,236,235,232));

                    }
                    tableRow.addView(coronaPositive);
                    if(blockOwner.equals(currentUser)){
                        Button actionButton = new Button(getApplicationContext());
                        actionButton.setText("Delete");
                        actionButton.setTextSize(8);
                        actionButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        actionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deletePerson(person.getName());
                            }
                        });
                        tableRow.addView(actionButton);
                    }
                    tableRow.setLayoutParams(lp);
                    //finally, add the table row to the table layout.
                    tableLayout.addView(tableRow,lp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
/****************************************CREATE TABLE CONTENT ENDS HERE******************************************************************/
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference occupancyStateDR =FirebaseDatabase.getInstance().getReference();
                occupancyStateDR.child("blocks").child(getIntent().getStringExtra("selectedBlockName")).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Block blockstate = snapshot.getValue(Block.class);
                        if(blockstate.getBlockOccupied()<blockstate.getBlockCapacity()){
                            showPopUpAndAddPerson(v);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Block is Full!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        medDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUpAndAddDate(v);
            }
        });
    }

    private void manageCoronaPositivePerson(String personName){
        CoronaPositivePerson newPerson = new CoronaPositivePerson(personName);
        DatabaseReference coronaReference = FirebaseDatabase.getInstance().getReference();
        coronaReference.child("Corona").child("Positive").child(personName).setValue(newPerson);
    }

    //This method, creates a pop up box, to accept new person name, and start of quarantine date. End Date is calculated.
    //Medical date is entered later.
    private void showPopUpAndAddPerson(View view){
        //Popup layout is in person_popup, inflate it and build the alert dialog. It has two button Ok & Cancel.
        //Ok writes to the database.
        LayoutInflater li = LayoutInflater.from(BlockDetailActivity.this);
        View addPersonView = li.inflate(R.layout.person_popup,null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BlockDetailActivity.this);
        alertDialogBuilder.setView(addPersonView);
        textViewPersonName = addPersonView.findViewById(R.id.personNameFd);
        textViewPersonName.requestFocus();
        datePickerStartDate = addPersonView.findViewById(R.id.medDtPickerFd);
        final String function = "Add";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yy");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //from the date selection spinner, get the day, month and year and build a calendar object.
                                int day = datePickerStartDate.getDayOfMonth();
                                int month = datePickerStartDate.getMonth();
                                int year = datePickerStartDate.getYear();
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, month, day);
                                //Date in the format dd/mm/yy
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                String formattedDate = sdf.format(calendar.getTime());
                                try {
                                    //Hold it in java util data. because date operations will be out of the box
                                    startDate = sdf.parse(formattedDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (TextUtils.isEmpty(textViewPersonName.getText().toString())) {
                                    Toast.makeText(getApplicationContext(), "Please enter a name!", Toast.LENGTH_SHORT).show();
                                } else {
                                    personName = Validator.encodeForFirebaseKey(textViewPersonName.getText().toString());
                                    Person newPerson = new Person(personName, startDate, startDate, startDate);
                                    //get a reference to the Node BlockName+People
                                    peopleInQuarantineBlock_DR = FirebaseDatabase.getInstance().getReference()
                                            .child(getIntent().getStringExtra("selectedBlockName") + "People");

                                    //Inside that node, create a child with the name of the Person and populate it.
                                    //Attach a  listener.  next we'll check all the
                                    //dates on which all the people in a block started quarantine.
                                    //2 weeks + the latest date will be the end date of all the people in that blocl
                                    peopleInQuarantineBlock_DR.child(personName).setValue(newPerson);
                                    peopleInQuarantineBlock_DR.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            //update the dates by looking at the latest entrant data. function so as to diff listners
                                            updateDates(snapshot,function);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
/***********************************Update the Landing Page with Details of Occupancy, Start, End and Medical Date of the Block*/
                                    listOfBlock_DR = FirebaseDatabase.getInstance().getReference().child("blocks");
                                    listOfBlock_DR.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            updateLandingPage(snapshot, function);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }

    private void updateDates(DataSnapshot snapshot, String callingFunction){
        inDates.clear();
        for (DataSnapshot startTimeSnapShot : snapshot.getChildren()) {
            Person personObjectToExtractStartDate = startTimeSnapShot.getValue(Person.class);
            inDates.add(personObjectToExtractStartDate.getStartDate());
        }
        Date latestDate = Collections.max(inDates);
        LocalDate startDate = Instant.ofEpochMilli(latestDate.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = startDate.plusWeeks(2);
        LocalDate medDate = startDate.plusWeeks(2).plusDays(1);
        final Date endingDate = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        final Date medicalDate = Date.from(medDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        endDateForLandingPage = endingDate;
        medDateForLandingPage = medicalDate;
        for (DataSnapshot personSnapshot : snapshot.getChildren()) {
            Person person = personSnapshot.getValue(Person.class);
            person.setEndDate(endingDate);
            person.setMedicalDate(medicalDate);
            if(callingFunction.equals("Add")){
                peopleInQuarantineBlock_DR.child(person.getName()).setValue(person);
            } else if(callingFunction.equals("Delete")){
                deletePersonDR.child(person.getName()).setValue(person);
            }
        }

    }

    private void updateLandingPage(DataSnapshot snapshot, String callingFunction){
        for (DataSnapshot blockSnapshot : snapshot.getChildren()) {
            Block block = blockSnapshot.getValue(Block.class);
            assert block != null;
            //to update that block in which the person was added
            if(block.getBlockName().equals(getIntent().getStringExtra("selectedBlockName"))){
                block.setBlockOccupied(blockOccupiedForLandingPage);
                block.setQuarantineEndDate(endDateForLandingPage);
                block.setMedicalDate(medDateForLandingPage);
                if(callingFunction.equals("Add")){
                    listOfBlock_DR.child(block.getBlockName()).setValue(block);
                } else if(callingFunction.equals("Delete")){
                    blockUpdateDR.child(block.getBlockName()).setValue(block);
                }
            }
        }
    }
    /******************************************Manually Add a Date of Medical Exam in case not possible after quarantine ends**************************************************************************************************************************/
    private void showPopUpAndAddDate(View view){
        LayoutInflater li = LayoutInflater.from(BlockDetailActivity.this);
        View addMedDtView = li.inflate(R.layout.med_date_popup,null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BlockDetailActivity.this);
        alertDialogBuilder.setView(addMedDtView);
        datePickerMedDate = addMedDtView.findViewById(R.id.medDtPickerFd);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yy");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //from the date selection spinner, get the day, month and year and build a calendar object.
                                int day = datePickerMedDate.getDayOfMonth();
                                int month = datePickerMedDate.getMonth();
                                int year = datePickerMedDate.getYear();
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, month, day);
                                //Date in the format dd/mm/yy
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                String formattedDate = sdf.format(calendar.getTime());
                                try {
                                    //Hold it in java util data. because date operations will be out of the box
                                    medDate = sdf.parse(formattedDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                peopleInQuarantineBlock_DR.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot personSnapshot : snapshot.getChildren()) {
                                            Person person = personSnapshot.getValue(Person.class);
                                            person.setMedicalDate(medDate);
                                            peopleInQuarantineBlock_DR.child(person.getName()).setValue(person);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                final DatabaseReference blockDR = FirebaseDatabase.getInstance().getReference();
                                blockDR.child("blocks").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot blocks : snapshot.getChildren()){
                                            Block block = blocks.getValue(Block.class);
                                            if(block.getBlockName().equals(getIntent().getStringExtra("selectedBlockName"))){
                                                block.setMedicalDate(medDate);
                                                blockDR.child("blocks").child(block.getBlockName()).setValue(block);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog dialog = alertDialogBuilder.create();
            dialog.show();
        }

    private void deletePerson(String personName){
        final String function = "Delete";
        deletePersonDR = FirebaseDatabase.getInstance().getReference()
                .child(getIntent().getStringExtra("selectedBlockName") + "People");
        deletePersonDR.child(personName).removeValue();
        deletePersonDR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                blockOccupiedForLandingPage = (int) snapshot.getChildrenCount();
                if(snapshot.getChildrenCount()>0){
                    updateDates(snapshot,function);
                }
                blockUpdateDR = FirebaseDatabase.getInstance().getReference().child("blocks");
                blockUpdateDR.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        updateLandingPage(snapshot, function);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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