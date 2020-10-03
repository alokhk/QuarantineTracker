package com.ak86.quarantinetracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.google.android.material.tabs.TabLayout;

import com.ak86.quarantinetracker.databinding.ActivityCoronaBinding;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


public class CoronaActivity extends AppCompatActivity {

        ActivityCoronaBinding binding;
        private TextView textViewCoronaPositivePersonName;
        private DatePicker resultDatePicker;
        private Date resultDate;
        private String coronaPositivePersonName;

        // tab titles
        private String[] titles = new String[]{"Active Cases", "Recovered Cases"};

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityCoronaBinding.inflate(getLayoutInflater());
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            setContentView(binding.getRoot());
            init();
        }

        private void init() {
            // removing toolbar elevation
            getSupportActionBar().setElevation(0);

            binding.viewPager.setAdapter(new ViewPagerFragmentAdapter(this));

            // attaching tab mediator
            new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                    (tab, position) -> tab.setText(titles[position])).attach();
        }

        private class ViewPagerFragmentAdapter extends FragmentStateAdapter {

            public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
                super(fragmentActivity);
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new ActiveCases();
                    case 1:
                        return new RecoveredCases();

                }
                return new ActiveCases();
            }

            @Override
            public int getItemCount() {
                return titles.length;
            }
        }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.active_person,menu);
        MenuItem addCoronaPerson = menu.findItem(R.id.addPositivePerson);
        return true;
    }

    private void popUpAndGetPerson(){
        //Popup layout is in person_popup, inflate it and build the alert dialog. It has two button Ok & Cancel.
        //Ok writes to the database.
        LayoutInflater li = LayoutInflater.from(CoronaActivity.this);
        View coronaPersonView = li.inflate(R.layout.add_corona_person,null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CoronaActivity.this);
        alertDialogBuilder.setView(coronaPersonView);
        textViewCoronaPositivePersonName = coronaPersonView.findViewById(R.id.personNameFdCP);
        resultDatePicker = coronaPersonView.findViewById(R.id.dtResultCP);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yy");
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //from the date selection spinner, get the day, month and year and build a calendar object.
                                int day = resultDatePicker.getDayOfMonth();
                                int month = resultDatePicker.getMonth();
                                int year = resultDatePicker.getYear();
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, month, day);
                                //Date in the format dd/mm/yy
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                String formattedDate = sdf.format(calendar.getTime());
                                try {
                                    //Hold it in java util data. because date operations will be out of the box
                                    resultDate = sdf.parse(formattedDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if (TextUtils.isEmpty(textViewCoronaPositivePersonName.getText().toString())) {
                                    Toast.makeText(getApplicationContext(), "Please enter a name!", Toast.LENGTH_SHORT).show();
                                } else {
                                    coronaPositivePersonName = Validator.encodeForFirebaseKey(textViewCoronaPositivePersonName.getText().toString());
                                    CoronaPositivePerson newCoronaPerson = new CoronaPositivePerson(coronaPositivePersonName);
                                    DatabaseReference newCoronaDR = FirebaseDatabase.getInstance().getReference();
                                    newCoronaDR.child("Corona").child("Positive").child(coronaPositivePersonName).setValue(newCoronaPerson);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.addPositivePerson :
                    popUpAndGetPerson();
                default:
                    return super.onOptionsItemSelected(item);
            }
    }
}