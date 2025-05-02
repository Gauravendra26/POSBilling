package de.syss.MifareClassicTool;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.zcs.sdk.Printer;

import de.syss.MifareClassicTool.Activities.MainMenu;
import de.syss.MifareClassicTool.Room.Bill;
import de.syss.MifareClassicTool.Room.BillDao;
import de.syss.MifareClassicTool.Room.BillDatabase;
import de.syss.MifareClassicTool.Room.Product;
import de.syss.MifareClassicTool.Room.ProductDao;
import de.syss.MifareClassicTool.Room.ProductDatabase;

public class KOT_Activity extends AppCompatActivity implements Adapter_KOT.ProductClick {

    RecyclerView rvItemofKOT;
    ImageView imgBack;
    LinearLayout llKOT;
    TextView tvLocationKOT, tvTotalBottom, tvTotal, tvTableno, tvKOTnumber, tvCardHolderName;
    RelativeLayout rlPrint;
    String userValue, locationName, tableId, name, memberID, MAINID,code, kotNUmber, currentDateTime,

        currentYear, waiterID, locationId, memberType,MemberType,couponNo,counponno,cashierCode,CashierCode,tableIdBill,locationID,cardAmount;

    int Aid,check=0,yearCode;
    Adapter_KOT adapter_kot;
    List<Product> products = new ArrayList<>();
    private com.zcs.sdk.DriverManager mDriverManager;
    private Printer mPrinter;
    List<String> filteredItemGroupBill;
    double spValue,taxRate, totalSP, newTotalSP, newGrandTotalSP, result,totalAmount = 0.0;
    int counting = 1,checkout=0,countingBill;
    List<String> filteredItemGroup;
    List<String> filteredItemSaletaxcode;
    List<String> filteredItemCode;
    List<String> filteredItems;
    List<String> filteredItemPrice;
    List<String> filteredItemQnt;
    List<Product> filteredProducts;
    int taxcode, taxcodeHalf, txquantity, exitCount = 0, discountCheck, discountCheck1 = 0, disCheckInner = 0, disCheckInner1 = 0;
String spString,resultString,MemberId,MemberIdFinal,MAINIDFinal,BillNoFinal,checkOccupiedtable;
    double   GrandTotal = 0.0, totalAmount1, totalAmount2, totalAmount3, totalAmount4, totalAmount5, totalAmount6, totalAmount7,
        totalAmount8, totalAmount9;
    BigDecimal roundedTotalAmount, roundedGrand, grandAmount, totalSPBigDec;
    private Handler handler;
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
        MAINID = intent.getStringExtra("MAINID");
        currentDateTime = intent.getStringExtra("currentDateTime");
        currentYear = intent.getStringExtra("currentYear");
        waiterID = intent.getStringExtra("waiterID");
        locationId = intent.getStringExtra("location");
        memberType = intent.getStringExtra("memberType");
        couponNo = intent.getStringExtra("couponNo");
        cashierCode = intent.getStringExtra("cashierCode");
        checkOccupiedtable = intent.getStringExtra("checkOccupiedtable");
        yearCode = intent.getIntExtra("yearCode",0);

        getBillData(code,tableId);
        if (checkOccupiedtable .matches(String.valueOf(0)) ){
            cardAmountData(MAINID);
        } else {
            cardAmountData(MAINIDFinal);
        }


        Log.e("fshtrstrh",""+name+" MAINID "+MAINID+" memberID "+memberID+" waiterID "+waiterID+" code "+code+"" +
            " tableId "+tableId+" checkOccupiedtable "+checkOccupiedtable+" locationName "+locationName+" currentDateTime "+currentDateTime+"" +
            " memberType "+memberType+" couponNo "+couponNo+" cashierCode "+cashierCode+" yearCode "+yearCode);


getDataFromKOTNo();
getDataFromBillNo();
        tvCardHolderName.setText("Hi, " + name);
        tvLocationKOT.setText(locationName);
        tvTableno.setText("Table : " + tableId);
        mDriverManager = DriverManager.getInstance();
        mPrinter = mDriverManager.getPrinter();


        ProductDatabase db = Room.databaseBuilder(getApplicationContext(),
            ProductDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();
        List<Product> allProducts = productDao.getallproduct();
        products.addAll(allProducts);
        filteredProducts = new ArrayList<>();
        filteredItemGroup = new ArrayList<>();
        filteredItemSaletaxcode = new ArrayList<>();
        filteredItemCode = new ArrayList<>();
        filteredItems = new ArrayList<>();
        filteredItemPrice = new ArrayList<>();
        filteredItemQnt = new ArrayList<>();

        for (Product product : allProducts) {
            if (product.getQnt() >= 1) {
                filteredProducts.add(product);
                filteredItems.add(product.getItemName());
                filteredItemPrice.add(String.valueOf(product.getSp()));
                filteredItemQnt.add(String.valueOf(product.getQnt()));
                filteredItemGroup.add(product.getItemGroup());
                filteredItemSaletaxcode.add(product.getSaletaxcode());
                filteredItemCode.add(String.valueOf(product.getItemCode()));

                int txquantity = product.getQnt();
                double spValue = product.getSp();
                int taxcode = Integer.parseInt(product.getSaletaxcode());


                Log.e("Quantity", String.valueOf(txquantity));
                Log.e("Tax", String.valueOf(taxcode));
                Log.e("spValue", String.valueOf(spValue));
                // Determine tax rate based on tax code

                // Calculate tax rate based on tax code
                double taxRate = 0.0;
                switch (taxcode) {
                    case 0:
                        taxRate = 0.00;
                        break;
                    case 2:
                        taxRate = 0.00;
                        break;
                    case 3:
                        taxRate = 0.00;
                        break;
                    case 5:
                        taxRate = 12.50;
                        break;
                    case 6:
                        taxRate = 5.00;
                        break;
                    case 7:
                        taxRate = 13.13;
                        break;
                    case 8:
                        taxRate = 18.00;
                        break;
                    case 9:
                        taxRate = 12.00;
                        break;
                    case 10:
                        taxRate = 28.00;
                        break;
                    case 11:
                        taxRate = 12.00;
                        break;
                    default:
                        // Handle default case
                        break;
                }

                // Calculate total amount for this product
                double productTotalAmount = txquantity * (spValue + (spValue * taxRate / 100.0));

                // Accumulate total amount
                totalAmount += productTotalAmount;

                // Log product details and total amount
                Log.e("Product Details", "Name: " + product.getItemName() +
                    ", Quantity: " + txquantity +
                    ", Selling Price: " + spValue +
                    ", Tax Rate: " + taxRate +
                    ", Total Amount: " + productTotalAmount);
                Log.e("dataofaddeddata", product.getItemGroup() + " Name : " + product.getItemName() +
                    " totalAmount : " + totalAmount);
            }

        }


        Log.e("dataofaddeddata", "String : " + filteredItemGroup + " " + filteredItems + " "
            + filteredItemPrice + " " + filteredItemQnt + " " + filteredItemSaletaxcode+ " " + totalAmount);



        exampleUsage();

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





        Log.e("TotalAmount", String.valueOf(totalAmount));
//        DiscountData(filteredItemSaletaxcode);
        roundedTotalAmount = new BigDecimal(totalAmount).setScale(2, RoundingMode.HALF_UP);

        Log.e("FinalRoundedTotalAmount", String.valueOf(roundedTotalAmount));

        if (discountCheck == 1) {
            try {
                GrandTotal = result + newTotalSP;

                Log.e("FinalcardAmount", String.valueOf(newTotalSP));
                roundedGrand = new BigDecimal(String.valueOf(newTotalSP)).setScale(2, RoundingMode.HALF_UP);
                newGrandTotalSP = newTotalSP + Double.parseDouble(String.valueOf(roundedTotalAmount));

                double cardAmountValue = Double.parseDouble(cardAmount);
                totalAmount1 = Double.valueOf(cardAmountValue - newGrandTotalSP);
                grandAmount = new BigDecimal(String.valueOf(totalAmount1)).setScale(2, RoundingMode.HALF_UP);

                Log.e("FinalcardAmount", String.valueOf(newGrandTotalSP));
            } catch (NumberFormatException e) {
                Log.e("NumberFormatException", "Error parsing double: " + e.getMessage());
            }

        } else {
            GrandTotal = Double.parseDouble(String.valueOf(roundedTotalAmount)) + Double.parseDouble(String.valueOf(totalSP));
            roundedGrand = new BigDecimal(String.valueOf(GrandTotal)).setScale(2, RoundingMode.HALF_UP);

            if (cardAmount != null && !cardAmount.isEmpty()) {

                double cardAmountValue = Double.parseDouble(cardAmount);
                totalAmount1 =  cardAmountValue - totalAmount ;
                grandAmount = new BigDecimal(String.valueOf(totalAmount1)).setScale(2, RoundingMode.HALF_UP);

                Log.e("FinalcardAmount", String.valueOf(grandAmount) + " " + roundedGrand + " " + cardAmount);

            }


        }
        handler = new Handler();

        handler = new Handler();
        // Convert cardAmount string to BigDecimal
        if(cardAmount!=null){


        BigDecimal cardAmountDecimal = new BigDecimal(cardAmount);

        if (roundedGrand.compareTo(cardAmountDecimal) > 0) {
            // newGrandTotalSP is greater than cardAmount
//            Toast.makeText(this, "Card balance is " + cardAmount +
//                "\nWhich is insufficient to cover the KOT amount\nPlease Recharge Card", Toast.LENGTH_LONG).show();
            rlPrint.setVisibility(View.GONE);
            showCustomAlertDialog();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    finish(); // Finish the activity
//                }
//            }, 5000); // 5000 milliseconds = 5 seconds
        } else if (roundedGrand.compareTo(cardAmountDecimal) < 0) {
            // newGrandTotalSP is less than cardAmount
            rlPrint.setVisibility(View.VISIBLE);
        } else {
            // newGrandTotalSP is equal to cardAmount
            rlPrint.setVisibility(View.VISIBLE);
        }
        }
        DiscountDataHead(code);
        rlPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                printReceipt();
                BillDatabase dbC = Room.databaseBuilder(getApplicationContext(),
                    BillDatabase.class, "bill_db").allowMainThreadQueries().build();
                BillDao countDao = dbC.BillDao();
                Boolean check = countDao.is_exist(counting);
                String Tid = String.valueOf(countDao.is_exist(Integer.valueOf(tableId)));
                String Lid = String.valueOf(countDao.is_exist(Integer.valueOf(code)));
                if (Lid.matches(code)) {
                    if (Tid.matches(tableId)) {
                        counting++;
                        countDao.updateRecord(counting, counting, counting);
//                        Toast.makeText(getApplicationContext(), "Updated Upper", Toast.LENGTH_SHORT).show();

                    } else {
                        if (!check) {
                            SharedPreferences sharedPreferences = getSharedPreferences("Details", MODE_PRIVATE);
                            SharedPreferences.Editor myEdit = sharedPreferences.edit();
                            myEdit.putString("counting", String.valueOf(counting));
                            myEdit.apply();
                            myEdit.commit();

                            insertKotData();
                            if (checkOccupiedtable .matches(String.valueOf(0)) ){
                                insertBillData();
                            } else {
                                insertDataBillBody();
                            }


                            Log.e("DataOfProduct", filteredItems + " "
                                + filteredItemPrice + " " + filteredItemQnt + " " + filteredItemGroup + " " +
                                "" + filteredItemSaletaxcode + " " + filteredItemCode + " name " + name);
                            SharedPreferences sharedPreferences2 = getSharedPreferences("dataOfcard", MODE_PRIVATE);
                            SharedPreferences.Editor myEdit2 = sharedPreferences2.edit();
                            myEdit2.clear();
                            myEdit2.apply();
                            int tOrder = 1;
                            updateTableOrder(Integer.parseInt(tableId), tOrder, code);
//                            countDao.insertrecord(new Bill(counting, String.valueOf(memberID + "," + name + "," + memberType+ "," + couponNo+ "," + cashierCode),
//                                userValue, locationName, locationId, waiterID, String.valueOf(filteredItems),
//                                String.valueOf(filteredItemPrice),
//                                String.valueOf(filteredItemQnt),
//                                String.valueOf(filteredItemGroup), String.valueOf(filteredItemSaletaxcode),
//                                String.valueOf(filteredItemCode), counting,
//                                Integer.valueOf(tableId), Integer.valueOf(code)));
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
//                        Toast.makeText(getApplicationContext(), "Updated Lower", Toast.LENGTH_SHORT).show();

                    } else {
                        if (!check) {
                            SharedPreferences sharedPreferences = getSharedPreferences("Details", MODE_PRIVATE);
                            SharedPreferences.Editor myEdit = sharedPreferences.edit();
                            myEdit.putString("counting", String.valueOf(counting));
                            myEdit.apply();
                            myEdit.commit();

                            insertKotData();
                            if (checkOccupiedtable .matches(String.valueOf(0)) ){
                                insertBillData();
                            } else {
                                insertDataBillBody();
                            }

                            Log.e("DataOfProduct", filteredItems + " "
                                + filteredItemPrice + " " + filteredItemQnt + " " + filteredItemGroup + " " +
                                "" + filteredItemSaletaxcode + " " + filteredItemCode + " name " + name);
                            SharedPreferences sharedPreferences2 = getSharedPreferences("dataOfcard", MODE_PRIVATE);
                            SharedPreferences.Editor myEdit2 = sharedPreferences2.edit();
                            myEdit2.clear();
                            myEdit2.apply();
                            int tOrder = 1;
                            updateTableOrder(Integer.parseInt(tableId), tOrder, code);
//                            countDao.insertrecord(new Bill(counting, String.valueOf(memberID + "," + name + "," + memberType+ "," + couponNo+ "," + cashierCode),
//                                userValue, locationName, locationId, waiterID, String.valueOf(filteredItems),
//                                String.valueOf(filteredItemPrice),
//                                String.valueOf(filteredItemQnt),
//                                String.valueOf(filteredItemGroup), String.valueOf(filteredItemSaletaxcode),
//                                String.valueOf(filteredItemCode), counting,
//                                Integer.valueOf(tableId), Integer.valueOf(code)));
                            counting++;

                            Toast.makeText(getApplicationContext(), "KOT Print", Toast.LENGTH_SHORT).show();
                        } else {
                            SharedPreferences sharedPcounting = getSharedPreferences("KotDetails", MODE_PRIVATE);
                            String Aid = sharedPcounting.getString("Aid", "0");
                            Log.e("DataOfProduct", filteredItems + " "
                                + filteredItemPrice + " " + filteredItemQnt + " " + filteredItemGroup + " " +
                                "" + filteredItemSaletaxcode + " " + filteredItemCode + " name " + name);

//                            countDao.insertrecord(new Bill(counting, String.valueOf(memberID + "," + name + "," + memberType+ "," + couponNo+ "," + cashierCode),
//                                userValue, locationName, locationId, waiterID, String.valueOf(filteredItems),
//                                String.valueOf(filteredItemPrice),
//                                String.valueOf(filteredItemQnt),
//                                String.valueOf(filteredItemGroup), String.valueOf(filteredItemSaletaxcode),
//                                String.valueOf(filteredItemCode), counting,
//                                Integer.valueOf(tableId), Integer.valueOf(code)));
//                            Toast.makeText(getApplicationContext(), "Product Already in List Lower " , Toast.LENGTH_SHORT).show();
                            counting++;

                            SharedPreferences.Editor myEdit1 = sharedPcounting.edit();
                            myEdit1.clear();
                            myEdit1.apply();
                        }
                    }


                }


//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putInt("counting", counting);
//                editor.apply();


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

    private void checkForNumbers(List<String> itemList, Context context) {
        boolean is8Present = itemList.contains("8");
        boolean is9Present = itemList.contains("9");
        if (userValue != null) {
            if (userValue.matches("1")) {
                if (is8Present) {
                    kotNUmber = "A-" + countingBill;
                } else if (is9Present) {
                    kotNUmber = "N-" + countingBill;
                } else {
                    kotNUmber = "F-" + countingBill;
                }
            } else if (userValue.matches("2")) {
                kotNUmber = "R-" + countingBill;
            }
        } else {
            Toast.makeText(this, "userValue is null", Toast.LENGTH_SHORT).show();
        }

    }


    private void exampleUsage() {
        checkForNumbers(filteredItemGroup, this);
    }


    private void printHorizontalLine(int length, PrnStrFormat format) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < length; i++) {
            line.append("-");
        }
        mPrinter.setPrintAppendString(line.toString(), format);
    }

    private void printText() {
        int printStatus = mPrinter.getPrinterStatus();
        if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
            //out of paper
            Toast.makeText(getApplicationContext(), "Out of paper", Toast.LENGTH_SHORT).show();
        } else {
            PrnStrFormat format = new PrnStrFormat();
            format.setTextSize(26);
            format.setAli(Layout.Alignment.ALIGN_CENTER);
            format.setStyle(PrnTextStyle.BOLD);
            format.setFont(PrnTextFont.SANS_SERIF);
            printHorizontalLine(43, format);
            mPrinter.setPrintAppendString("Defence Services Officers Institute", format);
            printHorizontalLine(43, format);
            format.setTextSize(23);
            format.setStyle(PrnTextStyle.NORMAL);
            format.setAli(Layout.Alignment.ALIGN_NORMAL);

            mPrinter.setPrintAppendString("KOT No.     : " + counting, format);
            mPrinter.setPrintAppendString("Table No.   : " + tableId, format);
            mPrinter.setPrintAppendString("MEMBER NAME : " + name, format);
            mPrinter.setPrintAppendString("MERCHANT ID : " + memberID, format);
            mPrinter.setPrintAppendString(" ", format);
            String leftP = " Item Name";
            String rightP = "Quantity";
            int totalL = 58;
            int leftPartL = leftP.length();
            int spacesN = totalL - leftPartL;
            Log.e("spaceData", leftPartL + " " + spacesN);
            String formatString = leftP + String.format("%" + spacesN + "s", rightP);

            mPrinter.setPrintAppendString("Item Name & Quantity", format);
            format.setTextSize(23);
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
                    String leftPart = product.getItemName();
                    String rightPart = " - " + product.getQnt() + "pc";
                    int leftPartLength = 48;
                    int rightPartLength = 10;


                    leftPart = leftPart.length() > leftPartLength ? leftPart.substring(0, leftPartLength) : leftPart;

// Truncate or pad the right part
                    rightPart = rightPart.length() > rightPartLength ? rightPart.substring(0, rightPartLength) : rightPart;

// Calculate the total length
                    int totalLength = leftPartLength + rightPartLength;

// Calculate the left padding
                    int leftPadding = Math.max(0, leftPartLength - leftPart.length());

// Create the final formatted string using String.format
                    String formattedString = leftPart + String.format("%" + (totalLength - leftPartLength) + "s", rightPart);

                    mPrinter.setPrintAppendString(formattedString, format);


//                    mPrinter.setPrintAppendString(product.getItemName() + "      -      " + product.getQnt() + "pc", format);
                }
            }
            format.setTextSize(23);
            format.setStyle(PrnTextStyle.NORMAL);
            format.setAli(Layout.Alignment.ALIGN_NORMAL);

            mPrinter.setPrintAppendString("", format);
            String totalQuantityText = "Total Quantity                                    : " + sum + "pcs";
            mPrinter.setPrintAppendString(totalQuantityText, format);

            mPrinter.setPrintAppendString(" ", format);
//            mPrinter.setPrintAppendString("Note : ", format);

            mPrinter.setPrintAppendString(" ", format);
            mPrinter.setPrintAppendString(" ", format);
            printStatus = mPrinter.setPrintStart();
        }
    }
    public void insertKotData() {

        ConnectionHelper connectionHelper = new ConnectionHelper(this);
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {
            try {

                System.out.println("Connected to the database");
                Log.e("sagaa", "Connected to the database");

                // Check if a record with the same KOTNo and LocationCode already exists
                String checkQuery = "SELECT COUNT(*) FROM TI_ChargeableBarKOTHead WHERE KOTNo = ? AND LocationCode = ?AND YearCode = ?";
                PreparedStatement checkStatement = connect.prepareStatement(checkQuery);
                checkStatement.setString(1, String.valueOf(counting));
                checkStatement.setString(2, code);
                checkStatement.setString(3, String.valueOf(yearCode));
                ResultSet resultSet = checkStatement.executeQuery();
                resultSet.next();
                int rowCount = resultSet.getInt(1);

                if (rowCount > 0) {
                    System.out.println("Record with KOTNo " + counting + " and LocationCode " + code + " already exists.");
                    Log.e("sagaa", "Record with KOTNo " + counting + " and LocationCode " + code + " already exists.");

                } else {
                       String insertQuery = "INSERT INTO TI_ChargeableBarKOThead (KOTNo, LocationCode, MemberId," +
                        " TableNo, YearCode, CashierCode, WaiterId, UserCode,KOTDate,CreationDate,ModificationDate,PAX" +
                        ",MemberType,Couponno,RefNo,OpeningBalance,ClosingBalance)" +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?,?, ?,?, ?, ?, ?,?, ?,?)";

                    // Create a PreparedStatement to execute the query
                    PreparedStatement preparedStatement = connect.prepareStatement(insertQuery);

                    // Set values for the parameters
                    preparedStatement.setString(1, String.valueOf(counting));
                    preparedStatement.setString(2, code);
                    if (checkOccupiedtable .matches(String.valueOf(0)) ){
                        preparedStatement.setString(3, memberID);
                    } else {
                        preparedStatement.setString(3, MemberIdFinal);
                    }

                    preparedStatement.setString(4, tableId);

                    // Set non-null values for other columns
                    preparedStatement.setInt(5, yearCode);
                    if (checkOccupiedtable .matches(String.valueOf(0)) ){
                        preparedStatement.setString(6, cashierCode);
                    } else {
                        preparedStatement.setString(6, CashierCode);
                    }

                    preparedStatement.setString(7, waiterID);
                    preparedStatement.setString(8, "1");
                    preparedStatement.setString(9, currentDateTime);
                    preparedStatement.setString(10, currentDateTime);
                    preparedStatement.setString(11, currentDateTime);
                    preparedStatement.setString(12, "1");

                    if (checkOccupiedtable .matches(String.valueOf(0)) ){
                        preparedStatement.setString(13, memberType);
                        preparedStatement.setString(14, couponNo);
                    } else {
                        preparedStatement.setString(13, MemberType);
                        preparedStatement.setString(14, counponno);
                    }

                    preparedStatement.setString(15, "0");
                    preparedStatement.setString(16, cardAmount);

                    if (discountCheck == 1) {
                        preparedStatement.setString(17, "0");
                    } else {
                        preparedStatement.setString(17, String.valueOf(grandAmount));
                    }

                    // Execute the update
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        Log.e("sagaa", "Data inserted successfully");
                        ProductDatabase db = Room.databaseBuilder(getApplicationContext(),
                            ProductDatabase.class, "cart_db").allowMainThreadQueries().build();
                        ProductDao productDao = db.ProductDao();
                        List<Product> allProducts = productDao.getallproduct();
                        for (Product product : allProducts) {
                            if (product.getQnt() >= 1) {

                                String itemName = product.getItemName();
                                String saletaxcode = product.getSaletaxcode();
                                int qyt = product.getQnt();
                                double rate = product.getSp();
                                String itemcode = String.valueOf(product.getItemCode());
                                String unitCode = product.getSaleUnit();
                                switch (Integer.valueOf(saletaxcode)) {
                                    case 0:
                                        insertDataKOTBody(itemName, "0.00", qyt, rate, itemcode,unitCode);
                                        break;
                                    case 2:
                                        insertDataKOTBody(itemName, "0.00", qyt, rate, itemcode,unitCode);
                                        break;
                                    case 3:
                                        insertDataKOTBody(itemName, "0.00", qyt, rate, itemcode,unitCode);
                                        break;
                                    case 5:
                                        insertDataKOTBody(itemName, "12.50", qyt, rate, itemcode,unitCode);
                                        break;
                                    case 6:
                                        insertDataKOTBody(itemName, "5.00", qyt, rate, itemcode,unitCode);
                                        break;
                                    case 7:
                                        insertDataKOTBody(itemName, "13.13", qyt, rate, itemcode,unitCode);
                                        break;
                                    case 8:
                                        insertDataKOTBody(itemName, "18.00", qyt, rate, itemcode,unitCode);
                                        break;
                                    case 9:
                                        insertDataKOTBody(itemName, "12.00", qyt, rate, itemcode,unitCode);
                                        break;
                                    case 10:
                                        insertDataKOTBody(itemName, "28.00", qyt, rate, itemcode,unitCode);
                                        break;
                                    case 11:
                                        insertDataKOTBody(itemName, "12.00", qyt, rate, itemcode,unitCode);
                                        break;
                                    default:
                                        // Handle default case
                                        break;
                                }


                                Log.e("adgadgag", itemName + " " + saletaxcode + " " + qyt + " " + rate);
                            }
                        }


                    } else {
                        System.out.println("Failed to insert data");
                    }

                    // Close the PreparedStatement
                    preparedStatement.close();
                }

                // Close the ResultSet and connection
                resultSet.close();
                checkStatement.close();
                connect.close();

            } catch (SQLException e) {
                // Handle SQL exceptions
                e.printStackTrace();
                System.out.println("Failed to execute SQL update: " + e.getMessage());
                Log.e("sagaa", "Failed to insert data. Error: " + e.getMessage()+" "+"SQL State: " + e.getSQLState()+" "+"Error Code: " + e.getErrorCode());
            }

        } else {
            // Connection failed
            System.out.println("Failed to connect to the database");
            Log.e("sagaa", "Failed to connect to the database");
        }
    }
    public void getDataFromKOTNo() {
        // Check if connection is not null
        try (Connection connection = new ConnectionHelper(this).connectionclass()) {
            if (connection != null) {
                try {
                    // SQL query to select data from KOTNo column
                    String selectQuery = "SELECT MAX(KOTNo) + 1 AS NextKOTNo FROM TI_ChargeableBarKOThead WHERE YearCode = ?";

                    // Prepare the statement
                    PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);

                    // Set the yearCode parameter
                    preparedStatement.setInt(1, yearCode);

                    // Execute the query
                    ResultSet resultSet = preparedStatement.executeQuery();

                    // Process the results
                    while (resultSet.next()) {
                        // Retrieve data from the current row
                        int nextKOTNo = resultSet.getInt("NextKOTNo");
                        // Do something with nextKOTNo, like printing it or storing it in a variable
                        System.out.println("Next KOTNo: " + nextKOTNo);
                        counting = nextKOTNo;

                        tvKOTnumber.setText(String.valueOf(counting));
                        // Log the retrieved KOTNo
                        Log.e("dataofKOT", String.valueOf(nextKOTNo));
                    }

                    // Close resources
                    resultSet.close();
                    preparedStatement.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Connection is null");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void getDataFromBillNo() {
        // Check if connection is not null
        try (Connection connection = new ConnectionHelper(this).connectionclass()) {
            if (connection != null) {
                try {
                    // SQL query to select data from KOTNo column
                    String selectQuery = "SELECT MAX(CAST( RIGHT(BillNo, LEN(BillNo)-2)AS numeric(18,0) ) )AS MaxBillNo FROM TI_BarBillHead WHERE YearCode = ?";

                    // Create PreparedStatement
                    try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                        // Set parameter for YearCode
                        preparedStatement.setString(1, String.valueOf(yearCode)); // Replace "your_year_code_here" with actual year code

                        // Execute query
                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                            if (resultSet.next()) {
                                // Retrieve data from result set
                                int maxBillNo = resultSet.getInt("MaxBillNo");
                                System.out.println("Max Bill No: " + maxBillNo);
                                Log.e("MaxDataofbill", String.valueOf(maxBillNo));
                                countingBill=maxBillNo;
                                countingBill++;
                                // Do something with the maxBillNo, e.g., return it or use it further
                            } else {
                                // No data found
                                System.out.println("No data found for the given year code.");
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Connection is null");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void insertDataKOTBody(String itemName, String saletaxcode, int qyt, double rate,
                                  String itemcode, String unitCode) {


        ConnectionHelper connectionHelper = new ConnectionHelper(this);
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {
            try {
                // Connected to the database
                Log.e("dsvdfvdaf", "Connected to the database");


                String insertQuery = "INSERT INTO TI_ChargeableBarKOTBody (KOTNo, itemcode, UnitCode," +
                    " Qty, Rate, Amount, DiscountPer, VAT,GST, ServiceCharge,ActualQty,ItemName,OpenItem," +
                    "CreationDate,ModificationDate,YearCode,UserCode,CessPer,AddCessAmt,SCM_Id,SCMDiscPer" +
                    ",SCMDiscAmt,SCMFreeItem,ItemRemarks) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?)";

                String total = String.valueOf(qyt * rate);
// Create a PreparedStatement to execute the query
                PreparedStatement preparedStatement = connect.prepareStatement(insertQuery);

// Set values for the parameters (replace these with your actual values)
                preparedStatement.setString(1, String.valueOf(counting)); // Assuming 'counting' contains the 'UserCode' value
                preparedStatement.setString(2, itemcode);
                preparedStatement.setString(3, unitCode);
                preparedStatement.setString(4, String.valueOf(qyt));

// Set non-null values for 'YearCode', 'CashierCode', and 'WaiterId' (replace 2024, 'YourCashierCodeValue', and 'YourWaiterIdValue' with your actual values)
                preparedStatement.setDouble(5, rate);
                preparedStatement.setString(6, total);
                preparedStatement.setString(7, "0.00");
                if (saletaxcode.equals("12.50") || saletaxcode.equals("13.13")) {
                    preparedStatement.setString(8, saletaxcode);
                    preparedStatement.setString(9, "0.00");
                } else {
                    preparedStatement.setString(8, "0.00");
                    preparedStatement.setString(9, saletaxcode);
                }

                preparedStatement.setString(10, "0.00");
                preparedStatement.setString(11, String.valueOf(qyt) + ".00");
                preparedStatement.setString(12, itemName);
                preparedStatement.setString(13, "0");
                preparedStatement.setString(14, currentDateTime);
                preparedStatement.setString(15, currentDateTime);
                preparedStatement.setString(16, String.valueOf(yearCode));
                if (checkOccupiedtable .matches(String.valueOf(0)) ){
                    preparedStatement.setString(17, cashierCode);
                } else {
                    preparedStatement.setString(17, CashierCode);
                }

                 preparedStatement.setString(18, "0");
                preparedStatement.setString(19, "0");
                preparedStatement.setString(20, "0");
                preparedStatement.setString(21, "0");
                preparedStatement.setString(22, "0");
                preparedStatement.setString(23, "0");
                preparedStatement.setString(24, "");
// Execute the update
                int rowsAffected = preparedStatement.executeUpdate();


                if (rowsAffected > 0) {
                    System.out.println("Data inserted successfully");
                    Log.e("dsvdfvdaf", "Data inserted successfully");
                    if (checkout==0){
                        printText();
                        ProductDatabase db = Room.databaseBuilder(getApplicationContext(),
                            ProductDatabase.class, "cart_db").allowMainThreadQueries().build();
                        db.clearAllTables();
                        checkout=1;
                        Intent intent = new Intent( this, MainMenu.class);
                        startActivity(intent);
                        finish();
                    }

                } else {
                    System.out.println("Failed to insert data");
                    Log.e("dsvdfvdaf", "Failed to insert data");
                }

                // Close the connection and statement
                preparedStatement.close();
                connect.close();
            } catch (SQLException e) {
                // Handle SQL exceptions
                e.printStackTrace();
                Log.e("dsvdfvdaf", "Failed to insert data. Error: " + e.getMessage());

            }

        } else {
            // Connection failed
            System.out.println("Failed to connect to the database");
            Log.e("dsvdfvdaf", "Failed to connect to the database");
        }
    }

     public void updateTableOrder(int tableID, int newTableOrder, String location) {


        ConnectionHelper connectionHelper = new ConnectionHelper(this);
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {
            try {
                // Connected to the database
                System.out.println("Connected to the database");
                Log.e("agdeagdgsd", "Connected to the database");

                // Create a SQL query to update TableOrder for a specific TableId
                String updateQuery = "UPDATE TM_LOCATION_TABLE SET TableOrder = " + newTableOrder +
                    " WHERE TableId = " + tableID + " AND Location = '" + location + "'";

                // Create a statement
                Statement statement = connect.createStatement();

                // Execute the query
                int rowsAffected = statement.executeUpdate(updateQuery);

                if (rowsAffected > 0) {

                    System.out.println("Data updated successfully");
                    Log.e("agdeagdgsd", "Data updated successfully");
                } else {
                    System.out.println("Failed to update data");
                    Log.e("agdeagdgsd", "Failed to update data");
                }

                // Close the connection and statement
                statement.close();
                connect.close();
            } catch (SQLException e) {
                // Handle SQL exceptions
                e.printStackTrace();
                System.out.println("Failed to execute SQL update");
                Log.e("agdeagdgsd", "Failed to update data. Error: " + e.getMessage());
            }

        } else {
            // Connection failed
            System.out.println("Failed to connect to the database");
            Log.e("agdeagdgsd", "Failed to connect to the database");
        }

        SharedPreferences sharedPreferences1 = getSharedPreferences("NecessaryData", MODE_PRIVATE);

        SharedPreferences sharedPreferences = getSharedPreferences("dataOfcard", MODE_PRIVATE);

        SharedPreferences.Editor myEdit1 = sharedPreferences1.edit();
        myEdit1.clear();
        myEdit1.apply();
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.clear();
        myEdit.apply();

        Intent intent = new Intent(getApplicationContext(), MainMenu.class);

        startActivity(intent);


        finish();
    }
    public void cardAmountData(String uservalue) {

        ConnectionHelper connectionHelper = new ConnectionHelper(this);
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {

            Log.e("gsvjgdv", "Connected to the database");

            try {

                String query = "SELECT couponno, amount,usercode FROM TM_IssueCoupon WHERE memberidno " +
                    "= '" + uservalue + "'";

                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);

                // Check if the result set is empty
                if (!rs.next()) {

                    //                    tvInformation.setText("Card Amount  not exist in DataBase");
//                    tvInformation.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Card not exist in CArd Amount DataBase", Toast.LENGTH_SHORT).show();
               finish();
                } else {

                    do {
                          counponno = rs.getString("couponno"); // Corrected column name
                        cardAmount = rs.getString("amount");
                        CashierCode = rs.getString("usercode");
check=1;
                        if (counponno != null && cardAmount != null) {
                            Log.e("ValueOFAmount", "Data found: " + counponno + " " + cardAmount+ " " + CashierCode);

                        } else {
                            Log.e("ValueOFAmount", "Incomplete data for this row");
                        }
                    } while (rs.next());
                }

                connect.close();
            } catch (Exception e) {
                // Handle the exception appropriately
                Log.e("ValueOFAmount", "Exception while retrieving data: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Connection failed
            Log.e("gsvjgdv", "Failed to connect to the database");
        }
    }


    public void DiscountDataHead(String code) {
        ConnectionHelper connectionHelper = new ConnectionHelper(this);
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {
            Log.e("eayrw44w5", "Connected to the database");

            try {
                // Fetch all data from TM_SchemeHead table
                String query = "SELECT * FROM TM_SchemeHeadWHERE LocationCode = '" + code + "'";

                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);

                // ArrayList to hold all the fetched data


                while (rs.next()) {
                    // Fetch data from each row
                    String ID = rs.getString("ID"); // Replace with actual column name
                    String AppliedOn = rs.getString("AppliedOn"); // Replace with actual column name
                    String LocationCode = rs.getString("LocationCode"); // Replace with actual column name
                    String FromDate = rs.getString("FromDate"); // Replace with actual column name
                    String ToDate = rs.getString("ToDate"); // Replace with actual column name
                    String YearCode = rs.getString("YearCode"); // Replace with actual column name



                    Log.e("SchemeBodyData", ID+" "+AppliedOn+" "+LocationCode+" "+FromDate+" "+ToDate+" "+YearCode  );
                }

                // Close the connection after fetching all data
                connect.close();
            } catch (Exception e) {
                // Handle the exception appropriately
                e.printStackTrace();
            }
        } else {
            // Connection failed
            Log.e("eayrw44w5", "Failed to connect to the database");
        }
    }

    public void DiscountData(List<String> itemCodeList) {


        ConnectionHelper connectionHelper = new ConnectionHelper(this);
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {
            Log.e("dhzffh", "Connected to the database");

            try {
                // Iterate through each item code in itemCodeList
                for (String itemCode : itemCodeList) {
                    String query = "SELECT * FROM TM_SchemeBody WHERE itemcode = '" + itemCode + "'";

                    Statement st = connect.createStatement();
                    ResultSet rs = st.executeQuery(query);
                    if (discountCheck != 1) {


                        while (rs.next()) {

                            String schemeBodyData = rs.getString("itemcode"); // Replace with actual column name

                            if (disCheckInner == 0) {

                                double percentage = 0.05;
                                result = totalSP * percentage;
                                newTotalSP = totalSP - result;
                                resultString = String.valueOf(result);
                                totalSPBigDec = new BigDecimal(String.valueOf(resultString)).setScale(2, RoundingMode.HALF_UP);


                                disCheckInner = 1;
                                disCheckInner1 = 2;
                            }

                            discountCheck = 1;
                            discountCheck1 = 2;

//                        Toast.makeText(this, ""+discountCheck, Toast.LENGTH_SHORT).show();
                            Log.e("SchemeBodyData", schemeBodyData + "   " + resultString);
                        }
                    }
                }

                connect.close();
            } catch (Exception e) {
                // Handle the exception appropriately
                e.printStackTrace();
            } finally {


            }
        } else {
            // Connection failed
            Log.e("dhzffh", "Failed to connect to the database");
        }
    }
    public void insertBillData() {
        try (Connection connect = new ConnectionHelper(this).connectionclass()) {
            if (connect != null) {
                // Connected to the database
                System.out.println("Connected to the database");
                Log.e("ergeg", "Connected to the database " + kotNUmber);

                // Create a SQL query to insert data into the table
                String insertQuery = "INSERT INTO TI_BarBillHead (BillNo, LocationCode, MemberId, " +
                    "TableId, YearCode, MemberType, WaiterId, UserCode, BillDate, CreationDate, " +
                    "ModificationDate, amount, OpeningBalance, ClosingBalance, Couponno, NoOfGuest, " +
                    "modeofpayment, PAX, BillStatus, BillType, RoundOff, CurrentOutStanding, printbalance) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement preparedStatement = connect.prepareStatement(insertQuery)) {
                    // Set values for the parameters
                    preparedStatement.setString(1, kotNUmber);
                    preparedStatement.setString(2, code);
                    preparedStatement.setString(3, memberID);
                    preparedStatement.setString(4, tableId);
                    preparedStatement.setInt(5, yearCode);
                    preparedStatement.setString(6, memberType);
                    preparedStatement.setString(7, waiterID);
                    preparedStatement.setString(8, checkOccupiedtable.matches(String.valueOf(0)) ? cashierCode : CashierCode);
                    preparedStatement.setString(9, currentDateTime);
                    preparedStatement.setString(10, currentDateTime);
                    preparedStatement.setString(11, currentDateTime);
                    preparedStatement.setString(12, discountCheck == 1 ? String.valueOf(newGrandTotalSP) : String.valueOf(roundedTotalAmount));
                    preparedStatement.setString(13, cardAmount);
                    preparedStatement.setString(14, String.valueOf(grandAmount));
                    preparedStatement.setString(15, checkOccupiedtable.matches(String.valueOf(0)) ? couponNo : counponno);
                    preparedStatement.setString(16, "");
                    preparedStatement.setString(17, "Card");
                    preparedStatement.setString(18, "1");
                    preparedStatement.setString(19, "0");
                    preparedStatement.setString(20, "0");
                    preparedStatement.setString(21, "0");
                    preparedStatement.setString(22, "0");
                    preparedStatement.setString(23, "No");

                    // Execute the update
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Data inserted successfully");
                        insertDataBillBody();
                        Log.e("ergeg", "Data inserted successfully");
                    } else {
                        System.out.println("Failed to insert data");
                        Log.e("ergeg", "Failed to insert data");
                        // Handle failure
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Failed to execute SQL update");
                    // Handle SQL exceptions
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

    // Method to check if a record with the same primary key already exists
     public void insertDataBillBody() {

        try (Connection connect = new ConnectionHelper(this).connectionclass()) {
            if (connect != null) {
                // Connected to the database
                System.out.println("Connected to the database");
                Log.e("sfadfaf", "Connected to the database");

                // Create a SQL query to insert data into the table
                // Create a SQL query to insert data into the table
                String insertQuery = "INSERT INTO TI_BarBillBody (BillNo, KOTNo, CreationDate," +
                    " ModificationDate, UserCode, YearCode) VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement preparedStatement = connect.prepareStatement(insertQuery)) {
                    if (checkOccupiedtable .matches(String.valueOf(0)) ){
                        preparedStatement.setString(1, kotNUmber);
                    } else {
                        preparedStatement.setString(1, BillNoFinal);
                    }

                    preparedStatement.setString(2, String.valueOf(counting));
                    preparedStatement.setString(3, currentDateTime);
                    preparedStatement.setString(4, currentDateTime);
                    if (checkOccupiedtable .matches(String.valueOf(0)) ){
                        preparedStatement.setString(5, cashierCode);
                    } else {
                        preparedStatement.setString(5, CashierCode);
                    }


                     preparedStatement.setString(6, String.valueOf(yearCode));


                    // Execute the update
                    int rowsAffected = preparedStatement.executeUpdate();
                    Log.e("sfadfaf", String.valueOf(rowsAffected)+" "+preparedStatement);

                    if (rowsAffected > 0) {

//                        Toast.makeText(this, "Bill Print", Toast.LENGTH_SHORT).show();
                        Log.e("sfadfaf", "Data inserted successfully");

//                        UpdateBillAmount();



//                        Intent intent = new Intent( this, MainMenu.class);
//                        startActivity(intent);
//                        finish();
                    } else {
                        System.out.println("Failed to insert data");
                        Toast.makeText(this, "Failed to insert data", Toast.LENGTH_SHORT).show();

                        Log.e("sfadfaf", "Failed to insert data");
                    }
                } catch (SQLException e) {
                    // Handle SQL exceptions
                    e.printStackTrace();
                    System.out.println("Failed to execute SQL update");
                    Toast.makeText(this, "Failed to insert data. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    Log.e("sfadfaf", "Failed to insert data. Error: " + e.getMessage());
                }
            } else {
                // Connection failed
                System.out.println("Failed to connect to the database");
                Log.e("sfadfaf", "Failed to connect to the database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void UpdateBillAmount() {

        ConnectionHelper connectionHelper = new ConnectionHelper(this);
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {
            try {
                // Connected to the database
                System.out.println("Connected to the database");
                Log.e("kjkjkj", "Connected to the database");
                if (discountCheck == 1){

                    totalAmount1=   newGrandTotalSP ;
                } else {

                    totalAmount1 = Double.parseDouble(String.valueOf(roundedTotalAmount));
                }
                // Create and execute the SQL update statement
                Statement stmt = connect.createStatement();
                String updateQuery = "UPDATE  [TM_IssueCoupon] " +
                    "SET [amount] = " +
                    totalAmount1 + " " +
                    "WHERE [memberidno] = '" + MAINID + "'";
                int rowsAffected = stmt.executeUpdate(updateQuery);

                if (rowsAffected > 0) {
                    System.out.println(rowsAffected + " rows updated successfully");
                    Log.e("kjkjkj", rowsAffected + " rows updated successfully");
                } else {
                    System.out.println("No rows updated");
                    Log.e("kjkjkj", "No rows updated");
                }

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

    public void getBillData(String code, String tableId) {
        try (Connection connect = new ConnectionHelper(this).connectionclass()) {
            if (connect != null) {
                // Connected to the database
                System.out.println("Connected to the database");
                Log.e("Debug", "Connected to the database");

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
                    Log.e("Debug", "Executing query: " + preparedStatement.toString());
                    // Execute the query
                    ResultSet resultSet = preparedStatement.executeQuery();
                    // Process the results
                    while (resultSet.next()) {
                        // Retrieve data from each row
                        String BillNo = resultSet.getString("BillNo");
                        String BillDate = resultSet.getString("BillDate");
                        MemberId = resultSet.getString("MemberId");
                        MAINID = resultSet.getString("MAINID");
                        // Extract only the date part
                        BillDate = BillDate.split(" ")[0]; // Assuming the date format is consistent
                        // Log the retrieved data for debugging
                        Log.e("Debug", "BillNo: " + BillNo + ", BillDate: " + BillDate);
                        // Check if the BillDate matches the current date
                        if (BillDate.equals(currentDate)) {
                            BillNoFinal = BillNo;
                            MemberIdFinal = MemberId;
                            MAINIDFinal = MAINID;
                          getCardData(MemberIdFinal);
                            Log.e("Debug", "Matching BillNo found: " + BillNoFinal);
                            // Call getBarBillBody
//                             getCardData(MemberId);

                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.e("Error", "SQLException: " + e.getMessage());
                }
            } else {
                // Connection failed
                System.out.println("Failed to connect to the database");
                Log.e("Error", "Failed to connect to the database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("Error", "SQLException while retrieving data: " + e.getMessage());
        }
    }
    public String getCardData(String memberId) {
        try (Connection connect = new ConnectionHelper(this).connectionclass()) {
            if (connect != null) {
                // Connected to the database
                System.out.println("Connected to the database");
                Log.e("fjgfhfhg", "Connected to the database");

                // Get the current date
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = dateFormat.format(new Date());

                // Create a SQL query to select data from the table where memberID matches
                String query = "SELECT serialno, MAINID, name, memberID, type " +
                    "FROM TM_Memberinformation WHERE memberID = ?";

                try (PreparedStatement statement = connect.prepareStatement(query)) {
                    statement.setString(1, memberId);
                    ResultSet rs = statement.executeQuery();

                    if (rs.next()) {
                        // Retrieve data from the ResultSet
                        String name = rs.getString("name");
                          MemberType = rs.getString("type");

                        if  ( name != null  ) {

                            // Assuming 'name' cannot be null in the database, you may want to handle it differently if it can be null.
                            Log.e("fjgfhfhg", "Connected to the database"+" " +name);

                            return null;
                        }

                        return name; // Assuming you want to return the name if it exists.
                    } else {
                        // Handle the case where no data is found for the given memberId
                        System.out.println("No data found for memberId: " + memberId);
                        return null; // Or handle the case differently based on your requirements
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null; // Handle the exception according to your application's logic
                }
            } else {
                // Connection failed
                System.out.println("Failed to connect to the database");
                Log.e("fjgfhfhg", "Failed to connect to the database");
                return null; // Or handle the failure differently based on your requirements
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Handle the exception according to your application's logic
        }
    }
    private void showCustomAlertDialog() {

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_custom_layout, null);

         RelativeLayout rlYes  = dialogView.findViewById(R.id.rlYes);

TextView tvMessage  = dialogView.findViewById(R.id.dialog_message);
        AlertDialog.Builder builder = new AlertDialog.Builder(KOT_Activity.this);
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();


        tvMessage.setText("Card balance is  "  + cardAmount + " Which is insufficient to cover the KOT amount");
        rlYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
//                SharedPreferences.Editor myEdit = sharedPreferences.edit();
//                myEdit.clear();
//                myEdit.apply();
                alertDialog.dismiss();

//                Intent intent = new Intent(KOT_Activity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
            }
        });

        alertDialog.show();
    }

}
