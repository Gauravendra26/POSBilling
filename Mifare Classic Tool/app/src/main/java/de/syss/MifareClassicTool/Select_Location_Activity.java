package de.syss.MifareClassicTool;


import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.syss.MifareClassicTool.Room.Count;
import de.syss.MifareClassicTool.Room.CountDao;
import de.syss.MifareClassicTool.Room.CountDatabase;

public class Select_Location_Activity extends AppCompatActivity {
    ImageView imgBack;

    LinearLayout rlRestaurant, rlZayaca, rlFrozenFood, rl100Piperbar, rlAbsoluteBar, rlSaltpepper, rlBlueChip;
    TextView tvBCHospitality, tvRestaurant, tvSaltPepper, tv100PiperBar, tvZayaca, tvFrozenFood,tvCardHolderName,
        tvAbsoluteBar, tvResTabNumber, tvZayTabNumber, tvFroFTabNumber, tvPiperTabNumber, tvAbsTabNumber,
        tvSaltPTabNumber, tvBCHospitalityTableNo;
    String Value16, Value6, Value7, Value8, Value9, Value10, Value11, code16, code6, code7, code8, code9,
        code10, code11,name,memberID,displayAs;
    List<String> secondLocationDataList18, secondLocationDataList8, secondLocationDataList9, secondLocationDataList10, secondLocationDataList11, secondLocationDataList12, secondLocationDataList13;
    int counting = 1,id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        Utils.blackIconStatusBar(  this, R.color.main);
        init();
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        memberID = intent.getStringExtra("memberID");
       tvCardHolderName.setText("Hi, "+name);
        LocationData();
        LocationTableData();

        CountDatabase db = Room.databaseBuilder(getApplicationContext(),
            CountDatabase.class, "count_db").allowMainThreadQueries().build();
        CountDao countDao = db.CountDao();
        List<Count> allProducts = countDao.getdataofallproduct();
        for (Count product : allProducts) {

//            tvCardHolderName.setText(String.valueOf(product.getAid())+" "+String.valueOf(product.getKqnt())+"  "+
//                String.valueOf(product.getBqnt())+" ,T: "+String.valueOf(product.getTid())+" ,L: "+String.valueOf(product.getLocid()));
            Log.d("Addreessdatabase", "A: "+String.valueOf(product.getAid())+" ,K: "+String.valueOf(product.getKqnt())+" ,B: "+
                String.valueOf(product.getBqnt())+" ,T: "+String.valueOf(product.getTid())+" ,L: "+String.valueOf(product.getLocid()));
        }



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
                intent.putStringArrayListExtra("secondLocationDataList", (ArrayList<String>) secondLocationDataList18);
//                Toast.makeText(Select_Location_Activity.this, ""+secondLocationDataList, Toast.LENGTH_SHORT).show();
                intent.putExtra("name",name);
                intent.putExtra("memberID",memberID);
                startActivity(intent);

            }
        });
        rlSaltpepper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Table_Selection_Activity.class);
                intent.putExtra("userValue", "1");
                intent.putExtra("locationName", "Salt & Pepper");
                intent.putExtra("name",name);
                intent.putExtra("memberID",memberID);
                intent.putExtra("code", code8);
                intent.putStringArrayListExtra("secondLocationDataList", (ArrayList<String>) secondLocationDataList10);

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
                intent.putExtra("name",name);
                intent.putExtra("memberID",memberID);
                intent.putExtra("code", code7);
                intent.putStringArrayListExtra("secondLocationDataList", (ArrayList<String>) secondLocationDataList9);

                startActivity(intent);

            }
        });
        rl100Piperbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Table_Selection_Activity.class);
                intent.putExtra("userValue", "1");
                intent.putExtra("locationName", "100 Piper Bar");
                intent.putExtra("name",name);
                intent.putExtra("memberID",memberID);
                intent.putExtra("code", code6);
                intent.putStringArrayListExtra("secondLocationDataList", (ArrayList<String>) secondLocationDataList8);

                startActivity(intent);

            }
        });
        rlFrozenFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Table_Selection_Activity.class);
                intent.putExtra("userValue", "2");
                intent.putExtra("locationName", "Frozen Food");
                intent.putExtra("name",name);
                intent.putExtra("memberID",memberID);
                intent.putExtra("code", code9);
                intent.putStringArrayListExtra("secondLocationDataList", (ArrayList<String>) secondLocationDataList11);

                startActivity(intent);

            }
        });
        rlZayaca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Table_Selection_Activity.class);
                intent.putExtra("userValue", "2");
                intent.putExtra("locationName", "Zayaca");
                intent.putExtra("name",name);
                intent.putExtra("memberID",memberID);
                intent.putExtra("code", code10);
                intent.putStringArrayListExtra("secondLocationDataList", (ArrayList<String>) secondLocationDataList12);

                startActivity(intent);

            }
        });
        rlRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Table_Selection_Activity.class);
                intent.putExtra("userValue", "2");
                intent.putExtra("locationName", "Spice Root Restaurant");
                intent.putExtra("name",name);
                intent.putExtra("memberID",memberID);
                intent.putExtra("code", code11);
                intent.putStringArrayListExtra("secondLocationDataList", (ArrayList<String>) secondLocationDataList13);

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
        tvCardHolderName = findViewById(R.id.tvCardHolderName);

    }

    public void LocationData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.new_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {
            // Connection successful
            System.out.println("Connected to the database");
            Log.e("kjkjkj", "Connected to the database");

            try {
                String query = "SELECT * FROM ST_StoreMaster";

                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);
                for (int i = 0; i < 18 && rs.next(); i++) {
                    // If the fifth row is reached, set data of the 2nd column into tv1
                    if (i == 16) {
                        tvBCHospitality.setText(rs.getString(2));
                        Value16 = rs.getString("Usercode");
                        code16 = rs.getString("code");
                        // Toast.makeText(this, ""+Value16, Toast.LENGTH_SHORT).show();
                    }
                    if (i == 6) {
                        tv100PiperBar.setText(rs.getString(2));
                        Value6 = rs.getString("Usercode");
                        code6 = rs.getString("code");

                    }
                    if (i == 7) {
                        tvAbsoluteBar.setText(rs.getString(2));
                        Value7 = rs.getString("Usercode");
                        code7 = rs.getString("code");

                    }
                    if (i == 8) {
                        tvSaltPepper.setText(rs.getString(2));
                        Value8 = rs.getString("Usercode");
                        code8 = rs.getString("code");

                    }
                    if (i == 9) {
                        tvFrozenFood.setText(rs.getString(2));
                        Value9 = rs.getString("Usercode");
                        code9 = rs.getString("code");

//                        Toast.makeText(this, ""+Value9, Toast.LENGTH_SHORT).show();
                    }
                    if (i == 10) {
                        tvZayaca.setText(rs.getString(2));
                        Value10 = rs.getString("Usercode");
                        code10 = rs.getString("code");

                    }
                    if (i == 11) {
                        tvRestaurant.setText(rs.getString(2));
                        Value11 = rs.getString("Usercode");
                        code11 = rs.getString("code");


                    }
                    progressDialog.dismiss();

                }
Log.e("dataofcode",  "code16 "+code16+" code6 "+code6+" code7 "+code7+" code8 "+code8
    +" code9 "+code9+" code10 "+code10+" code11 "+code11 );
                // Move to the second row

                connect.close();
            } catch (Exception e) {
                // Handle the exception appropriately
                e.printStackTrace();
            }
        } else {
            // Connection failed
            System.out.println();
            Log.e("kjkjkj", "Failed to connect to the database");

        }


    }

    public void LocationTableData() {
        TextView[] locationCountTextViews = new TextView[7];
        locationCountTextViews[0] = findViewById(R.id.tvBCHospitalityTableNo);  // replace with the actual IDs
        locationCountTextViews[1] = findViewById(R.id.tvPiperTabNumber);
        locationCountTextViews[2] = findViewById(R.id.tvAbsTabNumber);
        locationCountTextViews[3] = findViewById(R.id.tvSaltPTabNumber);
        locationCountTextViews[4] = findViewById(R.id.tvFroFTabNumber);
        locationCountTextViews[5] = findViewById(R.id.tvZayTabNumber);
        locationCountTextViews[6] = findViewById(R.id.tvResTabNumber);



        ConnectionHelper connectionHelper = new ConnectionHelper();
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {
            // Connection successful
            Log.e("kjkjkj", "Connected to the database");

            // Specify the locations you are interested in
            String[] targetLocations = {code16, code6, code7, code8, code9, code10, code11};

            try {
                // Use a map to store results for each location
                Map<String, List<String>> locationDataMap = new HashMap<>();

                for (String targetLocation : targetLocations) {
                    // Use a parameterized query to avoid SQL injection
                    String query = "SELECT * FROM TM_LOCATION_TABLE WHERE Location = ?";

                    // Prepare the statement with the parameterized query
                    PreparedStatement preparedStatement = connect.prepareStatement(query);
                    preparedStatement.setString(1, targetLocation);

                    // Execute the query
                    ResultSet rs = preparedStatement.executeQuery();

                    List<String> locationDataList = new ArrayList<>();

                    // Iterate through the result set
                    while (rs.next()) {
                        // Retrieve data as needed
                        int tableId = rs.getInt("TableId");
                        String location = rs.getString("Location");

                        // Store the data in the list
                        locationDataList.add("TableId: " + tableId + ", Location: " + location);
                    }

                    // Store the list of data for the current location in the map
                    locationDataMap.put(targetLocation, locationDataList);


                    // Print or use the total count as needed for each location
                    Log.e("TotalCountForLocation", targetLocation + ": " + locationDataList.size());


// Update the respective TextView with the total count

// ...

// Method to update the respective TextView with the total count

                    int index = -1;

                    // Find the index of the target location in the array
                    for (int i = 0; i < targetLocations.length; i++) {
                        if (targetLocations[i].equals(targetLocation)) {
                            index = i;
                            break;
                        }
                    }

                    // Check if the index is valid
                    if (index != -1 && index < locationCountTextViews.length) {
                        // Update the respective TextView with the total count
                        locationCountTextViews[index].setText("Table : " + locationDataList.size());
                    }

                }


// ...

// Method to update the respective TextView with the total count


                // Print or use the map of location data as needed
                for (Map.Entry<String, List<String>> entry : locationDataMap.entrySet()) {
                    Log.e("LocationData", entry.getKey() + ": " + entry.getValue());
                }
                if (locationDataMap.size() >= 6) {
                    String secondLocation = targetLocations[0]; // assuming "code6" is the second location
                    secondLocationDataList18 = locationDataMap.get(secondLocation);
                    Log.e("LocationData1", secondLocation + ": " + secondLocationDataList18);
                }
                if (locationDataMap.size() >= 6) {
                    String secondLocation = targetLocations[1]; // assuming "code6" is the second location
                    secondLocationDataList8 = locationDataMap.get(secondLocation);
                    Log.e("LocationData2", secondLocation + ": " + secondLocationDataList8);
                }
                if (locationDataMap.size() >= 6) {
                    String secondLocation = targetLocations[2]; // assuming "code6" is the second location
                    secondLocationDataList9 = locationDataMap.get(secondLocation);
                    Log.e("LocationData3", secondLocation + ": " + secondLocationDataList9);
                }
                if (locationDataMap.size() >= 6) {
                    String secondLocation = targetLocations[3]; // assuming "code6" is the second location
                    secondLocationDataList10 = locationDataMap.get(secondLocation);
                    Log.e("LocationData4", secondLocation + ": " + secondLocationDataList10);
                }
                if (locationDataMap.size() >= 6) {
                    String secondLocation = targetLocations[4]; // assuming "code6" is the second location
                    secondLocationDataList11 = locationDataMap.get(secondLocation);
                    Log.e("LocationData5", secondLocation + ": " + secondLocationDataList11);
                }
                if (locationDataMap.size() >= 6) {
                    String secondLocation = targetLocations[5]; // assuming "code6" is the second location
                    secondLocationDataList12 = locationDataMap.get(secondLocation);
                    Log.e("LocationData6", secondLocation + ": " + secondLocationDataList12);
                }
                if (locationDataMap.size() >= 6) {
                    String secondLocation = targetLocations[6]; // assuming "code6" is the second location
                    secondLocationDataList13 = locationDataMap.get(secondLocation);
                    Log.e("LocationData7", secondLocation + ": " + secondLocationDataList13);
                }
                // Close the connection
                connect.close();
            } catch (SQLException e) {
                // Handle the exception appropriately
                e.printStackTrace();
            }
        } else {
            // Connection failed
            Log.e("kjkjkj", "Failed to connect to the database");
        }

    }


}
