package de.syss.MifareClassicTool;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.zcs.sdk.DriverManager;
import com.zcs.sdk.Printer;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.print.PrnStrFormat;
import com.zcs.sdk.print.PrnTextFont;
import com.zcs.sdk.print.PrnTextStyle;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.syss.MifareClassicTool.Activities.MainMenu;
import de.syss.MifareClassicTool.Room.CountDao;
import de.syss.MifareClassicTool.Room.CountDatabase;
import de.syss.MifareClassicTool.Room.Product;
import de.syss.MifareClassicTool.Room.ProductDao;
import de.syss.MifareClassicTool.Room.ProductDatabase;

public class Bill_Activity extends AppCompatActivity {
    ImageView imgBack;
    RelativeLayout rlPaid;
    LinearLayout llBill;
    RecyclerView rvBill;
    TextView tvtablenNoofBill, tvGrandTotal, tvTotalOfBill, tvTotalOfGovTx, tvToPay, tvCardHolderName;
    String userValue, locationName, tableId, code, counting, Aid, cardAmount, kqntValue, currentDateTime, currentYear, waiterID;
    Adapter_Bill adapter_bill;
    List<Product> products = new ArrayList<>();

    int txcode, txquantity, exitCount = 0;


    String zeroRowData, firstRowData, secondRowData, thirdRowData, fourthRowData, fifthRowData,
        sixthRowData, seventhRowData, eigthRowData, nenthRowData;
    String taxCode0, taxCode1, taxCode2, taxCode3, taxCode4, taxCode5, taxCode6, taxCode7, taxCode8, taxCode9;
    String taxName0, taxName1, taxName2, taxName3, taxName4, taxName5, taxName6, taxName7, taxName8, taxName9;
    String valuePercentage0, valuePercentage1, valuePercentage2, valuePercentage3, valuePercentage4,
        valuePercentage5, valuePercentage6, valuePercentage7, valuePercentage8, valuePercentage9;
    String[] part0, parts1, parts2, parts3, parts4, parts5, parts6, parts7, parts8, parts9;
    double totalAmount = 0.0, GrandTotal = 0.0, totalAmount1, totalAmount2, totalAmount3, totalAmount4, totalAmount5, totalAmount6, totalAmount7,
        totalAmount8, totalAmount9;
    String spString, name, memberID;
    double spValue;
    private com.zcs.sdk.DriverManager mDriverManager;
    private Printer mPrinter;
    BigDecimal roundedTotalAmount, roundedGrand, grandAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        Utils.blackIconStatusBar(Bill_Activity.this, R.color.main);
        init();
        BillData();

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

//        Toast.makeText(Bill_Activity.this,  ""+currentYear, Toast.LENGTH_SHORT).show();

        tvCardHolderName.setText("Hi, " + name);
        tvtablenNoofBill.setText("Table : " + tableId);

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedamount", MODE_PRIVATE);
        cardAmount = sharedPreferences.getString("amount", "");
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.clear();
        myEdit.apply();

        mDriverManager = DriverManager.getInstance();
        mPrinter = mDriverManager.getPrinter();

        ProductDatabase db = Room.databaseBuilder(getApplicationContext(),
            ProductDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();
        List<Product> allProducts = productDao.getallproduct();
        products.addAll(allProducts);
        List<Product> filteredProducts = new ArrayList<>();
        List<String> saleTaxCodeList = new ArrayList<>(); // ArrayList to store SaleTaxCode values

        double totalSP = 0.0;

        for (Product product : allProducts) {
            if (product.getQnt() >= 1) {
                filteredProducts.add(product);

                totalSP += product.getSp() * product.getQnt();

                // Log the saletaxcode value for each product
                Log.e("SaleTaxCode", "Product Qnt: " + product.getQnt() + ", SaleTaxCode: " + product.getSaletaxcode());

                // Store SaleTaxCode value in the ArrayList
                saleTaxCodeList.add(String.valueOf(product.getQnt() + " " + product.getSaletaxcode() + " " + product.getSp()));

            }
        }
        Log.e("Totalamount", String.valueOf(totalSP));

        rvBill.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter_bill = new Adapter_Bill(getApplicationContext(), filteredProducts, tvTotalOfBill);
        rvBill.setAdapter(adapter_bill);

        if (adapter_bill.getItemCount() == 0) {
            llBill.setVisibility(View.GONE);
            rlPaid.setVisibility(View.GONE);
        }


        for (int i = 0; i < saleTaxCodeList.size(); i++) {
            String saleTaxCodeString = saleTaxCodeList.get(i);
            Log.e("SaleTaxCodeString", saleTaxCodeString);

            String[] parts = saleTaxCodeString.split(" ");
            if (parts.length == 3) {
                txquantity = Integer.parseInt(parts[0]);
                txcode = Integer.parseInt(parts[1]);
                spString = (parts[2]);

                // Now you can use 'quantity' and 'tax' as needed
                Log.e("Quantity", String.valueOf(txquantity));
                Log.e("Tax", String.valueOf(txcode));
                Log.e("spString", String.valueOf(spString));

                spValue = Double.parseDouble(spString);

                // Calculate totalAmount based on txcode
                switch (txcode) {
                    case 0:
                        totalAmount += txquantity * ((spValue * 0.00) / 100.0);
                        break;
                    case 2:
                        totalAmount += txquantity * ((spValue * 0.00) / 100.0);
                        break;
                    case 3:
                        totalAmount += txquantity * ((spValue * 0.00) / 100.0);
                        break;
                    case 5:
                        totalAmount += txquantity * ((spValue * 12.50) / 100.0);
                        break;
                    case 6:
                        totalAmount += txquantity * ((spValue * 5.00) / 100.0);
                        break;
                    case 7:
                        totalAmount += txquantity * ((spValue * 13.13) / 100.0);
                        break;
                    case 8:
                        totalAmount += txquantity * ((spValue * 18.00) / 100.0);
                        break;
                    case 9:
                        totalAmount += txquantity * ((spValue * 12.00) / 100.0);
                        break;
                    case 10:
                        totalAmount += txquantity * ((spValue * 28.00) / 100.0);
                        break;
                    case 11:
                        totalAmount += txquantity * ((spValue * 12.00) / 100.0);
                        break;
                    default:
                        // Handle default case
                        break;
                }
            }
        }

        Log.e("TotalAmount", String.valueOf(totalAmount));

        roundedTotalAmount = new BigDecimal(totalAmount).setScale(2, RoundingMode.HALF_UP);
        tvTotalOfGovTx.setText("\u20B9 " + String.valueOf(roundedTotalAmount));
        Log.e("FinalRoundedTotalAmount", String.valueOf(roundedTotalAmount));
        BigDecimal roundedtotalSP = new BigDecimal(totalSP).setScale(2, RoundingMode.HALF_UP);
        // Initialize the totalSP variable

        GrandTotal = Double.parseDouble(String.valueOf(roundedTotalAmount)) + totalSP;

// Use roundedTotalAmount in the following lines instead of totalAmount
        roundedGrand = new BigDecimal(String.valueOf(GrandTotal)).setScale(2, RoundingMode.HALF_UP);
        tvToPay.setText("\u20B9 " + roundedGrand);
        tvGrandTotal.setText("\u20B9 " + roundedGrand);

        double cardAmountValue = Double.parseDouble(cardAmount);

        totalAmount1 = Double.valueOf(cardAmountValue - GrandTotal);
        Log.e("FinalcardAmount", String.valueOf(totalAmount1));

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rlPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomAlertDialog();

            }
        });

    }

    void init() {
        imgBack = findViewById(R.id.imgBack);
        rlPaid = findViewById(R.id.rlPaid);
        tvtablenNoofBill = findViewById(R.id.tvtablenNoofBill);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);
        tvTotalOfBill = findViewById(R.id.tvTotalOfBill);
        tvTotalOfGovTx = findViewById(R.id.tvTotalOfGovTx);
        tvToPay = findViewById(R.id.tvToPay);
        tvCardHolderName = findViewById(R.id.tvCardHolderName);
        rvBill = findViewById(R.id.rvBill);
        llBill = findViewById(R.id.llBill);

    }

    public void BillData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.new_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {
            System.out.println("Connected to the database");
            Log.e("kjkjkj", "Connected to the database");

            try {
                String query = "SELECT * FROM TM_TaxMaster";

                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);

                // List to store each row's information
                List<String> rowDataList = new ArrayList<>();

                while (rs.next()) {
                    progressDialog.dismiss();

                    // Extract values for each row
                    String taxCode = rs.getString("TaxCode");
                    String taxName = rs.getString("TaxName");
                    double valuePercentage = rs.getDouble("ValuePercentage");

                    // Create a string for the current row's information
                    String rowData = "TaxCode: " + taxCode + " TaxName: " + taxName + " ValuePercentage: " + valuePercentage;

                    // Log the data for each row
                    Log.e("DataofBillString", rowData);

                    // Add the current row's information to the list
                    rowDataList.add(rowData);
                }
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

    void databaseData() {
        SharedPreferences sharedPreferences1 = getSharedPreferences("KotDetails", MODE_PRIVATE);
        Aid = sharedPreferences1.getString("Aid", "");


        SharedPreferences sharedPreferences = getSharedPreferences("Details", MODE_PRIVATE);
        counting = sharedPreferences.getString("counting", "");

//        Toast.makeText(this, "A " + Aid + " ,C " + counting, Toast.LENGTH_SHORT).show();

        CountDatabase db = Room.databaseBuilder(getApplicationContext(),
            CountDatabase.class, "count_db").allowMainThreadQueries().build();
        CountDao countDao = db.CountDao();

        if (Aid != null && !Aid.isEmpty()) {
            // Assuming Aid is the value you are looking for
            kqntValue = countDao.getKqntByAid(Aid);

            if (kqntValue != null) {
                Toast.makeText(this, "Kqnt for Aid " + Aid + ": " + kqntValue, Toast.LENGTH_SHORT).show();
                printText();
                countDao.deleteById(Integer.parseInt(Aid));
            } else {

                Toast.makeText(this, "No data found for Aid " + Aid, Toast.LENGTH_SHORT).show();
            }

        } else if (counting != null && !counting.isEmpty()) {
            kqntValue = countDao.getKqntByAid(counting);

            if (kqntValue != null) {
                Toast.makeText(this, "Kqnt for counting " + counting + ": " + kqntValue, Toast.LENGTH_SHORT).show();
                printText();
                countDao.deleteById(Integer.parseInt(counting));
                // Do something with kqntValue, for example, display it or use it as needed
            } else {
                // Handle the case when no matching record is found
                Toast.makeText(this, "No data found for counting " + counting, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "First Print KOT", Toast.LENGTH_SHORT).show();
        }

    }

    void databaseDataClear() {
        SharedPreferences sharedPreferences1 = getSharedPreferences("KotDetails", MODE_PRIVATE);

        SharedPreferences.Editor myEdit1 = sharedPreferences1.edit();
        myEdit1.clear();
        myEdit1.apply();

        SharedPreferences sharedPreferences = getSharedPreferences("Details", MODE_PRIVATE);

        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.clear();
        myEdit.apply();


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
            mPrinter.setPrintAppendString("Bill No.    : " + kqntValue, format);
            mPrinter.setPrintAppendString("Location    : " + locationName, format);
            mPrinter.setPrintAppendString("MEMBER NAME : " + name, format);
            mPrinter.setPrintAppendString("MERCHANT ID : " + memberID, format);
            mPrinter.setPrintAppendString(" ", format);

            mPrinter.setPrintAppendString("Item Name & Quantity        " + "       Price", format);
            format.setAli(Layout.Alignment.ALIGN_NORMAL);
            format.setTextSize(25);
            format.setStyle(PrnTextStyle.NORMAL);
            ProductDatabase db = Room.databaseBuilder(getApplicationContext(),
                ProductDatabase.class, "cart_db").allowMainThreadQueries().build();
            ProductDao productDao = db.ProductDao();
            List<Product> allProducts = productDao.getallproduct();
            double sum = 0;
            List<Product> filteredProducts = new ArrayList<>();

            for (Product product : allProducts) {
                if (product.getQnt() >= 1) {
                    filteredProducts.add(product);
                    sum = sum + (product.getSp() * product.getQnt());
                    mPrinter.setPrintAppendString(product.getItemName() + " - " + product.getQnt() + "pc        " + ("\u20B9 " + product.getSp() * product.getQnt()), format);
                }
            }
            mPrinter.setPrintAppendString(" ", format);
            format.setTextSize(30);
            format.setStyle(PrnTextStyle.NORMAL);
            format.setAli(Layout.Alignment.ALIGN_NORMAL);
            mPrinter.setPrintAppendString("Item Total :                " + sum, format);
            mPrinter.setPrintAppendString("Govt Taxes :                " + roundedTotalAmount, format);
            mPrinter.setPrintAppendString(" ", format);
            mPrinter.setPrintAppendString("To Pay :                    " + GrandTotal, format);
            mPrinter.setPrintAppendString(" ", format);
            mPrinter.setPrintAppendString("Amount Before :  " + cardAmount, format);
            mPrinter.setPrintAppendString("Amount After  :  " + totalAmount1, format);
            format.setAli(Layout.Alignment.ALIGN_NORMAL);
            format.setStyle(PrnTextStyle.NORMAL);
            format.setTextSize(25);
            mPrinter.setPrintAppendString(" ----------------------------- ", format);
            mPrinter.setPrintAppendString(" ", format);
            mPrinter.setPrintAppendString(" ", format);
            mPrinter.setPrintAppendString(" ", format);
            mPrinter.setPrintAppendString(" ", format);
            printStatus = mPrinter.setPrintStart();

//            Toast.makeText(this, "Kqnt  " + kqntValue, Toast.LENGTH_SHORT).show();

            db.clearAllTables();
            databaseDataClear();
            UpdateBillAmount();
            insertBillData();

            exitCount = 1;
        }
    }

    private void showCustomAlertDialog() {

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.exit_custom_layout, null);

        RelativeLayout rlCancel = dialogView.findViewById(R.id.rlCancel);
        RelativeLayout rlPrint = dialogView.findViewById(R.id.rlPrint);
        RelativeLayout rlExit = dialogView.findViewById(R.id.rlExit);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();


        rlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });

        rlPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                databaseData();
            }
        });
        rlExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exitCount == 0) {
                    Toast.makeText(Bill_Activity.this, "First Print the Bill Receipt", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Bill_Activity.this, MainMenu.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

        alertDialog.show();
    }


    public void UpdateBillAmount() {
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

                // Create and execute the SQL update statement
                Statement stmt = connect.createStatement();
                String updateQuery = "UPDATE [DSOI].[dbo].[TM_IssueCoupon] " +
                    "SET [amount] = " + totalAmount1 + " " +
                    "WHERE [memberidno] = '" + memberID + "'";
                int rowsAffected = stmt.executeUpdate(updateQuery);


                if (rowsAffected > 0) {
                    System.out.println(rowsAffected + " rows updated successfully");
                    Log.e("kjkjkj", rowsAffected + " rows updated successfully");
                } else {
                    System.out.println("No rows updated");
                    Log.e("kjkjkj", "No rows updated");
                }
                progressDialog.dismiss();
                // Close the statement and connection
                stmt.close();
                connect.close();
            } catch (SQLException e) {
                // Handle SQL exceptions
                e.printStackTrace();
                System.out.println("Failed to execute SQL update");
                Log.e("kjkjkj", "Failed to execute SQL update");
            }

        } else {
            // Connection failed
            System.out.println("Failed to connect to the database");
            Log.e("kjkjkj", "Failed to connect to the database");
        }
    }

    public void insertBillData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.new_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        try (Connection connect = new ConnectionHelper().connectionclass()) {
            if (connect != null) {
                // Connected to the database
                System.out.println("Connected to the database");
                Log.e("ergeg", "Connected to the database");

                // Create a SQL query to insert data into the table
                // Create a SQL query to insert data into the table
                String insertQuery = "INSERT INTO TI_BarBillHead (BillNo, LocationCode, MemberId," +
                    " TableId, YearCode, MemberType, WaiterId, UserCode, BillDate, CreationDate, ModificationDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement preparedStatement = connect.prepareStatement(insertQuery)) {
                    // Set values for the parameters
//                    preparedStatement.setString(1, String.valueOf(counting));
                    preparedStatement.setString(1, kqntValue);
                    preparedStatement.setString(2, code);
                    preparedStatement.setString(3, memberID);
                    preparedStatement.setString(4, tableId);
                    preparedStatement.setInt(5, Integer.parseInt(currentYear));

                    // Replace 'YourMemberTypeValue' with the appropriate value for MemberType
                    preparedStatement.setString(6, "Member");

                    preparedStatement.setString(7, waiterID);
                    preparedStatement.setString(8, "1");
                    preparedStatement.setString(9, currentDateTime);
                    preparedStatement.setString(10, currentDateTime);
                    preparedStatement.setString(11, currentDateTime);

                    // Execute the update
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        progressDialog.dismiss();
                        System.out.println("Data inserted successfully");
                        Log.e("ergeg", "Data inserted successfully");
                    } else {
                        System.out.println("Failed to insert data");
                        Log.e("ergeg", "Failed to insert data");
                    }
                } catch (SQLException e) {
                    // Handle SQL exceptions
                    e.printStackTrace();
                    System.out.println("Failed to execute SQL update");
                    Log.e("ergeg", "Failed to insert data. Error: " + e.getMessage());
                }
            } else {
                // Connection failed
                System.out.println("Failed to connect to the database");
                Log.e("ergeg", "Failed to connect to the database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
