package de.syss.MifareClassicTool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.syss.MifareClassicTool.Room.Adapter_OrderCancel;

public class OrderCancel_Activity extends AppCompatActivity {
    String MemberId, OpeningBalance, BillNoFinal, OpeningBalanceFinal, cardAmount;
    String userValue, locationName, tableId, code, checkOccupiedtable;
    double totalSp;
    ArrayList<Model_OccupiedTable> model_occupiedTable;
    ArrayList<Model_KOTCancel> model_kotCancel;
    Adapter_OrderCancel adapter_orderCancel;
    Adapter_KOTCancel adapter_kotCancel;


    RecyclerView rvOrderCancel, rvKOTNoCancel;
    RelativeLayout rlOrderCancel;
    TextView tvtablenNoofBill, tvGrandTotal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_cancel);
        init();

        Intent intent = getIntent();
        userValue = intent.getStringExtra("userValue");
        locationName = intent.getStringExtra("locationName");
        tableId = intent.getStringExtra("tableId");
        code = intent.getStringExtra("code");
        checkOccupiedtable = intent.getStringExtra("checkOccupiedtable");

        Log.e("gehash", "userValue " + userValue + " locationName " + locationName +
            " checkOccupiedtable " + checkOccupiedtable + " code " + code + " tableId " + tableId);

        getBillData(code, tableId);


        runOnUiThread(() -> {
            tvtablenNoofBill.setText("Table : " + tableId);
//            tvGrandTotal.setText("Total : "+"\u20B9"+  totalSp);

        });

        rlOrderCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    void init() {
        rvOrderCancel = findViewById(R.id.rvOrderCancel);
        rvKOTNoCancel = findViewById(R.id.rvKOTNoCancel);
        rlOrderCancel = findViewById(R.id.rlOrderCancel);
        tvtablenNoofBill = findViewById(R.id.tvtablenNoofBill);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);
    }

    public void getBillData(String code, String tableId) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.new_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        try (Connection connect = new ConnectionHelper(this).connectionclass()) {
            if (connect != null) {
                // Connected to the database
                System.out.println("Connected to the database");
                Log.e("Erhahfdbror", "Connected to the database");

                // Get the current date and time
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = dateFormat.format(new Date());

                // Create a SQL query to select data from the table where BillStatus is 0
                String selectQuery = "SELECT * FROM TI_BarBillHead WHERE LocationCode = ? AND TableId = ? AND BillStatus = 0";

                try (PreparedStatement preparedStatement = connect.prepareStatement(selectQuery)) {
                    // Set the parameter for BillStatus to 0
                    preparedStatement.setString(1, code);
                    preparedStatement.setString(2, tableId);
                    // Log the executed query for debugging
                    Log.e("Erhahfdbror", "Executing query: " + preparedStatement.toString());
                    // Execute the query
                    ResultSet resultSet = preparedStatement.executeQuery();
                    // Process the results
                    while (resultSet.next()) {

                        // Retrieve data from each row
                        String BillNo = resultSet.getString("BillNo");
                        String BillDate = resultSet.getString("BillDate");
                        MemberId = resultSet.getString("MemberId");
                        OpeningBalance = resultSet.getString("OpeningBalance");
                        // Extract only the date part
                        BillDate = BillDate.split(" ")[0]; // Assuming the date format is consistent
                        // Log the retrieved data for debugging
                        Log.e("Erhahfdbror", "BillNo: " + BillNo + ", BillDate: " + BillDate);
                        // Check if the BillDate matches the current date
                        if (BillDate.equals(currentDate)) {
                            progressDialog.dismiss();
                            BillNoFinal = BillNo;
                            OpeningBalanceFinal = OpeningBalance;
                            // Log the final BillNo before calling getBarBillBody
                            Log.e("Erhahfdbror", "Matching BillNo found: " + BillNoFinal + " " + OpeningBalanceFinal + " " + MemberId);
                            // Call getBarBillBody

                            getCardData(MemberId);
                            getBarBillBody(BillNoFinal);

                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.e("Erhahfdbror", "SQLException: " + e.getMessage());
                }
            } else {
                // Connection failed
                System.out.println("Failed to connect to the database");
                Log.e("Erhahfdbror", "Failed to connect to the database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("Erhahfdbror", "SQLException while retrieving data: " + e.getMessage());
        }
    }


    public void cardAmountData(String uservalue) {

        ConnectionHelper connectionHelper = new ConnectionHelper(this);
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {

            Log.e("gsvjgdv", "Connected to the database");

            try {

                String query = "SELECT couponno, amount FROM TM_IssueCoupon WHERE memberidno " +
                    "= '" + uservalue + "'";

                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);

                // Check if the result set is empty
                if (!rs.next()) {
                    // No data found
                    //                    tvInformation.setText("Card Amount  not exist in DataBase");
//                    tvInformation.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Card not exist in CArd Amount DataBase", Toast.LENGTH_SHORT).show();
                } else {

                    do {
                        String counponno = rs.getString("couponno"); // Corrected column name
                        cardAmount = rs.getString("amount");

                        if (counponno != null && cardAmount != null) {
                            Log.e("gsvjgdv", "Data found: " + counponno + " " + cardAmount);

                        } else {
                            Log.e("gsvjgdv", "Incomplete data for this row");
                        }
                    } while (rs.next());
                }

                connect.close();
            } catch (Exception e) {
                // Handle the exception appropriately
                Log.e("gsvjgdv", "Exception while retrieving data: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Connection failed
            Log.e("gsvjgdv", "Failed to connect to the database");
        }
    }

    public void getCardData(String memberId) {
        ConnectionHelper connectionHelper = new ConnectionHelper(this);
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {
            // Connection successful
            // Proceed with AsyncTask to fetch data from the database
            Log.e("fjgfhfhg", "Connected to the database");

            new FetchCardDataTask().execute(connect, memberId);
        } else {
            // Connection failed
            Log.e("fjgfhfhg", "Failed to connect to the database");

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
                    cardAmountData(MAINID);
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
//            nameFinal = name;
            // Update UI with the fetched data
            if (name != null) {
                runOnUiThread(() -> {
//                    llTableDeatils.setVisibility(View.VISIBLE);
//                    tvMemberId.setText(memberId); // Set memberId to tvMemberId
//                    tvName.setText(name);
                    Log.e("fjgfhfhg", "Connected to the database" + " " + name + " " + MAINID);
                }); // Data found

            } else {
                // No data found
                System.out.println("No data found for memberId: " + memberId);
                // Handle no data found case
            }
        }
    }

    public void getBarBillBody(String billNoFinal) {
        ConnectionHelper connectionHelper = new ConnectionHelper(this);
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {
            // Connection successful
            // Proceed with AsyncTask to fetch data from the database
            Log.e("dhfvdhs", "Connected to the database");

            new FetchBarBillDataTask().execute(connect, billNoFinal);
        } else {
            // Connection failed
            System.out.println("Failed to connect to the database");
            Log.e("dhfvdhs", "Failed to connect to the database");
            // Handle the case where connection failed
        }
    }

    private class FetchBarBillDataTask extends AsyncTask<Object, Void, Pair<Boolean, List<String>>> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(OrderCancel_Activity.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.new_progress);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        @Override
        protected Pair<Boolean, List<String>> doInBackground(Object... objects) {
            Connection connect = (Connection) objects[0];
            String billNoFinal = (String) objects[1];
            List<String> kotNumbers = new ArrayList<>();
            boolean matchFound = false;

            try {
                // Create a SQL query to select data from the table where BillNo matches
                String selectQuery = "SELECT * FROM TI_BarBillBody WHERE BillNo = ?";
                PreparedStatement preparedStatement = connect.prepareStatement(selectQuery);
                preparedStatement.setString(1, billNoFinal);
                ResultSet resultSet = preparedStatement.executeQuery();

                // Get the current date
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = dateFormat.format(new Date());

                // Process the results
                while (resultSet.next()) {
                    String KOTNo = resultSet.getString("KOTNo");
                    String CreationDate = resultSet.getString("CreationDate").split(" ")[0]; // Extracting date part
                    if (CreationDate.equals(currentDate)) {
                        matchFound = true;

                        kotNumbers.add(KOTNo);
                        Log.e("dhfvdhs", "Matching KOTNo: " + KOTNo + " " + kotNumbers);
                    }


                }

                preparedStatement.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return new Pair<>(matchFound, kotNumbers);
        }

        @Override
        protected void onPostExecute(Pair<Boolean, List<String>> result) {
            super.onPostExecute(result);
            boolean matchFound = result.first;
            List<String> kotNumbers = result.second;

            // Dismiss the ProgressDialog
            progressDialog.dismiss();

            if (matchFound) {
                // Process when match found
                runOnUiThread(() -> {
//                    tvKOTNo.setText(String.valueOf(kotNumbers));
                    getBarKOTBody(kotNumbers);

                });

            } else {
                runOnUiThread(() -> {
//                    tv_Information.setText("Bill not available for this table");
                });

            }
        }
    }

    public void getBarKOTBody(List<String> KOTNoFinal) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching data...");
        progressDialog.setCancelable(false); // prevent users from closing the dialog
        progressDialog.show();
        progressDialog.setContentView(R.layout.new_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Use a Handler to manage timeout
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
//                Toast.makeText(this, "Data fetch timeout", Toast.LENGTH_SHORT).show();
            }
        }, 2000); // define TIMEOUT_MILLISECONDS as per your requirement

        try (Connection connect = new ConnectionHelper(this).connectionclass()) {
            if (connect != null) {
                // Connected to the database
                Log.d("hergeg", "Connected to the database " + KOTNoFinal);

                // Get the current date
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = dateFormat.format(new Date());

                // Initialize lists outside of the loop
                model_occupiedTable = new ArrayList<>();
                model_kotCancel = new ArrayList<>();
                int i = 0;
                // Create a SQL query to select data from the table where KOTNo matches
                String selectQuery = "SELECT * FROM TI_ChargeableBarKOTBody WHERE KOTNo = ?";

                try (PreparedStatement preparedStatement = connect.prepareStatement(selectQuery)) {
                    for (String KOTNo : KOTNoFinal) {
                        preparedStatement.setString(1, KOTNo);
                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                            // Process the results
                            while (resultSet.next()) {
                                String CreationDate = resultSet.getString("CreationDate").split(" ")[0]; // Extracting date part
                                if (CreationDate.equals(currentDate)) {
                                    String ItemName = resultSet.getString("ItemName");
                                    String Rate = resultSet.getString("Rate");
                                    String Qty = resultSet.getString("Qty");
                                    String itemcode = resultSet.getString("itemcode");
                                    String GST = resultSet.getString("GST");
                                    i++;
                                    Model_OccupiedTable menu = new Model_OccupiedTable(ItemName, Rate, Qty, GST, itemcode);
                                    model_occupiedTable.add(menu);
                                    Model_KOTCancel menu1 = new Model_KOTCancel(KOTNo, i);
                                    model_kotCancel.add(menu1);
                                    Log.e("hergeg", "ItemName: " + ItemName +
                                        ", Rate: " + Rate + ", Qty: " + Qty +
                                        ", GST: " + GST +
                                        ", itemcode: " + itemcode);

                                }

                            }
                        }
                    }

                    // Set adapters after processing all KOT numbers
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        rvOrderCancel.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        adapter_orderCancel = new Adapter_OrderCancel(getApplicationContext(), model_occupiedTable);
                        rvOrderCancel.setAdapter(adapter_orderCancel);

                        rvKOTNoCancel.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        adapter_kotCancel = new Adapter_KOTCancel(getApplicationContext(), model_kotCancel);
                        rvKOTNoCancel.setAdapter(adapter_kotCancel);

                    });
                    if (model_occupiedTable != null) {
                        for (Model_OccupiedTable item : model_occupiedTable) {
                            Log.d("Model_OccupiedTable", "ItemName: " + item.getIteName() +
                                ", Rate: " + item.getItePrice() +
                                ", Qty: " + item.getIteQnt() +
                                ", GST: " + item.getItemSaletaxcode() +
                                ", itemcode: " + item.getItemCode());
                            //                            itemCodeList.add(item.getItemCode());
                            double amount = 0.0, amount1 = 0.0, totalAmountFinal = 0.0;
                            double total = Double.parseDouble(item.getItePrice()) *
                                Double.parseDouble(item.getIteQnt());
                            totalSp += total; // Accumulate the sp values


                        }

                    } else {
                        Log.d("Model_OccupiedTable", "model_occupiedTable is null");
                    }


                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.e("hergeg", "SQL Exception while executing query: " + e.getMessage());
                    progressDialog.dismiss(); // Dismiss the dialog in case of SQL exception
                }
            } else {
                // Connection failed
                Log.e("hergeg", "Failed to connect to the database");
                progressDialog.dismiss(); // Dismiss the dialog if connection fails
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("hergeg", "Exception while retrieving data: " + e.getMessage());
            progressDialog.dismiss(); // Dismiss the dialog in case of SQL exception
        }
    }


}
