package de.syss.MifareClassicTool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Waiter_Activity extends AppCompatActivity implements   Adapter_Waiter.ProductClick {

    ImageView imgBack;
    RecyclerView rvWaiter;
    TextView tvCardHolderName;
    ArrayList<Model_Waiter> model_waiter;
    Adapter_Waiter adapter_waiter;


    String id,displayAs,userValue,tableId,locationName,name,memberID,code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter);
        Utils.blackIconStatusBar( this, R.color.main);
        init();
        waiterData();

        Intent intent = getIntent();
        userValue = intent.getStringExtra("userValue");
        tableId = intent.getStringExtra("tableId");
        locationName = intent.getStringExtra("locationName");
        name = intent.getStringExtra("name");
        memberID = intent.getStringExtra("memberID");
        code = intent.getStringExtra("code");

        tvCardHolderName.setText("Hi, "+name);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
    }
    void init() {
        rvWaiter = findViewById(R.id.rvWaiter);
        tvCardHolderName = findViewById(R.id.tvCardHolderName);
        imgBack = findViewById(R.id.imgBack);


    }

    public void waiterData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.new_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {
            // Connection successful
            System.out.println("Connected to the database");
            Log.e("wwqq", "Connected to the database");
            model_waiter = new ArrayList<>();

            try {
                String query = "SELECT * FROM TM_WaiterMaster";

                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);

                while (rs.next()) {
                    progressDialog.dismiss();
                    // Retrieve data for each row
                    id = rs.getString("ID");
                    displayAs = rs.getString("DisplayAs");


                    Log.e("WaiterData", "ID: " + id + ", DisplayAs: " + displayAs);

                    if (id != null && displayAs != null) {
                        // Process the data
                        Model_Waiter menu = new Model_Waiter(id, displayAs);
                        model_waiter.add(menu);

                        Log.e("WaiterData", id + " " + displayAs);
                    } else {
                        Log.e("WaiterData", "Incomplete data for this row");
                    }

                }
                adapter_waiter = new Adapter_Waiter(getApplicationContext(), model_waiter);
                LinearLayoutManager layoutManagerSearch = new LinearLayoutManager(getApplicationContext(),
                    LinearLayoutManager.VERTICAL, true);
                layoutManagerSearch.setStackFromEnd(true);
                layoutManagerSearch.setReverseLayout(true);

                rvWaiter.setLayoutManager(layoutManagerSearch);
                rvWaiter.setHasFixedSize(true);
                rvWaiter.setItemAnimator(new DefaultItemAnimator());
                rvWaiter.setAdapter(adapter_waiter);
                adapter_waiter.set( this);
                connect.close();
            } catch (Exception e) {
                // Handle the exception appropriately
                e.printStackTrace();
            }
        } else {
            // Connection failed
            System.out.println();
            Log.e("wwqq", "Failed to connect to the database");
        }
    }

    @Override
    public void waiterClick(int position, String id, String displayAs) {
        Intent intent = new Intent(getApplicationContext(), Menu_Activity.class);
        intent.putExtra("userValue", userValue);
        intent.putExtra("tableId", tableId);
        intent.putExtra("code", code);
        intent.putExtra("locationName", locationName);
        intent.putExtra("name", name);
        intent.putExtra("memberID", memberID);
        intent.putExtra("waiterID", id);

        startActivity(intent);
    }
}
