package de.syss.MifareClassicTool;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.syss.MifareClassicTool.Activities.MainMenu;

public class Table_Selection_Activity extends AppCompatActivity implements Adapter_Tableqnt.ProductClick {
    ImageView imgBack;
    LinearLayout llL1;
    RecyclerView rvTables;
    Adapter_Tableqnt adapter_tableqnt;
    ArrayList<Model_Tableqnt> model_tableqnt;
    TextView tvCardHolderName;
    String userValue, code, checkOccupiedtable, AbsoluteBarUser, SaltpepperUser, FrozenFoodUser, ZayacaUser, RestaurantUser,
        tableId, location,locationName, name, memberID, displayAs;
    int id,tableOrder ;
    ArrayList<String> secondLocationDataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_selection);
        Utils.blackIconStatusBar(Table_Selection_Activity.this, R.color.main);
        init();
        Intent intent = getIntent();
        userValue = intent.getStringExtra("userValue");
        code = intent.getStringExtra("code");
        locationName = intent.getStringExtra("locationName");
//        secondLocationDataList = intent.getStringArrayListExtra("secondLocationDataList");
//        name = intent.getStringExtra("name");
//        memberID = intent.getStringExtra("memberID");


//        tvCardHolderName.setText("Hi, " + name);

        Log.e("ValueOfUser", " " + userValue + " " + code + " " + locationName  );

        tableData(code);
//getBillData(code,tableId);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        llL1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Menu_Activity.class);
//                intent.putExtra("name", name);
//                intent.putExtra("memberID", memberID);
                startActivity(intent);
            }
        });


    }

    void init() {
        imgBack = findViewById(R.id.imgBack);
        llL1 = findViewById(R.id.llL1);
        rvTables = findViewById(R.id.rvTables);
        tvCardHolderName = findViewById(R.id.tvCardHolderName);
    }


    public void tableData(String code) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.new_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        // Toast.makeText(this, ""+code, Toast.LENGTH_SHORT).show();
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                ConnectionHelper connectionHelper = new ConnectionHelper(getApplicationContext());
                Connection connect = connectionHelper.connectionclass();

                if (connect != null) {
                    Log.e("DatabaseConnection", "Connected to the database");

                    try {
                        String query = "SELECT * FROM TM_LOCATION_TABLE WHERE Location = ?";
                        PreparedStatement preparedStatement = connect.prepareStatement(query);
                        preparedStatement.setString(1, code);
                        ResultSet rs = preparedStatement.executeQuery();

                        model_tableqnt = new ArrayList<Model_Tableqnt>();

                        while (rs.next()) {
                            String tableId = rs.getString("TableId");
                            String location = rs.getString("Location");
//                            String tableOrder = rs.getString("TableOrder");
                            Model_Tableqnt model = new Model_Tableqnt(tableId, location );
                            model_tableqnt.add(model);

                            // Log the TableId and Location
                            Log.d("DatabaseQuery", "TableId: " + tableId + ", Location: " + location );
                        }

                        if (model_tableqnt.isEmpty()) {
                            Log.e("DatabaseQuery", "No data found for Location: " + code);
                        } else {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    adapter_tableqnt = new Adapter_Tableqnt(getApplicationContext(), model_tableqnt);
                                    GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
                                    rvTables.setLayoutManager(layoutManager);
                                    rvTables.setItemViewCacheSize(0);
                                    rvTables.setHasFixedSize(true);
                                    rvTables.setItemAnimator(new DefaultItemAnimator());
                                    rvTables.setAdapter(adapter_tableqnt);
                                    // I assume you have a method in your Adapter_Tableqnt to set the context
                                    adapter_tableqnt.set(Table_Selection_Activity.this);
                                }
                            });
                             }

                        progressDialog.dismiss();
                        rs.close();
                        preparedStatement.close();
                        connect.close();
                    } catch (SQLException e) {
                        Log.e("DatabaseQuery", "SQL Exception: " + e.getMessage());
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                } else {
                    Log.e("DatabaseConnection", "Failed to connect to the database");
                    progressDialog.dismiss();
                }
            }
        });
    }




    @Override
    public void tableClick(int position, String tableId,String location) {
        Intent intent = new Intent(getApplicationContext(), Waiter_Activity.class);
        intent.putExtra("userValue", userValue);
        intent.putExtra("tableId", tableId);
        intent.putExtra("code", code);
        intent.putExtra("locationName", locationName);
//        intent.putExtra("name", name);
//        intent.putExtra("memberID", memberID);
        intent.putExtra("location", location);
        intent.putExtra("checkOccupiedtable", "0");

        startActivity(intent);


    }

    @Override
    public void tableEngageClick(int position, String tableId,String location,String MemberIdFinal) {

//        Toast.makeText(this, " "+MemberIdFinal, Toast.LENGTH_SHORT).show();
        showCustomAlertDialog(tableId,MemberIdFinal);
    }

    private void showCustomAlertDialog( String tableId,String MemberIdFinal) {

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.table_custom_layout, null);

        RelativeLayout rlCancel = dialogView.findViewById(R.id.rlCancel);
        RelativeLayout rlPrint = dialogView.findViewById(R.id.rlPrint);
        RelativeLayout rlOrderCancel = dialogView.findViewById(R.id.rlOrderCancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();


        rlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), Waiter_Activity.class);
                intent.putExtra("userValue", userValue);
                intent.putExtra("tableId", tableId);
                intent.putExtra("code", code);
                intent.putExtra("locationName", locationName);
                intent.putExtra("MemberIdFinal", MemberIdFinal);
//        intent.putExtra("name", name);
//        intent.putExtra("memberID", memberID);
                intent.putExtra("location", location);
                intent.putExtra("checkOccupiedtable", "1");

                startActivity(intent);
alertDialog.dismiss();
            }
        });

        rlPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userValue.matches("1")){
                    Intent intent = new Intent(getApplicationContext(), Bill_Activity.class);
                    intent.putExtra("userValue", userValue);
                    intent.putExtra("tableId", tableId);
                    intent.putExtra("code", code);
                    intent.putExtra("locationName", locationName);
//        intent.putExtra("name", name);
//        intent.putExtra("memberID", memberID);
                    intent.putExtra("location", location);
                    intent.putExtra("checkOccupiedtable", "1");

                    startActivity(intent);
                } else{
                    Intent intent = new Intent(getApplicationContext(), OccupiedTables_Activity.class);
                    intent.putExtra("userValue", userValue);
                    intent.putExtra("tableId", tableId);
                    intent.putExtra("code", code);
                    intent.putExtra("locationName", locationName);
//        intent.putExtra("name", name);
//        intent.putExtra("memberID", memberID);
                    intent.putExtra("location", location);
                    intent.putExtra("checkOccupiedtable", "1");

                    startActivity(intent);
                }

                alertDialog.dismiss();
            }
        });

        rlOrderCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent intent = new Intent(getApplicationContext(), OrderCancel_Activity.class);
                    intent.putExtra("userValue", userValue);
                    intent.putExtra("tableId", tableId);
                    intent.putExtra("code", code);
                    intent.putExtra("locationName", locationName);

                    intent.putExtra("location", location);
                    intent.putExtra("checkOccupiedtable", "1");

                    startActivity(intent);

                alertDialog.dismiss();
            }
        });


        alertDialog.show();
    }



}
