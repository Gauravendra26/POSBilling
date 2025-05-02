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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.util.Pair;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.syss.MifareClassicTool.Activities.MainMenu;
import de.syss.MifareClassicTool.Room.Bill;
import de.syss.MifareClassicTool.Room.BillDao;
import de.syss.MifareClassicTool.Room.BillDatabase;
import de.syss.MifareClassicTool.Room.Product;
import de.syss.MifareClassicTool.Room.ProductDao;
import de.syss.MifareClassicTool.Room.ProductDatabase;

public class Bill_Activity extends AppCompatActivity {
    ImageView imgBack;
    RelativeLayout rlPaid, rlLayout;

    LinearLayout llBill, llDiscount, llNetTotalOfBill, llTableDeatils;
    RecyclerView rvBill, rvGST;
    TextView tvText, tvtablenNoofBill, tvGrandTotal, tvTotalOfBill, tvTotalOfGovTx, tvToPay, tvLocationName,
        tvDiscount, tv_myc, tvDiscountText, tvNetTotalOfBill, tvMemberId, tvTableId, tvName, tvKOTNo, tv_Information;
    String userValue, locationName, tableId, code, cardAmount, kqntValue, cashierCode,
        currentDateTime, currentYear, waiterID, kotNUmber, locationID, memberType, couponNo;
    Adapter_Bill adapter_bill;
    List<Product> products = new ArrayList<>();
    BigDecimal roundedFinaltotalAmountGSTFifthHalf,roundedFinaltotalAmountGSTtwelveHalf,roundedFinaltotalAmountGSTEighteenHalf
        ,roundedFinaltotalAmountGSTtwentyEightHalf,roundedTotalGST;
    TextView tvFiveCGSTAmount,tvFiveSGSTAmount,tvTwelveCGSTAmount,tvTwelveSGSTAmount,tvEighteenCGSTAmount,tvEighteenSGSTAmount,tvTwentyEightCGSTAmount,tvTwentyEightSGSTAmount;
    LinearLayout llFiveCGST,llFiveSGST,llTwelveCGST,llTwelveSGST,llEighteenCGST,llEighteenSGST,llTwentyEightCGST,llTwentyEightSGST;

    int taxcode, taxcodeHalf, txquantity, exitCount = 0, discountCheck, discountCheck1 = 0, counting,
        disCheckInner = 0, disCheckInner1 = 0;
    int check1=0,check2=0,check3=0,check4=0,check5=0;

    double totalAmount = 0.0, GrandTotal = 0.0, totalAmount1, totalAmount2, totalAmount3, totalAmount4, totalAmount5, totalAmount6, totalAmount7,
        totalAmount8, totalAmount9, totalAmountFinalDouble = 0.0, totalAmountList = 0.0, totalAmountGSTZero = 0.0,
        totalAmountGSTFifth = 0.0, totalAmountGSTSeventh = 0.0, totalAmountGSTtwelve = 0.0, totalAmountGSTEighteen = 0.0, totalAmountGSTtwentyEight = 0.0;
    String spString, name, memberID, MemberId, resultString, OpeningBalance, BillNoFinal, OpeningBalanceFinal, nameFinal;
    double spValue, totalSP, newTotalSP, newGrandTotalSP, result;
    private com.zcs.sdk.DriverManager mDriverManager;
    private Printer mPrinter;
    BigDecimal roundedTotalAmount, roundedGrand, grandAmount, totalSPBigDec, roundedGrandDouble, roundedGrandList;
    List<String> itemCodeList = new ArrayList<>();
    ArrayList<Double> totalAmountFinalList = new ArrayList<>();
    ArrayList<Model_GST> model_gsts;
    private List<String> matchedIdList;
    private int matchCount = 0;
    ArrayList<Model_OccupiedTable> model_occupiedTable;
    Adapter_GST adapter_gst;
    List<String> filteredItemGroupBill;
    int Aid, checkOut = 0, yearCode;
    private Handler handler;
    private List<String> idList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        Utils.blackIconStatusBar(Bill_Activity.this, R.color.main);
        init();
        BillData();

        Intent intent = getIntent();
        counting = intent.getIntExtra("aid", 0);
        Aid = intent.getIntExtra("aid", 0);
//        yearCode = intent.getIntExtra("yearCode", 0);
        userValue = intent.getStringExtra("userValue");
        locationName = intent.getStringExtra("locationName");
        tableId = intent.getStringExtra("tableId");
yearCodeData();
        name = intent.getStringExtra("name");
        memberID = intent.getStringExtra("memberID");
        memberType = intent.getStringExtra("memberType");
        couponNo = intent.getStringExtra("couponNo");
        code = intent.getStringExtra("code");
        cashierCode = intent.getStringExtra("cashierCode");

        waiterID = intent.getStringExtra("waiterID");
        Log.e("gehash", "" + name + " memberID " + memberID + " waiterID " + waiterID + " code " + code + " tableId " + tableId);
        idList = new ArrayList<>();
        matchedIdList = new ArrayList<>();


        getBillData(code, tableId);

        tvLocationName.setText(locationName);
        tvtablenNoofBill.setText("Table No. : " + tableId);
        tvTableId.setText(tableId);
        currentDateTime = DateTimeUtil.getCurrentDateTime();
        currentYear = DateTimeUtil.getCurrentYear();


        mDriverManager = DriverManager.getInstance();
        mPrinter = mDriverManager.getPrinter();



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


//        exampleUsage();
//        try {
//            while (isBillNoExist(kotNUmber)) {
//
//                counting++;
//                exampleUsage();
////                Toast.makeText(this, "Exist kqntValue " + kotNUmber, Toast.LENGTH_SHORT).show();
//                Log.d("dgdsgs", "Modified KqntValue: " + kotNUmber);
//            }
////            Toast.makeText(this, "Not exist kqntValue " + kotNUmber, Toast.LENGTH_SHORT).show();
//
//        } catch (SQLException e) {
//            // Handle the exception appropriately, e.g., log or show an error message
//            Log.e("dgdsgs", "Error checking Bill number existence", e);
//        }

    }

    void init() {
        imgBack = findViewById(R.id.imgBack);
        rlPaid = findViewById(R.id.rlPaid);
        rlLayout = findViewById(R.id.rlLayout);
        tvText = findViewById(R.id.tvText);
        tvtablenNoofBill = findViewById(R.id.tvtablenNoofBill);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);
        tvTotalOfBill = findViewById(R.id.tvTotalOfBill);
        tvTotalOfGovTx = findViewById(R.id.tvTotalOfGovTx);
        tvToPay = findViewById(R.id.tvToPay);
        tvLocationName = findViewById(R.id.tvLocationName);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvDiscountText = findViewById(R.id.tvDiscountText);
        tvNetTotalOfBill = findViewById(R.id.tvNetTotalOfBill);
        tvMemberId = findViewById(R.id.tvMemberId);
        tvTableId = findViewById(R.id.tvTableId);
        tvName = findViewById(R.id.tvName);
        tvKOTNo = findViewById(R.id.tvKOTNo);
        tv_Information = findViewById(R.id.tv_Information);
        tv_myc = findViewById(R.id.tv_myc);
        rvBill = findViewById(R.id.rvBill);
        rvGST = findViewById(R.id.rvGST);
        llBill = findViewById(R.id.llBill);
        llDiscount = findViewById(R.id.llDiscount);
        llNetTotalOfBill = findViewById(R.id.llNetTotalOfBill);
        llTableDeatils = findViewById(R.id.llTableDeatils);
        llFiveCGST = findViewById(R.id.llFiveCGST);
        llFiveSGST = findViewById(R.id.llFiveSGST);
        llTwelveCGST = findViewById(R.id.llTwelveCGST);
        llTwelveSGST = findViewById(R.id.llTwelveSGST);
        llEighteenCGST = findViewById(R.id.llEighteenCGST);
        llEighteenSGST = findViewById(R.id.llEighteenSGST);
        llTwentyEightCGST = findViewById(R.id.llTwentyEightCGST);
        llTwentyEightSGST = findViewById(R.id.llTwentyEightSGST);
        tvFiveCGSTAmount = findViewById(R.id.tvFiveCGSTAmount);
        tvFiveSGSTAmount = findViewById(R.id.tvFiveSGSTAmount);
        tvTwelveCGSTAmount = findViewById(R.id.tvTwelveCGSTAmount);
        tvTwelveSGSTAmount = findViewById(R.id.tvTwelveSGSTAmount);
        tvEighteenCGSTAmount = findViewById(R.id.tvEighteenCGSTAmount);
        tvEighteenSGSTAmount = findViewById(R.id.tvEighteenSGSTAmount);
        tvTwentyEightCGSTAmount = findViewById(R.id.tvTwentyEightCGSTAmount);
        tvTwentyEightSGSTAmount = findViewById(R.id.tvTwentyEightSGSTAmount);


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

    private void checkForNumbers(List<String> itemList, Context context) {
        boolean is8Present = itemList.contains("8");
        boolean is9Present = itemList.contains("9");
        if (userValue != null) {
            if (userValue.matches("1")) {
                if (is8Present) {
                    kotNUmber = "A-" + counting;
                } else if (is9Present) {
                    kotNUmber = "N-" + counting;
                } else {
                    kotNUmber = "F-" + counting;
                }
            } else if (userValue.matches("2")) {
                kotNUmber = "R-" + counting;
            }
        } else {
            Toast.makeText(this, "userValue is null", Toast.LENGTH_SHORT).show();
        }

    }


    private void exampleUsage() {
        checkForNumbers(filteredItemGroupBill, this);
    }

    public void BillData() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.new_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ConnectionHelper connectionHelper = new ConnectionHelper(this);
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

    public void DiscountData(List<String> itemCodeList) {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.new_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

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
                                llDiscount.setVisibility(View.VISIBLE);
                                double percentage = 0.05;
                                result = totalSP * percentage;
                                newTotalSP = totalSP - result;
                                resultString = String.valueOf(result);
                                totalSPBigDec = new BigDecimal(String.valueOf(resultString)).setScale(2, RoundingMode.HALF_UP);

                                tvDiscountText.setText("Discount " + "(5% on " + "\u20B9 " + totalSP + ")");
                                tvDiscount.setText("- " + "\u20B9 " + totalSPBigDec);
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
                progressDialog.dismiss(); // Hide the ProgressBar when done

            }
        } else {
            // Connection failed
            Log.e("dhzffh", "Failed to connect to the database");
        }
    }

    void databaseData() {


//        Toast.makeText(this, "A " + Aid + " ,C " + counting, Toast.LENGTH_SHORT).show();


        if (Aid != 0) {

            if (kotNUmber != null) {
//                Toast.makeText(this, "Kqnt for Aid " + Aid + ": " + kqntValue, Toast.LENGTH_SHORT).show();

                insertBillData();

            } else {

                Toast.makeText(this, "No data found for Aid " + Aid, Toast.LENGTH_SHORT).show();
            }

        } else if (counting != 0) {


            if (kotNUmber != null) {
//                Toast.makeText(this, "Kqnt for counting " + counting + ": " + kqntValue, Toast.LENGTH_SHORT).show();
//                printText();
                insertBillData();

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
            format.setTextSize(22);
            format.setStyle(PrnTextStyle.NORMAL);
            format.setAli(Layout.Alignment.ALIGN_CENTER);

            mPrinter.setPrintAppendString("H-Block, Palam Vihar, Gurgaon-122017", format);
            mPrinter.setPrintAppendString("Phone: 0124-2365228 ,4554700", format);
            mPrinter.setPrintAppendString("GST#06AAAAD4814C1ZD", format);

            printHorizontalLine(43, format);

            format.setTextSize(22);
            format.setStyle(PrnTextStyle.NORMAL);
            format.setAli(Layout.Alignment.ALIGN_NORMAL);
            String billNo = "Bill No.";
            String billNoP = String.valueOf(BillNoFinal);
            int billNoPartL = 35;
            int spacesBeforeColon = billNoPartL - billNo.length();
            String formatStringBillNo = billNo + String.format("%" + spacesBeforeColon + "s", ":  ") + billNoP;
            mPrinter.setPrintAppendString(formatStringBillNo, format);
            String billDate = "Bill Date";
            String billDateP = currentDateTime;
            int billDatePartL = 34;
            int spacesBeforeColonDate = billDatePartL - billDate.length();
            String formatStringBillDate = billDate + String.format("%" + spacesBeforeColonDate + "s", ":  ") + billDateP;
            mPrinter.setPrintAppendString(formatStringBillDate, format);


            String merchantId = "Member_ID";
            String memberId = MemberId;
            int merchantL = 25;
            int spacesBeforeColonmerchant = merchantL - merchantId.length();
            String formatStringMerchant = merchantId + String.format("%" + spacesBeforeColonmerchant + "s", ":  ")
                + memberId;
            mPrinter.setPrintAppendString(formatStringMerchant, format);

            String merchantName = "MEMBER NAME";
            String mName = nameFinal;
            int merchantNameL = 15;
            int spacesBeforeColonmerchantName = merchantNameL - merchantName.length();
            String formatStringmerchantName = merchantName + String.format("%" + spacesBeforeColonmerchantName +
                "s", ":  ") + mName;
            mPrinter.setPrintAppendString(formatStringmerchantName, format);

            String Location = "Location";
            String LocationP = locationName;
            int LocationL = 30;
            int spacesBeforeColonL = LocationL - Location.length();
            String formatStringLocation = Location + String.format("%" + spacesBeforeColonL + "s", ":  ") + LocationP;
            mPrinter.setPrintAppendString(formatStringLocation, format);
            Log.e("spaceBill", spacesBeforeColonL + " " + spacesBeforeColon);

            String tableNo = "Table No";
            String tNo = tableId;
            int tableNameL = 30;
            int spacesBeforeColontableName = tableNameL - tableNo.length();
            String formatStringtableName = tableNo + String.format("%" + spacesBeforeColontableName +
                "s", ":  ") + tNo;
            mPrinter.setPrintAppendString(formatStringtableName, format);

            String PayModeNo = "Pay Mode";
            String PayNo = "Smart Card";
            int tPayNameL = 27;
            int spacesBeforeColonPayName = tPayNameL - PayModeNo.length();
            String formatStringPayName = PayModeNo + String.format("%" + spacesBeforeColonPayName +
                "s", ":  ") + PayNo;
            mPrinter.setPrintAppendString(formatStringPayName, format);

            String amountca = "Opening Balance ";
            String cardamount = "\u20B9 " + cardAmount;
            int cardamountL = 20;
            int spacesBeforeColoncardamount = cardamountL - amountca.length();
            String formatStringcardamount = amountca + String.format("%" + spacesBeforeColoncardamount +
                "s", ":  ") + cardamount;

            printHorizontalLine(43, format);
            mPrinter.setPrintAppendString(formatStringcardamount, format);
            printHorizontalLine(43, format);


            String leftPartF = "Item Name ";
            String ratePartF = "Qty";
            String rightPartF = "Amount";
            String middlePartF = "Rate";
            int totalLengthF = 53;

            int leftPartLengthF = leftPartF.length();
            int middlePartLengthF = middlePartF.length();
            int ratePartLengthF = ratePartF.length();
            int rightPartLength = rightPartF.length();

            int spacesBetweenLeftAndMiddle = 10; // Adjust as needed for readability
            int spacesBetweenMiddleAndRate = 10;  // Adjust as needed for readability
            int spacesNeededForRightPart = totalLengthF - leftPartLengthF - middlePartLengthF
                - ratePartLengthF - spacesBetweenLeftAndMiddle - spacesBetweenMiddleAndRate;

            // Create the formatted string
            String formattedStringF = String.format("S.No  %s%" + spacesBetweenLeftAndMiddle +
                "s%" + spacesBetweenMiddleAndRate + "s%" + spacesNeededForRightPart +
                "s", leftPartF, middlePartF, ratePartF, rightPartF);

            // Append the formatted string to the printer
            mPrinter.setPrintAppendString(formattedStringF, format);

            double GrandTotal=0.0;
            double amount = 0.0,amount1 = 0.0,productTotalAmount=0, totalAmount1 = 0.0,totalAmount = 0.0, sum = 0.0,
                sumItem = 0.0, cgstPercentage = 0, sgstPercentage= 0;
            BigDecimal roundedGrandGst = null,roundedGrandGst1 = null;
            if (model_occupiedTable != null) {
                int count = 1; // Initialize counter outside the loop

                for (Model_OccupiedTable item : model_occupiedTable) {
                    Log.d("Model_OccupiedTable", "Item #" + count + ": ItemName: " + item.getIteName() +
                        ", Rate: " + item.getItePrice() +
                        ", Qty: " + item.getIteQnt() +
                        ", GST: " + item.getItemSaletaxcode() +
                        ", itemcode: " + item.getItemCode());
                    int qnt = (int) Double.parseDouble(item.getIteQnt());
                    int totalLengthofline = 60;
                    GrandTotal = Double.parseDouble(item.getItePrice()) * Double.parseDouble(item.getIteQnt());
                    format.setTextSize(21);
                    format.setStyle(PrnTextStyle.NORMAL);
                    format.setAli(Layout.Alignment.ALIGN_NORMAL);
                    mPrinter.setPrintAppendString("  "+count+"        "+item.getIteName() , format);
                    // Use Double.parseDouble(item1) instead of Integer.parseInt(item1)

                    format.setTextSize(21);
                    format.setStyle(PrnTextStyle.NORMAL);
                    format.setAli(Layout.Alignment.ALIGN_OPPOSITE);

                    mPrinter.setPrintAppendString( item.getItePrice() + "     "+qnt + "pc  "
                        + "         "+ ("\u20B9 " + Double.parseDouble(item.getItePrice()) * Double.parseDouble(item.getIteQnt())), format);

                    sumItem += GrandTotal;

                    count++; // Increment counter for the next iteration
                }
            } else {
                Log.d("Model_OccupiedTable", "model_occupiedTable is null");
            }
            printHorizontalLine(43, format);

            format.setTextSize(22);
            format.setStyle(PrnTextStyle.NORMAL);
            format.setAli(Layout.Alignment.ALIGN_NORMAL);

            String netTotalLabel1 = "Sub Total ";
            String netTotalValue = "\u20B9 " + totalAmountList;
            mPrinter.setPrintAppendString(netTotalLabel1 + String.format("%" + (66 - netTotalLabel1.length()) + "s", "") + netTotalValue, format);
            for (Model_OccupiedTable item1 : model_occupiedTable) {

                double gst = Double.parseDouble(item1.getItemSaletaxcode());
                double cgstPercentage1 = gst / 2;
                double sgstPercentage1 = gst / 2;
                Log.e("dataofPRint",gst+" "+cgstPercentage1);
                double amountValue = Double.parseDouble(item1.getItePrice());
                double amountto = amountValue * Double.parseDouble(item1.getIteQnt());
                double amountro = (amountto * gst) / 100;
                double totalAmountround = amountro / 2;


                BigDecimal roundedGrand = new BigDecimal(totalAmountround).setScale(2, RoundingMode.HALF_UP);


            }
            if (check1==1){
                String CGST = "CGST  @  "+"2.5%" ;
                String CGSTTotal = "\u20B9 " + roundedFinaltotalAmountGSTFifthHalf;
                int CGSTL = 60;
                int spacesBeforeColonCGST = CGSTL - CGST.length();
                String formatStringCGST = CGST + String.format("%" + spacesBeforeColonCGST +
                    "s", "") + CGSTTotal;
                mPrinter.setPrintAppendString(formatStringCGST, format);

                String SGST = "SGST  @  "+"2.5%" ;
                String SGSTTotal = "\u20B9 " + roundedFinaltotalAmountGSTFifthHalf;
                int SGSTL = 60;
                int spacesBeforeColonSGST = SGSTL - SGST.length();
                String formatStringSGST = SGST + String.format("%" + spacesBeforeColonSGST +
                    "s", "") + SGSTTotal;
                mPrinter.setPrintAppendString(formatStringSGST, format);
            }
            if (check2==1){
                String CGST = "CGST  @  "+"6%" ;
                String CGSTTotal = "\u20B9 " + roundedFinaltotalAmountGSTtwelveHalf;
                int CGSTL = 60;
                int spacesBeforeColonCGST = CGSTL - CGST.length();
                String formatStringCGST = CGST + String.format("%" + spacesBeforeColonCGST +
                    "s", "") + CGSTTotal;
                mPrinter.setPrintAppendString(formatStringCGST, format);

                String SGST = "SGST  @  "+"6%" ;
                String SGSTTotal = "\u20B9 " + roundedFinaltotalAmountGSTtwelveHalf;
                int SGSTL = 60;
                int spacesBeforeColonSGST = SGSTL - SGST.length();
                String formatStringSGST = SGST + String.format("%" + spacesBeforeColonSGST +
                    "s", "") + SGSTTotal;
                mPrinter.setPrintAppendString(formatStringSGST, format);
            }
            if (check3==1){
                String CGST = "CGST  @  "+"9%" ;
                String CGSTTotal = "\u20B9 " + roundedFinaltotalAmountGSTEighteenHalf;
                int CGSTL = 60;
                int spacesBeforeColonCGST = CGSTL - CGST.length();
                String formatStringCGST = CGST + String.format("%" + spacesBeforeColonCGST +
                    "s", "") + CGSTTotal;
                mPrinter.setPrintAppendString(formatStringCGST, format);

                String SGST = "SGST  @  "+"9%" ;
                String SGSTTotal = "\u20B9 " + roundedFinaltotalAmountGSTEighteenHalf;
                int SGSTL = 60;
                int spacesBeforeColonSGST = SGSTL - SGST.length();
                String formatStringSGST = SGST + String.format("%" + spacesBeforeColonSGST +
                    "s", "") + SGSTTotal;
                mPrinter.setPrintAppendString(formatStringSGST, format);
            }
            if (check4==1){
                String CGST = "CGST  @  "+"14%" ;
                String CGSTTotal = "\u20B9 " + roundedFinaltotalAmountGSTtwentyEightHalf;
                int CGSTL = 60;
                int spacesBeforeColonCGST = CGSTL - CGST.length();
                String formatStringCGST = CGST + String.format("%" + spacesBeforeColonCGST +
                    "s", "") + CGSTTotal;
                mPrinter.setPrintAppendString(formatStringCGST, format);

                String SGST = "SGST  @  "+"14%" ;
                String SGSTTotal = "\u20B9 " + roundedFinaltotalAmountGSTtwentyEightHalf;
                int SGSTL = 60;
                int spacesBeforeColonSGST = SGSTL - SGST.length();
                String formatStringSGST = SGST + String.format("%" + spacesBeforeColonSGST +
                    "s", "") + SGSTTotal;
                mPrinter.setPrintAppendString(formatStringSGST, format);
            }



            Log.e("dataofAdapter",  "  " + roundedGrand);


            format.setTextSize(22);
            format.setStyle(PrnTextStyle.NORMAL);
            format.setAli(Layout.Alignment.ALIGN_NORMAL);
            String GST = "Total GST";
            String TotalGST = "\u20B9 " + roundedTotalGST;
            int GSTL = 63;
            int spacesBeforeColonGST = GSTL - GST.length();
            String formatStringGST = GST + String.format("%" + spacesBeforeColonGST +
                "s", " + ") + TotalGST;
            mPrinter.setPrintAppendString(formatStringGST, format);
            printHorizontalLine(43, format);
            String Pay = "Bill Amount";
            String ToPay = "\u20B9 " + roundedGrandList;
            int ToPayL = 62;
            int spacesBeforeColonPay = ToPayL - Pay.length();
            String formatStringPay = Pay + String.format("%" + spacesBeforeColonPay +
                "s", "   ") + ToPay;
            mPrinter.setPrintAppendString(formatStringPay, format);
            BigDecimal cardAmountDecimal = new BigDecimal(cardAmount);

            BigDecimal closingBalanceAfterBill = cardAmountDecimal.subtract(roundedGrandList);

            printHorizontalLine(43, format);
            String AmountAfter = "Closing Balance";
            // Assuming cardAmount is a String and sumItem is a double
            String ToPayAmountAfter = "\u20B9 " + (closingBalanceAfterBill);
            int AmountAfterL = 55;
            int spacesBeforeColonAmountAfter = AmountAfterL - AmountAfter.length();
            String formatStringAmountAfter = AmountAfter + String.format("%" + spacesBeforeColonAmountAfter +
                "s", "") + ToPayAmountAfter;
            mPrinter.setPrintAppendString(formatStringAmountAfter, format);

            format.setTextSize(26);
            format.setAli(Layout.Alignment.ALIGN_CENTER);
            format.setStyle(PrnTextStyle.BOLD);
            format.setFont(PrnTextFont.SANS_SERIF);
            printHorizontalLine(43, format);
            mPrinter.setPrintAppendString("Thank You !", format);
            printHorizontalLine(43, format);

            mPrinter.setPrintAppendString("       " + " ", format);
            mPrinter.setPrintAppendString("       " + " ", format);

            format.setAli(Layout.Alignment.ALIGN_NORMAL);
            format.setStyle(PrnTextStyle.NORMAL);
            format.setTextSize(25);
            printHorizontalLine(43, format);
            printStatus = mPrinter.setPrintStart();

            int tOrder = 0;
//            updateTableOrder(Integer.parseInt(tableId), tOrder, locationID);
//            databaseDataClear();
            exitCount = 1;
        }
    }


    private void showCustomAlertDialog() {

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.exit_custom_layout, null);

        RelativeLayout rlCancel = dialogView.findViewById(R.id.rlCancel);
        RelativeLayout rlPrint = dialogView.findViewById(R.id.rlPrint);
//        RelativeLayout rlExit = dialogView.findViewById(R.id.rlExit);

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
//        rlExit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (exitCount == 0) {
//                    Toast.makeText(Bill_Activity.this, "First Print the Bill Receipt", Toast.LENGTH_SHORT).show();
//                } else {
//                    Intent intent = new Intent(Bill_Activity.this, MainMenu.class);
//                    startActivity(intent);
//                    finish();
//                }
//
//            }
//        });

        alertDialog.show();
    }


    public void UpdateBillAmount() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.new_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ConnectionHelper connectionHelper = new ConnectionHelper(this);
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

        try (Connection connect = new ConnectionHelper(this).connectionclass()) {
            if (connect != null) {
                // Connected to the database
                System.out.println("Connected to the database");
                Log.e("ergeg", "Connected to the database");

                // Create a SQL query to insert data into the table
                // Create a SQL query to insert data into the table
                String insertQuery = "INSERT INTO TI_BarBillHead (BillNo, LocationCode, MemberId," +
                    " TableId, YearCode, MemberType, WaiterId, UserCode, BillDate, CreationDate," +
                    " ModificationDate,amount, OpeningBalance,ClosingBalance,Couponno,NoOfGuest," +
                    "modeofpayment,PAX,BillStatus,BillType,RoundOff,CurrentOutStanding,printbalance) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?)";

                try (PreparedStatement preparedStatement = connect.prepareStatement(insertQuery)) {
                    // Set values for the parameters
//                    preparedStatement.setString(1, String.valueOf(counting));
                    preparedStatement.setString(1, kotNUmber);
                    preparedStatement.setString(2, locationID);
                    preparedStatement.setString(3, memberID);
                    preparedStatement.setString(4, tableId);
                    preparedStatement.setInt(5, yearCode);

                    // Replace 'YourMemberTypeValue' with the appropriate value for MemberType
                    preparedStatement.setString(6, memberType);

                    preparedStatement.setString(7, waiterID);
                    preparedStatement.setString(8, cashierCode);
                    preparedStatement.setString(9, currentDateTime);
                    preparedStatement.setString(10, currentDateTime);
                    preparedStatement.setString(11, currentDateTime);
                    if (discountCheck == 1) {
                        preparedStatement.setString(12, String.valueOf(newGrandTotalSP));

                    } else {
                        preparedStatement.setString(12, String.valueOf(roundedGrand));

                    }
                    preparedStatement.setString(13, String.valueOf(cardAmount));
                    preparedStatement.setString(14, String.valueOf(grandAmount));
                    preparedStatement.setString(15, couponNo);
                    preparedStatement.setString(16, "");
                    preparedStatement.setString(17, "Card");
                    preparedStatement.setString(18, "1");
                    preparedStatement.setString(19, "1");
                    preparedStatement.setString(20, "0");
                    preparedStatement.setString(21, "0");
                    preparedStatement.setString(22, "0");
                    preparedStatement.setString(23, "Yes");

                    // Execute the update
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Data inserted successfully");
                        insertDataBillBody();
                        Log.e("ergeg", "Data inserted successfully");

                    } else {
                        System.out.println("Failed to insert data");
                        Log.e("ergeg", "Failed to insert data");
                        Toast.makeText(this, "Failed to insert data", Toast.LENGTH_SHORT).show();

                    }
                } catch (SQLException e) {
                    // Handle SQL exceptions
                    e.printStackTrace();
                    System.out.println("Failed to execute SQL update");
                    Toast.makeText(this, "Failed to insert data. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

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

    public void insertDataBillBody() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.new_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

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
                    // Set values for the parameters
//                    preparedStatement.setString(1, String.valueOf(counting));
                    preparedStatement.setString(1, kotNUmber);
                    preparedStatement.setString(2, String.valueOf(Aid));
                    preparedStatement.setString(3, currentDateTime);
                    preparedStatement.setString(4, currentDateTime);
                    preparedStatement.setInt(5, Integer.parseInt(cashierCode));

                    // Replace 'YourMemberTypeValue' with the appropriate value for MemberType
                    preparedStatement.setString(6, String.valueOf(yearCode));


                    // Execute the update
                    int rowsAffected = preparedStatement.executeUpdate();
                    BillDatabase db = Room.databaseBuilder(getApplicationContext(),
                        BillDatabase.class, "bill_db").allowMainThreadQueries().build();
                    BillDao countDao = db.BillDao();

                    if (rowsAffected > 0) {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Bill Print", Toast.LENGTH_SHORT).show();
                        Log.e("sfadfaf", "Data inserted successfully");
                        printText();
                        UpdateBillAmount();

                        databaseDataClear();

                        checkOut = 1;
                        if (checkOut == 1) {
                            if (Aid != 0) {
                                countDao.deleteById(Aid);
                            } else if (counting != 0) {
                                countDao.deleteById(counting);
                            }

                        }
                        Intent intent = new Intent(Bill_Activity.this, MainMenu.class);
                        startActivity(intent);
                        finish();
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

    private boolean isBillNoExist(String counting) throws SQLException {
        ConnectionHelper connectionHelper = new ConnectionHelper(this);
        try (Connection connect = connectionHelper.connectionclass()) {
            if (connect != null) {
                Log.e("ewwergwer", "Connected to the database");
                String query = "SELECT COUNT(*) FROM TI_BarBillHead WHERE BillNo = ?";
                try (PreparedStatement preparedStatement = connect.prepareStatement(query)) {
                    preparedStatement.setString(1, counting);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            int count = resultSet.getInt(1);
                            return count > 0;
                        }
                    }
                }
            } else {
                Log.e("ewwergwer", "Failed to connect to the database");
            }
            return false;
        } catch (SQLException e) {
            Log.e("ewwergwer", "Failed to check data. Error: " + e.getMessage());
            throw e;
        }
    }

    public void getSchemeHeadData(String locationCode) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.new_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Get current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(new Date());
        idList = new ArrayList<>();
        try (Connection connect = new ConnectionHelper(this).connectionclass()) {
            if (connect != null) {
                // Connected to the database
                System.out.println("Connected to the database");
                Log.e("qrfgfefg", "Connected to the database" + " " + locationCode);

                // Create a SQL query to select data from the table
                String selectQuery = "SELECT * FROM TM_SchemeHead WHERE Status = 'Y' AND LocationCode = ? AND FromDate <= ? AND ToDate >= ?";

                try (PreparedStatement preparedStatement = connect.prepareStatement(selectQuery)) {
                    preparedStatement.setString(1, locationCode); // Set the LocationCode
                    preparedStatement.setString(2, currentDate.toString()); // Set current date as FromDate
                    preparedStatement.setString(3, currentDate.toString()); // Set current date as ToDate

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            progressDialog.dismiss();
                            // Retrieve data from the result set
                            String ID = resultSet.getString("ID");
                            String AppliedOn = resultSet.getString("AppliedOn");
                            String SchemeType = resultSet.getString("SchemeType");
                            idList.add(ID);

                            Log.e("qrfgfefg", "ID: " + ID + ", AppliedOn: " + AppliedOn + ", SchemeType: " + SchemeType + ", idList: " + idList);
                        }
                    }
                } catch (SQLException e) {
                    // Handle SQL exceptions
                    e.printStackTrace();
                    System.out.println("Failed to execute SQL select: " + e.getMessage());
                    Log.e("qrfgfefg", "Failed to execute SQL select: " + e.getMessage());
                }
            } else {
                // Connection failed
                System.out.println("Failed to connect to the database");
                Log.e("qrfgfefg", "Failed to connect to the database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
//                            cardAmountData(MemberId);
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

    public void getCardData(String memberId) {
        ConnectionHelper connectionHelper = new ConnectionHelper(this);
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {
            // Connection successful
            // Proceed with AsyncTask to fetch data from the database
            Log.e("fjgfhfhg", "Connected to the database");

            new Bill_Activity.FetchCardDataTask().execute(connect, memberId);
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
            nameFinal = name;
            // Update UI with the fetched data
            if (name != null) {
                runOnUiThread(() -> {
                    llTableDeatils.setVisibility(View.VISIBLE);
                    tvMemberId.setText(memberId); // Set memberId to tvMemberId
                    tvName.setText(name);
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

            new Bill_Activity.FetchBarBillDataTask().execute(connect, billNoFinal);
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
            progressDialog = new ProgressDialog(Bill_Activity.this);
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
                getBarKOTBody(kotNumbers);
                runOnUiThread(() -> {
                    tvKOTNo.setText(String.valueOf(kotNumbers));
                });

            } else {
                runOnUiThread(() -> {
                    tv_Information.setText("Bill not available for this table");
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
                model_gsts = new ArrayList<>();

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
                                    Model_OccupiedTable menu = new Model_OccupiedTable(ItemName, Rate, Qty, GST, itemcode);
                                    model_occupiedTable.add(menu);
//                                    tv_myc.setText("ItemName: " + ItemName +
//                                        ", Rate: " +  Rate + ", Qty: " + Qty +
//                                        ", GST: " + GST +
//                                        ", itemcode: " + itemcode );
                                    Log.e("hergeg", "ItemName: " + ItemName +
                                        ", Rate: " + Rate + ", Qty: " + Qty +
                                        ", GST: " + GST +
                                        ", itemcode: " + itemcode);
                                    double qty = Double.parseDouble(Qty);

                                    double amountValue = Double.parseDouble(Rate);
                                    double amount = amountValue * qty;
                                    totalAmountList += amount;

                                    int gstValue = (int) Double.parseDouble(GST); // Convert GST string to an integer

                                    switch (gstValue) {
                                        case 0:
                                            totalAmountGSTZero += amount;
                                            Model_GST menu1  = new Model_GST(GST, Rate, Qty);
                                            model_gsts.add(menu1);

                                            break;
                                        case 5:
                                            totalAmountGSTFifth += amount;
                                            Model_GST menu2 = new Model_GST(GST, Rate, Qty);
                                            model_gsts.add(menu2);
                                            llFiveCGST.setVisibility(View.VISIBLE);
                                            llFiveSGST.setVisibility(View.VISIBLE);
                                            check1=1;
                                            break;
                                        case 12:
                                            totalAmountGSTtwelve += amount;
                                            Model_GST menu3 = new Model_GST(GST, Rate, Qty);
                                            model_gsts.add(menu3);
                                            llTwelveCGST.setVisibility(View.VISIBLE);
                                            llTwelveSGST.setVisibility(View.VISIBLE);
                                            check2=1;
                                            break;
                                        case 18:
                                            totalAmountGSTEighteen += amount;
                                            Model_GST menu4 = new Model_GST(GST, Rate, Qty);
                                            model_gsts.add(menu4);
                                            llEighteenCGST.setVisibility(View.VISIBLE);
                                            llEighteenSGST.setVisibility(View.VISIBLE);
                                            check3=1;
                                            break;
                                        case 28:
                                            totalAmountGSTtwentyEight += amount;
                                            Model_GST menu5 = new Model_GST(GST, Rate, Qty);
                                            model_gsts.add(menu5);
                                            llTwentyEightCGST.setVisibility(View.VISIBLE);
                                            llTwentyEightSGST.setVisibility(View.VISIBLE);
                                            check4=1;
                                            break;
                                        default:

                                            break;
                                    }

                                    Log.d("hergeg",
                                        "totalAmountList: " + totalAmountList + " totalAmountGSTZero " + totalAmountGSTZero +
                                            " totalAmountGSTtwelve " + totalAmountGSTtwelve + " totalAmountGSTEighteen " + totalAmountGSTEighteen +
                                            " totalAmountGSTtwentyEight " + totalAmountGSTtwentyEight + " Amount: " + amount + " ItemName: " + ItemName +
                                            ", Rate: " + Rate + ", Qty: " + Qty + ", GST: " + GST + ", itemcode: " + itemcode);

                                }

                            }
                        }
                    }

                    // Set adapters after processing all KOT numbers
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        double FinaltotalAmountGSTFifth = totalAmountGSTFifth * 0.05;
                        double FinaltotalAmountGSTtwelve = totalAmountGSTtwelve * 0.12;
                        double FinaltotalAmountGSTEighteen = totalAmountGSTEighteen * 0.18;
                        double FinaltotalAmountGSTtwentyEight = totalAmountGSTtwentyEight * 0.28;

                        double FinaltotalAmountGSTFifthHalf = FinaltotalAmountGSTFifth / 2.0;
                        double FinaltotalAmountGSTtwelveHalf = FinaltotalAmountGSTtwelve / 2.0;
                        double FinaltotalAmountGSTEighteenHalf = FinaltotalAmountGSTEighteen / 2.0;
                        double FinaltotalAmountGSTtwentyEightHalf = FinaltotalAmountGSTtwentyEight / 2.0;

                        BigDecimal roundedFinaltotalAmountGSTFifth = BigDecimal.valueOf(FinaltotalAmountGSTFifth).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal roundedFinaltotalAmountGSTtwelve = BigDecimal.valueOf(FinaltotalAmountGSTtwelve).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal roundedFinaltotalAmountGSTEighteen = BigDecimal.valueOf(FinaltotalAmountGSTEighteen).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal roundedFinaltotalAmountGSTtwentyEight = BigDecimal.valueOf(FinaltotalAmountGSTtwentyEight).setScale(2, RoundingMode.HALF_UP);

                          roundedFinaltotalAmountGSTFifthHalf = BigDecimal.
                            valueOf(FinaltotalAmountGSTFifthHalf).setScale(2, RoundingMode.HALF_UP);
                          roundedFinaltotalAmountGSTtwelveHalf = BigDecimal.valueOf(FinaltotalAmountGSTtwelveHalf).setScale(2, RoundingMode.HALF_UP);
                          roundedFinaltotalAmountGSTEighteenHalf = BigDecimal.valueOf(FinaltotalAmountGSTEighteenHalf).setScale(2, RoundingMode.HALF_UP);
                          roundedFinaltotalAmountGSTtwentyEightHalf = BigDecimal.valueOf(FinaltotalAmountGSTtwentyEightHalf).setScale(2, RoundingMode.HALF_UP);
tvFiveCGSTAmount.setText(""+roundedFinaltotalAmountGSTFifthHalf);
tvFiveSGSTAmount.setText(""+roundedFinaltotalAmountGSTFifthHalf);
tvTwelveCGSTAmount.setText(""+roundedFinaltotalAmountGSTtwelveHalf);
tvTwelveSGSTAmount.setText(""+roundedFinaltotalAmountGSTtwelveHalf);
tvEighteenCGSTAmount.setText(""+roundedFinaltotalAmountGSTEighteenHalf);
tvEighteenSGSTAmount.setText(""+roundedFinaltotalAmountGSTEighteenHalf);
tvTwentyEightCGSTAmount.setText(""+roundedFinaltotalAmountGSTtwentyEightHalf);
tvTwentyEightSGSTAmount.setText(""+roundedFinaltotalAmountGSTtwentyEightHalf);


                          roundedTotalGST = roundedFinaltotalAmountGSTFifth.add(roundedFinaltotalAmountGSTtwelve)
                            .add(roundedFinaltotalAmountGSTEighteen).add(roundedFinaltotalAmountGSTtwentyEight);
                        tvTotalOfGovTx.setText(""+roundedTotalGST);

                        Log.d("DecimalDataofGST",
                            "roundedFinaltotalAmountGSTFifth: " + roundedFinaltotalAmountGSTFifth +
                                " roundedFinaltotalAmountGSTtwelve " + roundedFinaltotalAmountGSTtwelve +
                                " roundedFinaltotalAmountGSTEighteen " + roundedFinaltotalAmountGSTEighteen +
                                " roundedFinaltotalAmountGSTtwentyEight " + roundedFinaltotalAmountGSTtwentyEight
                                + "   Half Data  " + "roundedFinaltotalAmountGSTFifthHalf: " + roundedFinaltotalAmountGSTFifthHalf +
                                " roundedFinaltotalAmountGSTtwelveHalf " + roundedFinaltotalAmountGSTtwelveHalf +
                                " roundedFinaltotalAmountGSTEighteenHalf " + roundedFinaltotalAmountGSTEighteenHalf +
                                " roundedFinaltotalAmountGSTtwentyEightHalf " + roundedFinaltotalAmountGSTtwentyEightHalf
                        );

                        Log.d("FinalDataofGST",
                            "FinaltotalAmountGSTFifth: " + FinaltotalAmountGSTFifth +
                                " FinaltotalAmountGSTtwelve " + FinaltotalAmountGSTtwelve +
                                " FinaltotalAmountGSTEighteen " + FinaltotalAmountGSTEighteen +
                                " FinaltotalAmountGSTtwentyEight " + FinaltotalAmountGSTtwentyEight
                                + "   Half Data  " + "FinaltotalAmountGSTFifthHalf: " + FinaltotalAmountGSTFifthHalf +
                                " FinaltotalAmountGSTtwelveHalf " + FinaltotalAmountGSTtwelveHalf +
                                " FinaltotalAmountGSTEighteenHalf " + FinaltotalAmountGSTEighteenHalf +
                                " FinaltotalAmountGSTtwentyEightHalf " + FinaltotalAmountGSTtwentyEightHalf +
                                "   roundedTotalGST " + roundedTotalGST

                        );


//                        compareItemCodes(model_occupiedTable,matchedIdList);
                        rlLayout.setVisibility(View.VISIBLE);
                        tvText.setVisibility(View.GONE);
                        rvBill.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        adapter_bill = new Adapter_Bill(getApplicationContext(), model_occupiedTable, tvTotalOfBill);
                        rvBill.setAdapter(adapter_bill);

                        rvGST.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        adapter_gst = new Adapter_GST(getApplicationContext(), model_gsts);
                        rvGST.setAdapter(adapter_gst);


                    });
                    if (model_occupiedTable != null) {
                        for (Model_OccupiedTable item : model_occupiedTable) {
                            Log.d("Model_OccupiedTable", "ItemName: " + item.getIteName() +
                                ", Rate: " + item.getItePrice() +
                                ", Qty: " + item.getIteQnt() +
                                ", GST: " + item.getItemSaletaxcode() +
                                ", itemcode: " + item.getItemCode());
//                                getItemMasterData(item.getItemCode());
                            itemCodeList.add(item.getItemCode());
                            double amount = 0.0, amount1 = 0.0, totalAmountFinal = 0.0;

// Declare an ArrayList to store totalAmountFinal values


// Calculate CGST and SGST amounts
                            double gst = Double.parseDouble(item.getItemSaletaxcode());


                            // Assuming live.getAmount() returns a numeric type (int or double)
                            double amountValue = Double.parseDouble(item.getItePrice());
                            amount1 = amountValue * Double.parseDouble(item.getIteQnt());
                            amount = (amount1 * gst) / 100;

                            totalAmountFinal += amount;
                            totalAmountFinalList.add(totalAmountFinal);

                            BigDecimal roundedGrand = new BigDecimal(String.valueOf(totalAmountFinal)).setScale(2, RoundingMode.HALF_UP);
                            Log.d("Model_OccupiedTable", String.valueOf(roundedGrand));


                        }
                    } else {
                        Log.d("Model_OccupiedTable", "model_occupiedTable is null");
                    }
                    for (Double amountDouble : totalAmountFinalList) {
                        // Do something with 'amount'
                        System.out.println(amountDouble); // For example, print each amount
                        totalAmountFinalDouble += amountDouble;
                        roundedGrandDouble = new BigDecimal(String.valueOf(totalAmountFinalDouble)).setScale(2, RoundingMode.HALF_UP);
                        roundedGrandList = new BigDecimal(String.valueOf(totalAmountFinalDouble + totalAmountList)).setScale(2, RoundingMode.HALF_UP);

                        Log.d("totalAmountFinalDouble", String.valueOf(roundedGrandDouble) + " " + roundedGrandList);

                    }

                    runOnUiThread(() -> {

//                        tvTotalOfGovTx.setText(String.valueOf("+ \u20B9 " + roundedGrandDouble)); // Set total for unmatched items
                        tvToPay.setText(String.valueOf("\u20B9 " + roundedGrandList)); // Set total for unmatched items
                        tvGrandTotal.setText(String.valueOf("\u20B9 " + roundedGrandList)); // Set total for unmatched items

                    });
                    BigDecimal cardAmountDecimal = new BigDecimal(cardAmount);

                    if (roundedGrandList.compareTo(cardAmountDecimal) > 0) {
                        // newGrandTotalSP is greater than cardAmount
                        Toast.makeText(this, "Card balance is " + cardAmount + "\n Which is insufficient to cover the Bill amount\n Please Recharge Card", Toast.LENGTH_LONG).show();
                        rlPaid.setVisibility(View.GONE);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 5000); // 5000 milliseconds = 5 seconds
                    } else if (roundedGrandList.compareTo(cardAmountDecimal) < 0) {
                        // newGrandTotalSP is less than cardAmount
                        rlPaid.setVisibility(View.VISIBLE);
                    } else {
                        // newGrandTotalSP is equal to cardAmount
                        rlPaid.setVisibility(View.VISIBLE);
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

    public void getItemMasterData(String itemCode) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.new_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Get current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(new Date());

        try (Connection connect = new ConnectionHelper(this).connectionclass()) {
            if (connect != null) {
                // Connected to the database
                Log.e("zsaffsf", "Connected to the database");

                // Create a SQL query to select data from the table
                String selectQuery = "SELECT * FROM ST_ItemMaster WHERE ItemCode = ? ";

                try (PreparedStatement preparedStatement = connect.prepareStatement(selectQuery)) {
                    preparedStatement.setString(1, itemCode); // Set the ItemCode

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            progressDialog.dismiss();
                            // Retrieve data from the result set
                            String itemGroup = resultSet.getString("ItemGroup");
                            String itemSubGroup = resultSet.getString("ItemSubGroup");

                            getSchemeBodyData(idList, itemCode, itemGroup, itemSubGroup);
                            Log.e("zsaffsf", " ItemCode: " + itemCode + " ItemGroup: " + itemGroup + ", ItemSubGroup: " + itemSubGroup);
                        }
                    }
                } catch (SQLException e) {
                    // Handle SQL exceptions
                    e.printStackTrace();
                    Log.e("zsaffsf", "Failed to execute SQL select: " + e.getMessage());
                }
            } else {
                // Connection failed
                Log.e("zsaffsf", "Failed to connect to the database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getSchemeBodyData(List<String> idList, String itemCode, String itemGroup, String itemSubGroup) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.new_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        try (Connection connect = new ConnectionHelper(this).connectionclass()) {
            if (connect != null) {
                // Connected to the database
                Log.e("iulkghjgh", "Connected to the database");

                // Create a SQL query to select data from the table
                String selectQuery = "SELECT * FROM TM_SchemeBody WHERE ID = ? AND ItemGroup = ? AND ItemSubGroup = ? ";

                try (PreparedStatement preparedStatement = connect.prepareStatement(selectQuery)) {
                    for (String id : idList) {
                        preparedStatement.setString(1, id); // Set the ID from the idList
                        preparedStatement.setString(2, itemGroup); // Set the ItemGroup
                        preparedStatement.setString(3, itemSubGroup); // Set the ItemSubGroup

                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                            while (resultSet.next()) {
                                // Retrieve data from the result set
                                matchedIdList.add(itemCode);
                                matchCount++;
                                Log.e("iulkghjgh", "Matched " + itemCode);
                            }
                        }
                    }
                } catch (SQLException e) {
                    // Handle SQL exceptions
                    e.printStackTrace();
                    Log.e("iulkghjgh", "Failed to execute SQL select: " + e.getMessage());
                } finally {
                    progressDialog.dismiss();
                }

                // Log the total count of matches
                Log.e("iulkghjgh", "Total matches: " + matchCount + " " + itemCode);
            } else {
                // Connection failed
                Log.e("iulkghjgh", "Failed to connect to the database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void compareItemCodes(ArrayList<Model_OccupiedTable> model_occupiedTable, List<String> matchedIdList) {
        double totalAmountFinalMatched = 0;
        double totalAmountFinalUnmatched = 0;
        double totalAmountItemsMatched = 0;
        double totalAmountItemsUnmatched = 0;
        double gst = 0;
        StringBuilder matchedData = new StringBuilder();
        StringBuilder unmatchedData = new StringBuilder();

        for (Model_OccupiedTable item : model_occupiedTable) {
            try {
                double amountValue = Double.parseDouble(item.getItePrice());
                gst = Double.parseDouble(item.getItemSaletaxcode());
                double quantity = Double.parseDouble(item.getIteQnt());

                double amountItems = amountValue * quantity;

                if (matchedIdList.contains(item.getItemCode())) {

                    totalAmountItemsMatched += amountItems;

                    Log.e("Innerdata", " " + item.getItemCode() + " totalAmountItemsMatched " + totalAmountItemsMatched);
                    disCheckInner = 1;
                    disCheckInner1 = 2;
                    llDiscount.setVisibility(View.VISIBLE);
                    llNetTotalOfBill.setVisibility(View.VISIBLE);
                } else {
                    double amount = amountValue * Double.parseDouble(item.getIteQnt()) * gst / 100 / 2;
//                    llNetTotalOfBill1.setVisibility(View.VISIBLE);
                    totalAmountItemsUnmatched += amountItems;
                    totalAmountFinalUnmatched += amount * 2;
                    Log.e("Innerdata", " " + item.getItemCode() + " amount " + amount + " totalAmountItemsUnmatched " + totalAmountItemsUnmatched + " totalAmountFinalUnmatched " + totalAmountFinalUnmatched);

                }
            } catch (NumberFormatException e) {
                // Handle parsing errors
                Log.e("Error", "Error parsing double value");
            }
        }
//        double discount = totalAmountItemsMatched * 0.05;
//        roundedDiscount = BigDecimal.valueOf(discount)
//            .setScale(2, RoundingMode.HALF_UP);
//
//        discountedAmount = totalAmountItemsMatched - discount;
//        double amount = discountedAmount *   gst / 100 / 2;
//        finalTotalAmountItemsMatched = totalAmountItemsMatched;
//        finalTotalAmountItemsUnmatched = totalAmountItemsUnmatched;
//        finalTotalAmount = totalAmountItemsUnmatched+discountedAmount ;
//        double gstAmount = finalTotalAmount * 0.05;
//
//        double gstAmountFinal=gstAmount/2.0;
//        dividedAmount = BigDecimal.valueOf(gstAmountFinal)
//            .setScale(2, RoundingMode.HALF_UP);
//
//        BigDecimal dividedAmountBigDecimal = BigDecimal.valueOf(gstAmountFinal);
//        BigDecimal multipliedAmount = dividedAmountBigDecimal.multiply(BigDecimal.valueOf(2));
//        roundedgstAmount = multipliedAmount.setScale(2, RoundingMode.HALF_UP);
//
//        runOnUiThread(() -> {
//            tvDiscountText.setText("Discount " + "(5% on " + "\u20B9 " + finalTotalAmountItemsMatched + ")");
//            tvDiscount.setText("- " + "\u20B9 " + roundedDiscount);
//            tvNetTotalOfBilltext.setText("Net Total After Discount on " +  "\u20B9 " + finalTotalAmountItemsMatched + ")");
//            tvNetTotalOfBill.setText("\u20B9 " + discountedAmount);
//            tvNetTotalwithoutDiscount.setText( "\u20B9 " + finalTotalAmountItemsUnmatched);
//            tvFinalNetTotal.setText("\u20B9 " + finalTotalAmount);
//            tvCGSTAmount.setText("\u20B9 " + dividedAmount);
//            tvSGSTAmount.setText("\u20B9 " +  dividedAmount);
//        });
//
//
//        BigDecimal roundedGrandMatched = BigDecimal.valueOf(totalAmountItemsMatched + totalAmountFinalMatched)
//            .setScale(2, RoundingMode.HALF_UP);
//        BigDecimal roundedGrandUnmatched = BigDecimal.valueOf(totalAmountItemsUnmatched + totalAmountFinalUnmatched)
//            .setScale(2, RoundingMode.HALF_UP);
//
//        BigDecimal cardAmountBigDecimal = new BigDecimal(String.valueOf(cardAmount));
//
//
//        roundedGrandFinalUnmatched = BigDecimal.valueOf(finalTotalAmount+gstAmount).setScale(2, RoundingMode.HALF_UP);
//        closingBalanceAfterBill = cardAmountBigDecimal.subtract(roundedGrandFinalUnmatched);
//        Log.e("Matched Data",  " "+cardAmount+ " " + cardAmountBigDecimal + " " + roundedGrandFinalUnmatched + " " + closingBalanceAfterBill);
//
//        runOnUiThread(() -> {
//            tvTotalOfGovTx.setText(String.valueOf("+ \u20B9 " + roundedgstAmount)); // Set total for unmatched items
//            tvToPay.setText(String.valueOf("\u20B9 " + roundedGrandFinalUnmatched)); // Set total to pay for unmatched items
//            tvGrandTotal.setText(String.valueOf("\u20B9 " + roundedGrandFinalUnmatched)); // Set grand total for unmatched items
//            handler = new Handler();
//            // Convert cardAmount string to BigDecimal
//            BigDecimal cardAmountDecimal = new BigDecimal(cardAmount);
//
//            if (roundedGrandFinalUnmatched != null && cardAmountDecimal != null) {
//                if (roundedGrandFinalUnmatched.compareTo(cardAmountDecimal) > 0) {
//                    // newGrandTotalSP is greater than cardAmount
//                    Toast.makeText(this, "Card balance is " + cardAmount + "\n Which is insufficient to cover the Bill amount\n Please Recharge Card", Toast.LENGTH_LONG).show();
//                    rlPaid.setVisibility(View.GONE);
//
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            finish();
//                        }
//                    }, 5000); // 5000 milliseconds = 5 seconds
//                } else if (roundedGrandFinalUnmatched.compareTo(cardAmountDecimal) < 0) {
//                    // newGrandTotalSP is less than cardAmount
//                    rlPaid.setVisibility(View.VISIBLE);
//                } else {
//                    // newGrandTotalSP is equal to cardAmount
//                    rlPaid.setVisibility(View.VISIBLE);
//                }
//            } else {
//                // Handle the case where one or both of the objects are null
//                Toast.makeText(this, "Null", Toast.LENGTH_SHORT).show();
//            }
//        });

    }
    public void yearCodeData() {
        ConnectionHelper connectionHelper = new ConnectionHelper(this);
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {
            try {
                // Construct the SQL query to select YEARCODE from the last row ordered by DATETO
                String query = "SELECT TOP 1 YEARCODE FROM [DSOI].[dbo].TM_FINANCIALYEAR ORDER BY DATETO DESC";

                // Create a statement
                Statement statement = connect.createStatement();

                // Execute the query
                ResultSet resultSet = statement.executeQuery(query);

                // Process the result set
                if (resultSet.next()) {
                    yearCode = resultSet.getInt("YEARCODE");
                    // Use the yearCode as needed
                    Log.e("djhhaaddgs", ""+yearCode);

                } else {
                    // No data found
                    Log.e("djhhaaddgs", "No data found in the TM_FINANCIALYEAR table");
                }

                // Close the connection and resources
                resultSet.close();
                statement.close();
                connect.close();
            } catch (Exception e) {
                // Handle the exception appropriately
                Log.e("djhhaaddgs", "Exception while retrieving data: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Connection failed
            Log.e("djhhaaddgs", "Failed to connect to the database");
        }
    }


}
