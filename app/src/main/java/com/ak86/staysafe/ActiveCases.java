package com.ak86.staysafe;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;


public class ActiveCases extends Fragment {

    private TableLayout coronaActiveTable;
    private DatabaseReference coronaPositiveDR;
    private int slNoCounter;
    private FirebaseAuth mAuth;
    private TableLayout.LayoutParams lp;
    private View updatePerson, negativeDatePopup;
    private ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view  =  inflater.inflate(R.layout.fragment_active_cases, container, false);
        mAuth = FirebaseAuth.getInstance();
        coronaActiveTable = view.findViewById(R.id.coronaActiveTable);
        progressBar = view.findViewById(R.id.progressBarActiveCases);
        lp = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0,5,5,0);
        coronaPositiveDR = FirebaseDatabase.getInstance().getReference();
        coronaPositiveDR.child("Corona").child("Positive").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                slNoCounter = 1;
                populateAndInitializeTable(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        return view;
    }

    private void populateAndInitializeTable(DataSnapshot snapshot){
        coronaActiveTable.removeAllViews();
        intializeTableHeader();
        for(DataSnapshot coronaPositiveSnapshot : snapshot.getChildren()){
            CoronaPositivePerson coronaPositivePerson = coronaPositiveSnapshot.getValue(CoronaPositivePerson.class);
            if(getContext()!=null){
                final TableRow tableRow = new TableRow(getActivity());

                TextView textViewSlNo = new TextView(getActivity());
                textViewSlNo.setText(String.valueOf(slNoCounter));
                textViewSlNo.setTextColor(Color.argb(255,33,150,243));
                textViewSlNo.setGravity(Gravity.CENTER);
                slNoCounter++;
                tableRow.addView(textViewSlNo);

                TextView textViewPersonName = new TextView(getActivity());
                textViewPersonName.setText(Validator.decodeFromFirebaseKey(coronaPositivePerson.getName().toString()));
                textViewPersonName.setTextColor(Color.argb(255,33,150,243));
                textViewPersonName.setGravity(Gravity.CENTER);
                textViewPersonName.setHeight(50);
                tableRow.addView(textViewPersonName);

                TextView textViewStatus = new TextView(getActivity());
                textViewStatus.setText("+ve");
                textViewStatus.setTextColor(Color.argb(255,33,150,243));
                textViewStatus.setGravity(Gravity.CENTER);
                tableRow.addView(textViewStatus);

                TextView textViewLocation  = new TextView(getActivity());
                textViewLocation.setText(Validator.decodeFromFirebaseKey(coronaPositivePerson.getCurrentLocation()));
                textViewLocation.setTextColor(Color.argb(255,33,150,243));
                textViewLocation.setGravity(Gravity.CENTER);
                tableRow.addView(textViewLocation);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

                TextView dateOfResultTV = new TextView(getActivity());
                dateOfResultTV.setText(sdf.format(coronaPositivePerson.getDayOfResult()));
                dateOfResultTV.setTextColor(Color.argb(255,33,150,243));
                dateOfResultTV.setGravity(Gravity.CENTER);
                dateOfResultTV.setWidth(150);
                tableRow.addView(dateOfResultTV);

                TextView dateOfNextTestTV = new TextView(getActivity());
                dateOfNextTestTV.setText(sdf.format(coronaPositivePerson.getDateOfNextTest()));
                dateOfNextTestTV.setTextColor(Color.argb(255,33,150,243));
                dateOfNextTestTV.setGravity(Gravity.CENTER);
                dateOfNextTestTV.setWidth(150);
                tableRow.addView(dateOfNextTestTV);

                /*TextView dateOfNegativeTestTv = new TextView(getActivity());
                dateOfNegativeTestTv.setText(sdf.format(coronaPositivePerson.getDateOfNegativeResult()));
                dateOfNegativeTestTv.setGravity(Gravity.CENTER);
                tableRow.addView(dateOfNegativeTestTv);*/
                DatabaseReference userAuthDR = FirebaseDatabase.getInstance().getReference();
                userAuthDR.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot users : snapshot.getChildren()){
                            User user = users.getValue(User.class);
                            if(mAuth.getCurrentUser().getEmail().equals(Validator.decodeFromFirebaseKey(user.getEmailId()))){
                                if(user.getUserLevel() > 2){
                                    MaterialButton btnNegativePerson = new MaterialButton(getActivity());
                                    btnNegativePerson.setText("Mark Negative");
                                    btnNegativePerson.setTextSize(8);
                                    btnNegativePerson.setElevation(5);
                                    btnNegativePerson.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    btnNegativePerson.setCornerRadius(8);
                                    btnNegativePerson.setTextColor(Color.argb(255,33,150,243));
                                    btnNegativePerson.setBackgroundColor(Color.parseColor("white"));
                                    btnNegativePerson.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            popUpForNegativeTestDate(coronaPositivePerson);
                                        }
                                    });
                                    tableRow.addView(btnNegativePerson);

                                    tableRow.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            popUpAndUpdateCoronaDetails(coronaPositivePerson);
                                            return true;
                                        }
                                    });

                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                tableRow.setMinimumHeight(50);
                tableRow.setElevation(4);
                tableRow.setBackgroundColor(Color.argb(100,236,235,232));
                tableRow.setLayoutParams(lp);
                progressBar.setVisibility(View.GONE);
                coronaActiveTable.addView(tableRow, lp);
            }

        }
    }

    private void intializeTableHeader(){
        final TableLayout.LayoutParams lp =
                new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0,5,5,0);
        if (getContext() != null){
            TableRow headerRow = new TableRow(getContext());
            TextView slno = new TextView(getActivity());
            slno.setText(" S.No ");
            slno.setTextColor(Color.parseColor("white"));
            slno.setGravity(Gravity.CENTER);
            headerRow.addView(slno);
            TextView name = new TextView(getActivity());
            name.setText("     Name     ");
            name.setTextColor(Color.parseColor("white"));
            name.setGravity(Gravity.CENTER);
            headerRow.addView(name);
            TextView status = new TextView(getActivity());
            status.setText("  Status  ");
            status.setTextColor(Color.parseColor("white"));
            status.setGravity(Gravity.CENTER);
            headerRow.addView(status);
            TextView location = new TextView(getActivity());
            location.setText("  Location  ");
            location.setTextColor(Color.parseColor("white"));
            location.setGravity(Gravity.CENTER);
            headerRow.addView(location);
            TextView dtOfResult = new TextView(getActivity());
            dtOfResult.setText("Tested\n+ve on");
            dtOfResult.setTextColor(Color.parseColor("white"));
            dtOfResult.setGravity(Gravity.CENTER);
            headerRow.addView(dtOfResult);
            TextView dtOfNextTest = new TextView(getActivity());
            dtOfNextTest.setText("Next Test\nOn");
            dtOfNextTest.setTextColor(Color.parseColor("white"));
            dtOfNextTest.setGravity(Gravity.CENTER);
            headerRow.addView(dtOfNextTest);
            /*TextView dtOfNegativeResult = new TextView(getActivity());
            dtOfNegativeResult.setText("Tested\n   -ve on   ");
            dtOfNegativeResult.setTextColor(Color.parseColor("white"));
            dtOfNegativeResult.setGravity(Gravity.CENTER);
            headerRow.addView(dtOfNegativeResult);*/
            DatabaseReference userAuthDR = FirebaseDatabase.getInstance().getReference();
            userAuthDR.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot users : snapshot.getChildren()){
                        User user = users.getValue(User.class);
                        if(mAuth.getCurrentUser().getEmail().equals(Validator.decodeFromFirebaseKey(user.getEmailId()))){
                            if(user.getUserLevel() > 2){
                                TextView action = new TextView(getActivity());
                                action.setText("  Actions  ");
                                action.setTextColor(Color.parseColor("white"));
                                action.setGravity(Gravity.CENTER);
                                headerRow.addView(action);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            headerRow.setBackgroundColor(Color.argb(200,33,150,243));
            headerRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            coronaActiveTable.addView(headerRow,lp);
        }
    }

    private void popUpAndUpdateCoronaDetails(CoronaPositivePerson coronaPositivePerson){
        LayoutInflater li = LayoutInflater.from(getContext());
        updatePerson = li.inflate(R.layout.corona_person_details, null);
        TextView curLoc = updatePerson.findViewById(R.id.locationOfCoronaPersonFd);
        DatePicker dtOfResultPicker = updatePerson.findViewById(R.id.dtOfPositiveTestFd);
        DatePicker dtOfNextPicker = updatePerson.findViewById(R.id.dtOfNextTestFd);
        //DatePicker dtOfNegativePicker = updatePerson.findViewById(R.id.dtOfNegativeTestFd);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(updatePerson);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String currentLocation = Validator.encodeForFirebaseKey(curLoc.getText().toString().trim());
                Date resultDate = readDate(dtOfResultPicker);
                Date nextDate = readDate(dtOfNextPicker);
                //Date negativeDate = readDate(dtOfNegativePicker);
                DatabaseReference personUpdateDR = FirebaseDatabase.getInstance().getReference();
                coronaPositivePerson.setCurrentLocation(currentLocation);
                coronaPositivePerson.setDayOfResult(resultDate);
                coronaPositivePerson.setDateOfNextTest(nextDate);
                //coronaPositivePerson.setDateOfNegativeResult(negativeDate);
                personUpdateDR.child("Corona").child("Positive").child(coronaPositivePerson.getName()).setValue(coronaPositivePerson);
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void popUpForNegativeTestDate(CoronaPositivePerson nowNegativePerson){
        LayoutInflater li = LayoutInflater.from(getContext());
        negativeDatePopup = li.inflate(R.layout.negative_test_date, null);
        DatePicker dtOfNegativePicker = negativeDatePopup.findViewById(R.id.dtOfNegativeTestFd);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(negativeDatePopup);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Date negativeDate = readDate(dtOfNegativePicker);
                DatabaseReference personUpdateDR = FirebaseDatabase.getInstance().getReference();
                nowNegativePerson.setDateOfNegativeResult(negativeDate);
                nowNegativePerson.setNowNegative(true);
                nowNegativePerson.setNowPositive(false);
                nowNegativePerson.setActive(false);
                personUpdateDR.child("Corona").child("Positive").child(nowNegativePerson.getName()).setValue(nowNegativePerson);
                final DatabaseReference coronaPersonToRecoveredDR = FirebaseDatabase.getInstance().getReference();
                moveNegativePersonToRecovered(coronaPersonToRecoveredDR.child("Corona").child("Positive").child(nowNegativePerson.getName()),
                        coronaPersonToRecoveredDR.child("Corona").child("Negative").child(nowNegativePerson.getName()));
                coronaPersonToRecoveredDR.child("Corona").child("Positive").child(nowNegativePerson.getName()).removeValue();
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void moveNegativePersonToRecovered(final DatabaseReference fromPath, final DatabaseReference toPath){
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

    private Date readDate(DatePicker inputDate){
        Date convertedDate = new Date();
        int day = inputDate.getDayOfMonth();
        int month = inputDate.getMonth();
        int year = inputDate.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month,day);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = sdf.format(calendar.getTime());
        try{
            convertedDate = sdf.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

}