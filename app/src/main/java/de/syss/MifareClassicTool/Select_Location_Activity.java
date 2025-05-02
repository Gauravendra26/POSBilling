package de.syss.MifareClassicTool;


import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Select_Location_Activity extends AppCompatActivity {
    ImageView imgBack;
    int totalTables = 0;
    LinearLayout rlRestaurant, rlZayaca, rlFrozenFood, rl100Piperbar, rlAbsoluteBar, rlSaltpepper, rlBlueChip, llLocation;
    TextView tvBCHospitality, tvRestaurant, tvSaltPepper, tv100PiperBar, tvZayaca, tvFrozenFood,
        tvConnectionInformation,
        tvAbsoluteBar, tvResTabNumber, tvZayTabNumber, tvFroFTabNumber, tvPiperTabNumber, tvAbsTabNumber,
        tvSaltPTabNumber, tvBCHospitalityTableNo;

    String Value16, Value6, Value7, Value8, Value9, Value10, Value11, code16, code6, code7, code8, code9,
        code10, code11, name, memberID, displayAs;
    List<String> secondLocationDataList18, secondLocationDataList8, secondLocationDataList9, secondLocationDataList10, secondLocationDataList11, secondLocationDataList12, secondLocationDataList13;
    int counting = 1, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        Utils.blackIconStatusBar(this, R.color.main);
        init();
        Intent intent = getIntent();
//        name = intent.getStringExtra("name");
//        memberID = intent.getStringExtra("memberID");



        runOnUiThread(() -> {
//                    tvKOTNo.setText(String.valueOf(kotNumbers));
            LocationData();

        });





//        CountDatabase db = Room.databaseBuilder(getApplicationContext(),
//            CountDatabase.class, "count_db").allowMainThreadQueries().build();
//        CountDao countDao = db.CountDao();
//        List<Count> allProducts = countDao.getdataofallproduct();
//        for (Count product : allProducts) {
//
////            tvCardHolderName.setText(String.valueOf(product.getAid())+" "+String.valueOf(product.getKqnt())+"  "+
////                String.valueOf(product.getBqnt())+" ,T: "+String.valueOf(product.getTid())+" ,L: "+String.valueOf(product.getLocid()));
//            Log.d("Addreessdatabase", "A: "+String.valueOf(product.getAid())+" ,K:"
//                +String.valueOf(product.getKqnt())+" ,S:"
//                +String.valueOf(product.getKqnt1())+" ,Q:"
//                +String.valueOf(product.getKqnt2())+" ,B: "+
//                String.valueOf(product.getBqnt())+" ,T: "+String.valueOf(product.getTid())  +
//                ",L: "+String.valueOf(product.getLocid()) );
////            Toast.makeText(this, ""+product.getFilteredItems(), Toast.LENGTH_SHORT).show();
//        }
        Log.e("adgdsfbdfb",  "code16 "+code16+" code6 "+code6+" code7 "+code7+" code8 "+code8
            +" code9 "+code9+" code10 "+code10+" code11 "+code11 );


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        rlBlueChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Table_Selection_Activity.class);
                intent.putExtra("userValue", "2");
                intent.putExtra("locationName", "Blue chip Hospitality");
                intent.putExtra("code", code16);
//                intent.putStringArrayListExtra("secondLocationDataList", (ArrayList<String>) secondLocationDataList18);
//                Toast.makeText(Select_Location_Activity.this, ""+secondLocationDataList, Toast.LENGTH_SHORT).show();
//                intent.putExtra("name",name);
//                intent.putExtra("memberID",memberID);
                startActivity(intent);

            }
        });
        rlSaltpepper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Table_Selection_Activity.class);
                intent.putExtra("userValue", "1");
                intent.putExtra("locationName", "Salt & Pepper");
//                intent.putExtra("name",name);
//                intent.putExtra("memberID",memberID);
                intent.putExtra("code", code8);
//                intent.putStringArrayListExtra("secondLocationDataList", (ArrayList<String>) secondLocationDataList10);

//                Toast.makeText(Select_Location_Activity.this, ""+Value8, Toast.LENGTH_SHORT).show();
                startActivity(intent);

            }
        });
        rlAbsoluteBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Table_Selection_Activity.class);
                intent.putExtra("userValue", "1");
                intent.putExtra("locationName", "Absolute Bar");
//                intent.putExtra("name",name);
//                intent.putExtra("memberID",memberID);
                intent.putExtra("code", code7);
//                intent.putStringArrayListExtra("secondLocationDataList", (ArrayList<String>) secondLocationDataList9);

                startActivity(intent);

            }
        });
        rl100Piperbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Table_Selection_Activity.class);
                intent.putExtra("userValue", "1");
                intent.putExtra("locationName", "100 Piper Bar");
//                intent.putExtra("name",name);
//                intent.putExtra("memberID",memberID);
                intent.putExtra("code", code6);
//                intent.putStringArrayListExtra("secondLocationDataList", (ArrayList<String>) secondLocationDataList8);

                startActivity(intent);

            }
        });
        rlFrozenFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Table_Selection_Activity.class);
                intent.putExtra("userValue", "2");
                intent.putExtra("locationName", "Frozen Food");
//                intent.putExtra("name",name);
//                intent.putExtra("memberID",memberID);
                intent.putExtra("code", code9);
//                intent.putStringArrayListExtra("secondLocationDataList", (ArrayList<String>) secondLocationDataList11);

                startActivity(intent);

            }
        });
        rlZayaca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Table_Selection_Activity.class);
                intent.putExtra("userValue", "2");
                intent.putExtra("locationName", "Zayaca");
//                intent.putExtra("name",name);
//                intent.putExtra("memberID",memberID);
                intent.putExtra("code", code10);
//                intent.putStringArrayListExtra("secondLocationDataList", (ArrayList<String>) secondLocationDataList12);

                startActivity(intent);

            }
        });
        rlRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Table_Selection_Activity.class);
                intent.putExtra("userValue", "2");
                intent.putExtra("locationName", "Spice Root Restaurant");
//                intent.putExtra("name",name);
//                intent.putExtra("memberID",memberID);
                intent.putExtra("code", code11);
//                intent.putStringArrayListExtra("secondLocationDataList", (ArrayList<String>) secondLocationDataList13);

                startActivity(intent);

            }
        });

    }

    void init() {

        imgBack = findViewById(R.id.imgBack);
        rlRestaurant = findViewById(R.id.rlRestaurant);
        rlZayaca = findViewById(R.id.rlZayaca);
        rlFrozenFood = findViewById(R.id.rlFrozenFood);
        rl100Piperbar = findViewById(R.id.rl100Piperbar);
        rlAbsoluteBar = findViewById(R.id.rlAbsoluteBar);
        rlSaltpepper = findViewById(R.id.rlSaltpepper);
        rlBlueChip = findViewById(R.id.rlBlueChip);
        tvBCHospitality = findViewById(R.id.tvBCHospitality);
        tvRestaurant = findViewById(R.id.tvRestaurant);
        tvSaltPepper = findViewById(R.id.tvSaltPepper);
        tv100PiperBar = findViewById(R.id.tv100PiperBar);
        tvZayaca = findViewById(R.id.tvZayaca);
        tvFrozenFood = findViewById(R.id.tvFrozenFood);
        tvAbsoluteBar = findViewById(R.id.tvAbsoluteBar);
        tvBCHospitalityTableNo = findViewById(R.id.tvBCHospitalityTableNo);
        tvResTabNumber = findViewById(R.id.tvResTabNumber);
        tvZayTabNumber = findViewById(R.id.tvZayTabNumber);
        tvFroFTabNumber = findViewById(R.id.tvFroFTabNumber);
        tvPiperTabNumber = findViewById(R.id.tvPiperTabNumber);
        tvAbsTabNumber = findViewById(R.id.tvAbsTabNumber);
        tvSaltPTabNumber = findViewById(R.id.tvSaltPTabNumber);
        tvConnectionInformation = findViewById(R.id.tvConnectionInformation);
        llLocation = findViewById(R.id.llLocation);


    }




    public void LocationData() {
        ConnectionHelper connectionHelper = new ConnectionHelper(this);
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {
            // Connection successful
            // Proceed with AsyncTask to fetch data from the database
            Log.e("kjkjkj", " connect with database");

            new FetchLocationDataTask().execute(connect);
        } else {
            // Connection failed
            Log.e("kjkjkj", "Failed to connect with database");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvConnectionInformation.setVisibility(View.VISIBLE);
                    tvConnectionInformation.setText("Failed to connect with database");
                }
            });
        }
    }

    private class FetchLocationDataTask extends AsyncTask<Connection, Void, Map<String, List<String>>> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Select_Location_Activity.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.new_progress);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        @Override
        protected Map<String, List<String>> doInBackground(Connection... connections) {
            Connection connect = connections[0];
            Map<String, List<String>> locationDataMap = new HashMap<>();

            try {
                // Fetch data from the database asynchronously
                // Replace this section with your database query logic
                String query = "SELECT * FROM ST_StoreMaster WHERE LocationType = 'POS'";
                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);

                // Process the ResultSet and populate locationDataMap

                while (rs.next()) {
                    String location = rs.getString("storeName");
                    int code = rs.getInt("code");
                    String data = rs.getString("usercode");
                    if (!locationDataMap.containsKey(location)) {
                        locationDataMap.put(location, new ArrayList<String>());
                    }
                    locationDataMap.get(location).add(String.valueOf(code));
                    Log.e("hasdja", location + " " + code);

                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return locationDataMap;
        }

        @Override
        protected void onPostExecute(Map<String, List<String>> locationDataMap) {
            super.onPostExecute(locationDataMap);

            // Process the fetched data and update UI accordingly
            for (Map.Entry<String, List<String>> entry : locationDataMap.entrySet()) {
                String location = entry.getKey();
                List<String> dataList = entry.getValue();

                // Update UI with location and data
                Log.e("LocationData", location + "  " + dataList);

                // Extracting data from the list
                String data = dataList.isEmpty() ? "" : dataList.get(0);
                runOnUiThread(() -> {
                    tvConnectionInformation.setVisibility(View.GONE);
                    progressDialog.dismiss();

                    llLocation.setVisibility(View.VISIBLE);
                    switch (location) {
                        case "Spice Root Restaurant":
                            tvRestaurant.setText(location);
                            code11 = data;
                            LocationTableData(code11);
                            break;
                        case "Zayaca":
                            tvZayaca.setText(location);
                            code10 = data;
                            LocationTableData(code10);
                            break;
                        case "Frozen Food":
                            tvFrozenFood.setText(location);
                            code9 = data;
                            LocationTableData(code9);
                            break;
                        case "100 Piper Bar":
                            tv100PiperBar.setText(location);
                            code6 = data;
                            LocationTableData(code6);
                            break;
                        case "Absolute Bar":
                            tvAbsoluteBar.setText(location);
                            code7 = data;
                            LocationTableData(code7);
                            break;
                        case "Salt & Pepper":
                            tvSaltPepper.setText(location);
                            code8 = data;
                            LocationTableData(code8);
                            break;
                        case "Blue Chips Hospitality Pvt Ltd":
                            StringBuilder sb = new StringBuilder();
                            for (String item : dataList) {
                                sb.append(item).append("\n");
                            }
                            tvBCHospitality.setText("Blue Chips Hospitality Pvt Ltd");
                            code16 = data;
                            LocationTableData(code16);
                            break;
                        // Add more cases for other locations if needed
                        default:
                            // Handle default case if necessary
                            break;
                    }

                });

                }
            Log.e("dataofcode",  "code16 "+code16+" code6 "+code6+" code7 "+code7+" code8 "+code8
                +" code9 "+code9+" code10 "+code10+" code11 "+code11 );

        }

    }


public void LocationTableData(String code) {
    ProgressDialog progressDialog = new ProgressDialog(this);
    progressDialog.show();
    progressDialog.setContentView(R.layout.new_progress);
    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//    Toast.makeText(this, ""+code, Toast.LENGTH_SHORT).show();
    Handler handler = new Handler(Looper.getMainLooper());

    handler.post(new Runnable() {
        @Override
        public void run() {
            ConnectionHelper connectionHelper = new ConnectionHelper(getApplicationContext());
            Connection connect = connectionHelper.connectionclass();

            if (connect != null) {
                progressDialog.dismiss();
                llLocation.setVisibility(View.VISIBLE);
                Log.e("fsdggfsdg", "Connected to the database");

                try {
                    String query = "SELECT * FROM TM_LOCATION_TABLE WHERE Location = ?";
                    PreparedStatement preparedStatement = connect.prepareStatement(query);
                    preparedStatement.setString(1, code);
                    ResultSet rs = preparedStatement.executeQuery();

                    // Check if ResultSet contains any data


                    // Counter variable to keep track of the total number of tables

                    while (rs.next()) {
                        int tableId = rs.getInt("TableId");
                        String location = rs.getString("Location");
                        totalTables++; // Increment the counter for each row processed
                        // Log the TableId and Location
                        Log.d("fsdggfsdg", "TableId: " + tableId + ", Location: " + location + ", Table count: " + totalTables);
                    }

// Log the total number of tables after the loop completes
                    Log.d("fsdggfsdg", "Total number of tables: " + totalTables);
                    runOnUiThread(() -> {
                        switch (code) {
                            case "18":
                                tvBCHospitalityTableNo.setText("No. of Tables: " + totalTables);
                                totalTables=0;
                                break;
                            case "8":
                                tvPiperTabNumber.setText("No. of Tables: " + totalTables);
                                totalTables=0;
                                break;
                            case "9":
                                tvAbsTabNumber.setText("No. of Tables: " + totalTables);
                                totalTables=0;
                                break;
                            case "10":
                                tvSaltPTabNumber.setText("No. of Tables: " + totalTables);
                                totalTables=0;
                                break;
                            case "11":
                                tvFroFTabNumber.setText("No. of Tables: " + totalTables);
                                totalTables=0;
                                break;
                            case "12":
                                tvZayTabNumber.setText("No. of Tables: " + totalTables);
                                totalTables=0;
                                break;
                            case "13":
                                tvResTabNumber.setText("No. of Tables: " + totalTables);
                                totalTables=0;
                                break;
                            default:
                                // Handle default case if necessary
                                break;
                        }

                    });




                    connect.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("fsdggfsdg", "Failed to connect to the database");
                llLocation.setVisibility(View.GONE);
            }
        }
    });
}


}
