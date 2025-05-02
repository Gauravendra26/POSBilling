package de.syss.MifareClassicTool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import de.syss.MifareClassicTool.Activities.MainMenu;
import de.syss.MifareClassicTool.Activities.ReadTag;

public class Waiter_Activity extends AppCompatActivity implements   Adapter_Waiter.ProductClick {

    ImageView imgBack;
    RecyclerView rvWaiter;
    TextView tvCardHolderName;
    ArrayList<Model_Waiter> model_waiter;
    Adapter_Waiter adapter_waiter;


    String id,displayAs,userValue,tableId,locationName,name,MainID,memberID,code,location,
        MemberIdFinal,checkOccupiedtable;
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
//        name = intent.getStringExtra("name");
         code = intent.getStringExtra("code");
        location = intent.getStringExtra("location");
        checkOccupiedtable = intent.getStringExtra("checkOccupiedtable");
        MemberIdFinal = intent.getStringExtra("MemberIdFinal");
getCardData(MemberIdFinal);
        Log.e("qeqrt",""+userValue+" tableId "+tableId+" locationName "+locationName+
            " memberID "+memberID+" code "+code+" location "+location+" checkOccupiedtable "+checkOccupiedtable);

//        Toast.makeText(this, ""+checkOccupiedtable, Toast.LENGTH_SHORT).show();
//        tvCardHolderName.setText("Hi, "+name);
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

        ConnectionHelper connectionHelper = new ConnectionHelper(this);
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });

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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter_waiter = new Adapter_Waiter(getApplicationContext(), model_waiter);
                        LinearLayoutManager layoutManagerSearch = new LinearLayoutManager(getApplicationContext(),
                            LinearLayoutManager.VERTICAL, true);
                        layoutManagerSearch.setStackFromEnd(true);
                        layoutManagerSearch.setReverseLayout(true);

                        rvWaiter.setLayoutManager(layoutManagerSearch);
                        rvWaiter.setHasFixedSize(true);
                        rvWaiter.setItemAnimator(new DefaultItemAnimator());
                        rvWaiter.setAdapter(adapter_waiter);
                        adapter_waiter.set(Waiter_Activity.this);
                    }
                });

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
    public void getCardData(String memberId) {
        ConnectionHelper connectionHelper = new ConnectionHelper(this);
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {
            // Connection successful
            // Proceed with AsyncTask to fetch data from the database
            Log.e("eqhoifusa", "Connected to the database");

            new FetchCardDataTask().execute(connect, memberId);
        } else {
            // Connection failed
            Log.e("eqhoifusa", "Failed to connect to the database");

            // Handle failure to connect to the database
        }
    }

    private class FetchCardDataTask extends AsyncTask<Object, Void, String> {
        private String memberId;
        private String MAINID; // Declare MAINID here

        @Override
        protected String doInBackground(Object... params) {
            Connection connect = (Connection) params[0];
            memberId = (String) params[1]; // Assign the value to the class variable
            String name = null;

            try {
                // Create a SQL query to select data from the table where memberID matches
                String query = "SELECT name, MAINID FROM TM_Memberinformation WHERE memberID = ?";
                PreparedStatement statement = connect.prepareStatement(query);
                statement.setString(1, memberId);
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    // Retrieve data from the ResultSet
                    name = rs.getString("name");
                    MAINID = rs.getString("MAINID");
//                    cardAmountData(MAINID);
                }

                rs.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return name;
        }

        @Override
        protected void onPostExecute(String name) {
            super.onPostExecute(name);
            MainID = MAINID;
            // Update UI with the fetched data
            if (name != null) {
                runOnUiThread(() -> {
//                    llTableDeatils.setVisibility(View.VISIBLE);
//                    tvMemberId.setText(memberId); // Set memberId to tvMemberId
//                    tvName.setText(name);
                    Log.e("eqhoifusa", "Connected to the database" + " " + name + " " + MAINID);
                }); // Data found

            } else {
                // No data found
                System.out.println("No data found for memberId: " + memberId);
                Log.e("eqhoifusa", "No data found for memberId: " + memberId);


            }
        }
    }

    @Override
    public void waiterClick(int position, String id, String displayAs) {

        if (checkOccupiedtable .matches(String.valueOf(1)) ) {
          Intent intent = new Intent(getApplicationContext(), Menu_Activity.class);
          SharedPreferences sharedPreferences = getSharedPreferences("NecessaryData", MODE_PRIVATE);
          SharedPreferences.Editor myEdit = sharedPreferences.edit();
          myEdit.putString("userValue", userValue);
          myEdit.putString("tableId", tableId);
          myEdit.putString("code", code);
          myEdit.putString("locationName", locationName);
           myEdit.putString("waiterID", id);
          myEdit.putString("location", location);
          myEdit.putString("checkOccupiedtable", checkOccupiedtable);
          myEdit.putString("MAINID", MainID);
          myEdit.apply();
          myEdit.commit();
          startActivity(intent);
      } else {
          Intent intent = new Intent(getApplicationContext(), ReadTag.class);
          SharedPreferences sharedPreferences = getSharedPreferences("NecessaryData", MODE_PRIVATE);
          SharedPreferences.Editor myEdit = sharedPreferences.edit();
          myEdit.putString("userValue", userValue);
          myEdit.putString("tableId", tableId);
          myEdit.putString("code", code);
          myEdit.putString("locationName", locationName);
           myEdit.putString("waiterID", id);
          myEdit.putString("location", location);
          myEdit.putString("checkOccupiedtable", checkOccupiedtable);
          myEdit.apply();
          myEdit.commit();
          startActivity(intent);
      }

    }
}
