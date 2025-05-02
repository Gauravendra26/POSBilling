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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.syss.MifareClassicTool.Activities.MainMenu;

public class Table_Selection_Activity extends AppCompatActivity implements Adapter_Tableqnt.ProductClick  {
    ImageView imgBack;
    LinearLayout llL1;
    RecyclerView rvTables;
    Adapter_Tableqnt adapter_tableqnt;
    ArrayList<Model_Tableqnt> model_tableqnt;
    TextView tvCardHolderName;
    String userValue, code, PiperbarUser, AbsoluteBarUser, SaltpepperUser, FrozenFoodUser, ZayacaUser, RestaurantUser,
        tableId, location, locationName, name, memberID, displayAs;
    int id;
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
        secondLocationDataList = intent.getStringArrayListExtra("secondLocationDataList");
        name = intent.getStringExtra("name");
        memberID = intent.getStringExtra("memberID");


        tvCardHolderName.setText("Hi, " + name);

        Log.e("ValueOfUser", " " + userValue + " " + code + " " + secondLocationDataList + " " + PiperbarUser + " " +
            FrozenFoodUser + " " + ZayacaUser + " " + RestaurantUser);

        tableData();

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
                intent.putExtra("name", name);
                intent.putExtra("memberID", memberID);
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

    public void tableData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.new_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        model_tableqnt = new ArrayList<Model_Tableqnt>();
        // Loop through the list with a step of 2 to get both TableId and Location in each iteration
        for (int i = 0; i < secondLocationDataList.size(); i++) {
            String data = secondLocationDataList.get(i);
            String data1 = secondLocationDataList.get(i);

            // Extract TableId and Location using the modified extractTableId method
            tableId = String.valueOf(extractTableId(data));
            location = String.valueOf(extractLocation(data1));

            Model_Tableqnt model = new Model_Tableqnt(tableId, location);
            model_tableqnt.add(model);

            // Log the values
            Log.e("ValueOftableId", tableId + " " + location);

        }

        adapter_tableqnt = new Adapter_Tableqnt(getApplicationContext(), model_tableqnt);

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        rvTables.setLayoutManager(layoutManager);
        rvTables.setItemViewCacheSize(0);
        rvTables.setHasFixedSize(true);
        rvTables.setItemAnimator(new DefaultItemAnimator());
        rvTables.setAdapter(adapter_tableqnt);

        // I assume you have a method in your Adapter_Tableqnt to set the context
        adapter_tableqnt.set(Table_Selection_Activity.this);

        progressDialog.dismiss();
    }


    private int extractTableId(String data) {
        String[] parts = data.split(", ");
        for (String part : parts) {
            if (part.startsWith("TableId:")) {
                return Integer.parseInt(part.split(":")[1].trim());
            }
        }
        return -1;  // Handle the case where TableId is not found
    }

    // Add a new method to extract Location
    private int extractLocation(String data) {
        String[] parts = data.split(", ");
        for (String part : parts) {
            if (part.startsWith("Location:")) {
                return Integer.parseInt(part.split(":")[1].trim());
            }
        }
        return -1;  // Handle the case where Location is not found
    }


    @Override
    public void tableClick(int position, String tableId) {
        Intent intent = new Intent(getApplicationContext(), Waiter_Activity.class);
        intent.putExtra("userValue", userValue);
        intent.putExtra("tableId", tableId);
        intent.putExtra("code", code);
        intent.putExtra("locationName", locationName);
        intent.putExtra("name", name);
        intent.putExtra("memberID", memberID);

        startActivity(intent);
//        updatetableData(tableId);
    }

    @Override
    public void tableEngageClick(int position, String tableId) {
        Intent intent = new Intent(getApplicationContext(), Waiter_Activity.class);
        intent.putExtra("userValue", userValue);
        intent.putExtra("tableId", tableId);
        intent.putExtra("code", code);
        intent.putExtra("locationName", locationName);
        intent.putExtra("name", name);
        intent.putExtra("memberID", memberID);

        startActivity(intent);
    }




    public void updatetableData(String tableId) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.new_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {
            // Connection successful
            System.out.println("Connected to the database");
            Log.e("asfaafs", "Connected to the database");

            try {
                // Use PreparedStatement to avoid SQL injection
                String query = "UPDATE TM_LOCATION_TABLE SET TableOrder = ? WHERE TableId = ?";

                PreparedStatement preparedStatement = connect.prepareStatement(query);

                // Set parameters for the PreparedStatement
                preparedStatement.setString(1, "Engage"); // Set TableOrder to "Engage"
                preparedStatement.setInt(2, Integer.parseInt(tableId)); // Set TableId to 5
                Toast.makeText(this, "Success " + tableId, Toast.LENGTH_SHORT).show();

                // Execute the update
                preparedStatement.executeUpdate();
                // Close the PreparedStatement
                preparedStatement.close();

                connect.close();
            } catch (SQLException e) {
                // Handle the SQL exception appropriately
                e.printStackTrace();
            } catch (Exception e) {
                // Handle other exceptions appropriately
                e.printStackTrace();
            }
            progressDialog.dismiss();
        } else {
            // Connection failed
            System.out.println();
            Log.e("asfaafs", "Failed to connect to the database");
        }
    }



}
