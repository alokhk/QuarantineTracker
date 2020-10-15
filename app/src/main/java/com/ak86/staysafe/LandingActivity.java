package com.ak86.staysafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LandingActivity extends AppCompatActivity {

    private DatabaseReference blocksDatabaseReference, coronaReference;
    private List<Block> blocksList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter blockAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressBar progressBar4;
    private TextView totalBlocks, overallCapacity, totalQuarantined;
    private int blocksCount, capacityCount, quarantineCount;
    private FirebaseAuth mAuth;
    private int totalCases, totalActive, totalRecovered;
    private PieChart pieChartCoronaStats;
    private ConstraintLayout constraintLayout;
    private Button reportButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        getSupportActionBar().setTitle("StaySafe Dashboard");
        mAuth = FirebaseAuth.getInstance();
        totalBlocks = findViewById(R.id.totalQuarantineBlocksFd);
        overallCapacity = findViewById(R.id.totalCapacityFd);
        totalQuarantined = findViewById(R.id.totalQuarantinedFd);
        progressBar4 = findViewById(R.id.progressBar4);
        reportButton = findViewById(R.id.btnGenerateReport);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reportActivity = new Intent(getApplicationContext(),OccupancyStateReport.class);
                startActivity(reportActivity);
            }
        });
        pieChartCoronaStats = findViewById(R.id.piechart);
        constraintLayout = findViewById(R.id.constraintLayout);
        coronaReference = FirebaseDatabase.getInstance().getReference();
        coronaReference.child("Corona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalActive = (int) snapshot.child("Positive").getChildrenCount();
                totalRecovered = (int) snapshot.child("Negative").getChildrenCount();
                totalCases = totalActive + totalRecovered;
                ArrayList<PieEntry> variousCounts = new ArrayList();
                //variousCounts.add(new PieEntry(240, 0));
                variousCounts.add(new PieEntry(totalActive, "Active"));
                variousCounts.add(new PieEntry(totalRecovered, "Recovered"));
                PieDataSet dataSet = new PieDataSet(variousCounts, "Click Me For Details");
                dataSet.setSliceSpace(3f);
                dataSet.setIconsOffset(new MPPointF(0, 40));
                dataSet.setSelectionShift(5f);


                PieData data = new PieData(dataSet);
                dataSet.setColors(Color.RED,Color.GREEN);
                data.setValueTextSize(14f);
                data.setValueTextColor(Color.BLACK);
                data.setValueFormatter(new MyValueFormatter());
                pieChartCoronaStats.setData(data);
                pieChartCoronaStats.animateXY(5000, 5000);
                pieChartCoronaStats.setCenterText(totalCases+" Cases Total");
                pieChartCoronaStats.setDrawEntryLabels(false);
                pieChartCoronaStats.getDescription().setEnabled(false);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        pieChartCoronaStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent coronaStatsIntent = new Intent(getApplicationContext(),CoronaActivity.class);
                startActivity(coronaStatsIntent);
            }
        });
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent coronaStatsIntent = new Intent(getApplicationContext(),CoronaActivity.class);
                startActivity(coronaStatsIntent);

            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.requestLayout();
        blocksDatabaseReference = FirebaseDatabase.getInstance().getReference().child("blocks");
        blocksDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                blocksCount = (int)snapshot.getChildrenCount();
                blocksList.clear();
                capacityCount = 0;
                quarantineCount = 0;
                for(DataSnapshot blocksSnapshot : snapshot.getChildren()){
                    Block block = blocksSnapshot.getValue(Block.class);
                    capacityCount = capacityCount + block.getBlockCapacity();
                    quarantineCount = quarantineCount + block.getBlockOccupied();
                    blocksList.add(block);
                    //Log.println(Log.ASSERT,"BLOCKNAME : ", block.getBlockName());
                    layoutManager =  new LinearLayoutManager(getApplicationContext());
                    blockAdapter = new BlockDetailsAdapter(blocksList);
                    recyclerView.removeAllViews();
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(blockAdapter);
                    totalBlocks.setText(String.valueOf(blocksCount));
                    overallCapacity.setText(String.valueOf(capacityCount));
                    totalQuarantined.setText(String.valueOf(quarantineCount));






                    if(progressBar4 != null){
                        progressBar4.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.side_menu,menu);
        MenuItem manageBlock = menu.findItem(R.id.manageBlocks);
        MenuItem addBarrackType = menu.findItem(R.id.addBarrackType);
        MenuItem manageUsers = menu.findItem(R.id.manageUsers);
        MenuItem logout = menu.findItem(R.id.itemLogout);
       /* DatabaseReference userReference = FirebaseDatabase.getInstance().getReference();
        userReference.child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot users : snapshot.getChildren()){
                            User user = users.getValue(User.class);
                            assert user != null;
                            if(Objects.equals(mAuth.getCurrentUser().getEmail(), Validator.decodeFromFirebaseKey(user.getEmailId()))){
                                if(user.getUserLevel() == 2){
                                    manageBlock.setVisible(true);
                                    addBarrackType.setVisible(true);
                                    manageUsers.setVisible(true);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
*/
        /*if(Objects.equals(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail(), "admin@39gtc.gr")){*/
        Log.println(Log.ASSERT, "Alok",String.valueOf(getIntent().getIntExtra("currentUserAuthLevel",0)));
        if(getIntent().getIntExtra("currentUserAuthLevel",0) > 2){
            manageBlock.setVisible(true);
            addBarrackType.setVisible(true);
            manageUsers.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.manageBlocks :
                Intent manageIntent = new Intent(getApplicationContext(), ManageBlockActivity.class);
                startActivity(manageIntent);
                return true;
            case R.id.addBarrackType :
                Intent barrackTypeIntent = new Intent(getApplicationContext(),BarrackTypeActivity.class);
                startActivity(barrackTypeIntent);
                return true;
            case R.id.manageUsers :
                Intent manageUsersIntent = new Intent(getApplicationContext(), ManageUserActivity.class);
                startActivity(manageUsersIntent);
                return true;
            case R.id.itemLogout :
                mAuth.signOut();
                finish();
                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public class MyValueFormatter extends ValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("#");
        }

        @Override
        public String getFormattedValue(float value) {
            return mFormat.format(value);
        }
    }
}