package de.syss.MifareClassicTool;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.zcs.sdk.DriverManager;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.print.PrnStrFormat;
import com.zcs.sdk.print.PrnTextFont;
import com.zcs.sdk.print.PrnTextStyle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.zcs.sdk.Printer;

import de.syss.MifareClassicTool.Room.Count;
import de.syss.MifareClassicTool.Room.CountDao;
import de.syss.MifareClassicTool.Room.CountDatabase;
import de.syss.MifareClassicTool.Room.Product;
import de.syss.MifareClassicTool.Room.ProductDao;
import de.syss.MifareClassicTool.Room.ProductDatabase;

public class KOT_Activity extends AppCompatActivity implements Adapter_KOT.ProductClick {

    RecyclerView rvItemofKOT;
    ImageView imgBack;
    LinearLayout llKOT;
    TextView tvLocationKOT, tvTotalBottom, tvTotal, tvTableno, tvKOTnumber, tvCardHolderName;
    RelativeLayout rlPrint;
    String userValue, locationName, tableId, name, memberID, code, kotNUmber,currentDateTime,currentYear,waiterID;
    Adapter_KOT adapter_kot;
    List<Product> products = new ArrayList<>();
    private com.zcs.sdk.DriverManager mDriverManager;
    private Printer mPrinter;

    int counting = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kot);
        Utils.blackIconStatusBar(KOT_Activity.this, R.color.main);
        init();

        Intent intent = getIntent();
        userValue = intent.getStringExtra("userValue");
        locationName = intent.getStringExtra("locationName");
        tableId = intent.getStringExtra("tableId");
        name = intent.getStringExtra("name");
        memberID = intent.getStringExtra("memberID");
        code = intent.getStringExtra("code");
        currentDateTime = intent.getStringExtra("currentDateTime");
        currentYear = intent.getStringExtra("currentYear");
        waiterID = intent.getStringExtra("waiterID");
//        Toast.makeText(KOT_Activity.this,  ""+currentYear, Toast.LENGTH_SHORT).show();

        tvCardHolderName.setText("Hi, " + name);
        tvLocationKOT.setText(locationName);
        tvTableno.setText("Table : " + tableId);
         mDriverManager = DriverManager.getInstance();
        mPrinter = mDriverManager.getPrinter();

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        counting = preferences.getInt("counting", 1);

        SharedPreferences sharedPreferences = getSharedPreferences("Details", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("counting", String.valueOf(counting));
        myEdit.apply();
        myEdit.commit();
        tvKOTnumber.setText(String.valueOf(counting));

        handleCode(Integer.parseInt(code));


        ProductDatabase db = Room.databaseBuilder(getApplicationContext(),
            ProductDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();
        List<Product> allProducts = productDao.getallproduct();
        products.addAll(allProducts);
        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : allProducts) {
            if (product.getQnt() >= 1) {
                filteredProducts.add(product);
            }
        }
        rvItemofKOT.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter_kot = new Adapter_KOT(getApplicationContext(), filteredProducts, tvTotalBottom, tvTotal);
        rvItemofKOT.setAdapter(adapter_kot);
        if (adapter_kot.getItemCount() == 0) {
            llKOT.setVisibility(View.GONE);
            rlPrint.setVisibility(View.GONE);

        }
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rlPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printText();
                insertKotData();
                CountDatabase dbC = Room.databaseBuilder(getApplicationContext(),
                    CountDatabase.class, "count_db").allowMainThreadQueries().build();
                CountDao countDao = dbC.CountDao();
                Boolean check = countDao.is_exist(counting);
                String Tid = String.valueOf(countDao.is_exist(Integer.valueOf(tableId)));
                String Lid = String.valueOf(countDao.is_exist(Integer.valueOf(code)));
                if (Lid.matches(code)) {
                    if (Tid.matches(tableId)) {
                        counting++;
                        countDao.updateRecord(counting, counting, counting);
                        Toast.makeText(getApplicationContext(), "Updated Upper", Toast.LENGTH_SHORT).show();

                    } else {
                        if (!check) {
                            SharedPreferences sharedPreferences = getSharedPreferences("Details", MODE_PRIVATE);
                            SharedPreferences.Editor myEdit = sharedPreferences.edit();
                            myEdit.putString("counting", String.valueOf(counting));
                            myEdit.apply();
                            myEdit.commit();

                            countDao.insertrecord(new Count(counting, kotNUmber, counting, Integer.valueOf(tableId), Integer.valueOf(code)));
                            counting++;
//                            Toast.makeText(getApplicationContext(), "Added to List Successfully Upper", Toast.LENGTH_SHORT).show();
                        } else {
                            counting++;
                            countDao.updateRecord(counting, counting, counting);
//                            Toast.makeText(getApplicationContext(), "Product Already in List Upper", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (Tid.matches(tableId)) {
                        counting++;
                        countDao.updateRecord(counting, counting, counting);
                        Toast.makeText(getApplicationContext(), "Updated Lower", Toast.LENGTH_SHORT).show();

                    } else {
                        if (!check) {
                            SharedPreferences sharedPreferences = getSharedPreferences("Details", MODE_PRIVATE);
                            SharedPreferences.Editor myEdit = sharedPreferences.edit();
                            myEdit.putString("counting", String.valueOf(counting));
                            myEdit.apply();
                            myEdit.commit();

                            countDao.insertrecord(new Count(counting, kotNUmber, counting, Integer.valueOf(tableId), Integer.valueOf(code)));
                            counting++;
//                            Toast.makeText(getApplicationContext(), "Added to List Successfully Lower", Toast.LENGTH_SHORT).show();
                        } else {
                            counting++;
                            countDao.updateRecord(counting, counting, counting);
//                            Toast.makeText(getApplicationContext(), "Product Already in List Lower", Toast.LENGTH_SHORT).show();
                        }
                    }


                }


                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("counting", counting);
                editor.apply();


            }
        });

    }

    void init() {
        imgBack = findViewById(R.id.imgBack);
        rlPrint = findViewById(R.id.rlPrint);
        tvLocationKOT = findViewById(R.id.tvLocationKOT);
        tvTotalBottom = findViewById(R.id.tvTotalBottom);
        tvTotal = findViewById(R.id.tvTotal);
        tvTableno = findViewById(R.id.tvTableno);
        tvKOTnumber = findViewById(R.id.tvKOTnumber);
        tvCardHolderName = findViewById(R.id.tvCardHolderName);
        rvItemofKOT = findViewById(R.id.rvItemofKOT);
        llKOT = findViewById(R.id.llKOT);

    }


    private void handleCode(int code) {
        switch (code) {
            case 18:

                kotNUmber = "BCHo-" + counting;
//                Toast.makeText(KOT_Activity.this, "" + kotNUmber, Toast.LENGTH_SHORT).show();
                break;
            case 8:

                kotNUmber = "100P-" + counting;
//                Toast.makeText(KOT_Activity.this, "" + kotNUmber, Toast.LENGTH_SHORT).show();
                break;
            case 9:

                kotNUmber = "AbsB-" + counting;
//                Toast.makeText(KOT_Activity.this, "" + kotNUmber, Toast.LENGTH_SHORT).show();
                break;
            case 10:

                kotNUmber = "SalP-" + counting;
//                Toast.makeText(KOT_Activity.this, "" + kotNUmber, Toast.LENGTH_SHORT).show();
                break;
            case 11:

                kotNUmber = "FroF-" + counting;
//                Toast.makeText(KOT_Activity.this, "" + kotNUmber, Toast.LENGTH_SHORT).show();
                break;
            case 12:

                kotNUmber = "Zaya-" + counting;
//                Toast.makeText(KOT_Activity.this, "" + kotNUmber, Toast.LENGTH_SHORT).show();
                break;
            case 13:
                kotNUmber = "SRRe-" + counting;
//                Toast.makeText(KOT_Activity.this, "" + kotNUmber, Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(KOT_Activity.this, "Empty COde " , Toast.LENGTH_SHORT).show();

                break;
        }
    }

    private void printText() {
        int printStatus = mPrinter.getPrinterStatus();
        if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
            //out of paper
            Toast.makeText(getApplicationContext(), "Out of paper", Toast.LENGTH_SHORT).show();
        } else {
            PrnStrFormat format = new PrnStrFormat();
            format.setTextSize(30);
            format.setAli(Layout.Alignment.ALIGN_CENTER);
            format.setStyle(PrnTextStyle.BOLD);
            format.setFont(PrnTextFont.SANS_SERIF);
            mPrinter.setPrintAppendString("Defence Services Officers Institute", format);

            format.setTextSize(25);
            format.setStyle(PrnTextStyle.NORMAL);
            format.setAli(Layout.Alignment.ALIGN_NORMAL);
            mPrinter.setPrintAppendString(" ", format);
            mPrinter.setPrintAppendString("KOT No.     : " + counting, format);
            mPrinter.setPrintAppendString("Table No.   : " + tableId, format);
            mPrinter.setPrintAppendString(" ", format);
            mPrinter.setPrintAppendString("MEMBER NAME : " + name, format);
            mPrinter.setPrintAppendString("MERCHANT ID : " + memberID, format);
            mPrinter.setPrintAppendString(" ", format);
            mPrinter.setPrintAppendString("Item Name                " + "            Quantity", format);
            format.setTextSize(25);
            format.setStyle(PrnTextStyle.NORMAL);
            format.setAli(Layout.Alignment.ALIGN_NORMAL);
            ProductDatabase db = Room.databaseBuilder(getApplicationContext(),
                ProductDatabase.class, "cart_db").allowMainThreadQueries().build();
            ProductDao productDao = db.ProductDao();
            List<Product> allProducts = productDao.getallproduct();
            int sum = 0;
            List<Product> filteredProducts = new ArrayList<>();
            for (Product product : allProducts) {
                if (product.getQnt() >= 1) {
                    filteredProducts.add(product);
                    sum = sum + product.getQnt();
                    mPrinter.setPrintAppendString(product.getItemName() + "      -      " + product.getQnt() + "pc", format);
                }
            }
            format.setTextSize(25);
            format.setStyle(PrnTextStyle.NORMAL);
            format.setAli(Layout.Alignment.ALIGN_NORMAL);
            mPrinter.setPrintAppendString(" ", format);
            mPrinter.setPrintAppendString("Total Quantity     :       " + sum + "pcs", format);
            mPrinter.setPrintAppendString(" ----------------------------- ", format);
            mPrinter.setPrintAppendString(" ", format);
            mPrinter.setPrintAppendString(" ", format);
            mPrinter.setPrintAppendString(" ", format);
            mPrinter.setPrintAppendString(" ", format);
            printStatus = mPrinter.setPrintStart();
        }
    }

    public void insertKotData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.new_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {
            try {
                // Connected to the database
                System.out.println("Connected to the database");
                Log.e("kjkjkj", "Connected to the database");

                // Create a SQL query to insert data into the table, including the 'UserCode' column
                String insertQuery = "INSERT INTO TI_ChargeableBarKOThead (KOTNo, LocationCode, MemberId," +
                    " TableNo, YearCode, CashierCode, WaiterId, UserCode,KOTDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";

// Create a PreparedStatement to execute the query
                PreparedStatement preparedStatement = connect.prepareStatement(insertQuery);

// Set values for the parameters (replace these with your actual values)
                preparedStatement.setString(1, String.valueOf(counting));
                preparedStatement.setString(2, code);
                preparedStatement.setString(3, memberID);
                preparedStatement.setString(4, tableId);

// Set non-null values for 'YearCode', 'CashierCode', and 'WaiterId' (replace 2024, 'YourCashierCodeValue', and 'YourWaiterIdValue' with your actual values)
                preparedStatement.setInt(5, Integer.parseInt(currentYear));
                preparedStatement.setString(6, "33");
                preparedStatement.setString(7, waiterID);
                preparedStatement.setString(8, "1");
                preparedStatement.setString(9, currentDateTime);
// Execute the update
                int rowsAffected = preparedStatement.executeUpdate();


                if (rowsAffected > 0) {
                    progressDialog.dismiss();
                    System.out.println("Data inserted successfully");
                    Log.e("kjkjkj", "Data inserted successfully");
                } else {
                    System.out.println("Failed to insert data");
                    Log.e("kjkjkj", "Failed to insert data");
                }

                // Close the connection and statement
                preparedStatement.close();
                connect.close();
            } catch (SQLException e) {
                // Handle SQL exceptions
                e.printStackTrace();
                System.out.println("Failed to execute SQL update");
                 Log.e("kjkjkj", "Failed to insert data. Error: " + e.getMessage());

            }

        } else {
            // Connection failed
            System.out.println("Failed to connect to the database");
            Log.e("kjkjkj", "Failed to connect to the database");
        }
    }

}
