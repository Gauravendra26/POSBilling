package de.syss.MifareClassicTool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.syss.MifareClassicTool.Activities.MainMenu;
import de.syss.MifareClassicTool.Room.Bill;
import de.syss.MifareClassicTool.Room.BillDao;
import de.syss.MifareClassicTool.Room.BillDatabase;

public class OccupiedTables_Activity extends AppCompatActivity implements Adapter_OccRecyclerview.ProductClick {
    RecyclerView rvTablesOccupy;
    private List<String> idList;
    private List<String> matchedIdList;
    private int matchCount = 0;
    private com.zcs.sdk.DriverManager mDriverManager;
    private Printer mPrinter;
    double amountItems,amountItemsFinal = 0;
    RelativeLayout rlPaid,rlLayout;
    String memberId, name,nameFinal, currentDateTimeForUpdate, currentDateTime, cashierCode, code, KOTNoFinal, tableId, locationName,
        MemberId, BillNoFinal, OpeningBalance,MAINIDFinal,
        OpeningBalanceFinal, cardAmount,checkOccupiedtable, resultString;
    BigDecimal   totalSPBigDec,roundedGrandMatched;
    int   exitCount = 0, discountCheck, checkofTax = 0,checkofAmount = 0,    disCheckInner = 0, disCheckInner1 = 0;
    double spValue, totalSP, newTotalSP, newGrandTotalSP, result;
    Adapter_OccRecyclerview adapter_occRecyclerview;
    ArrayList<Model_OccupiedTable> model_occupiedTable;
    ArrayList<Model_GST> model_gsts;
double amountofTotal;
     List<String> itemCodeList = new ArrayList<>();
     RecyclerView rvBill, rvGST;
    double totalAmountFinal = 0, totalAmountItems = 0;
    Adapter_Bill adapter_bill;

    Adapter_GST adapter_gst;
    private Handler handler;
    ArrayList<Model_OccRecyclerview> model_occRecyclerviews;
    int yearCode,check=0;
    LinearLayout llTableDeatils,llDiscount,llNetTotalOfBill,llNetTotalOfBill1,llNetTotalOfBill2 ;
    BigDecimal roundedGrand;
    TextView tvMemberId, tvTableId, tvtablenNoofBill, tvName, tvLocationName, tvTotalOfBill, tvKOTNo,
        tvTotalOfGovTx,tvDiscount, tvDiscountText,tvNetTotalOfBill,tvNetTotalOfBilltext,tvFinalNetTotal,tvNetTotalwithoutDiscount,
        tvToPay, tvGrandTotal, tv_Information,tvCGSTAmount,tvSGSTAmount;
    ArrayList<String> kotNumbers = new ArrayList<>();
    ArrayList<String> itemsData = new ArrayList<>();
    double finalTotalAmount,finalTotalAmountItemsMatched,discountedAmount,finalTotalAmountItemsUnmatched;
    BigDecimal roundedDiscount,roundedgstAmount,dividedAmount,dividedAmounthalf,roundedGrandFinalUnmatched,closingBalanceAfterBill;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occupied_tables);
        Utils.blackIconStatusBar(this, R.color.main);
        init();
        mDriverManager = DriverManager.getInstance();
        mPrinter = mDriverManager.getPrinter();
        Intent intent = getIntent();

//        yearCode = intent.getIntExtra("yearCode", 0);
        code = intent.getStringExtra("code");
        tableId = intent.getStringExtra("tableId");
        locationName = intent.getStringExtra("locationName");
        checkOccupiedtable = intent.getStringExtra("checkOccupiedtable");
yearCodeData();
        runOnUiThread(() -> {
            tvTableId.setText(tableId);
            tvtablenNoofBill.setText("Table No : " + tableId);
            tvLocationName.setText(locationName);
        });


        Log.e("jsrtjsbx", " " + tableId + " " + code + " " + locationName + " " + checkOccupiedtable);
        idList = new ArrayList<>();
        matchedIdList = new ArrayList<>();

        getSchemeHeadData(code);

        getBillData(code, tableId);


        currentDateTimeForUpdate = DateTimeUtil.getCurrentDateTime();
        currentDateTime = getCurrentDateTime();


        rlPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomAlertDialog();

            }
        });
//        Log.d("matchedIdListTable", String.valueOf(matchedIdList));
//compareItemCodes(model_occupiedTable,matchedIdList);

    }
    private void compareItemCodes(ArrayList<Model_OccupiedTable> model_occupiedTable,  List<String> matchedIdList) {
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

                amountItems = amountValue * quantity;
                amountItemsFinal+=amountItems;
                Log.e("Dataoftotalamount", String.valueOf(amountItemsFinal));
                if (matchedIdList.contains(item.getItemCode())) {

                    totalAmountItemsMatched += amountItems;
checkofTax=1;
                    Log.e("Innerdata", " "+item.getItemCode()+  " totalAmountItemsMatched " +totalAmountItemsMatched );
                    disCheckInner = 1;
                    disCheckInner1 = 2;
                    llDiscount.setVisibility(View.VISIBLE);
//                    llNetTotalOfBill.setVisibility(View.VISIBLE);
                } else {
                    double amount = amountValue * Double.parseDouble(item.getIteQnt()) * gst / 100 / 2;
//                     llNetTotalOfBill1.setVisibility(View.VISIBLE);
                    totalAmountItemsUnmatched += amountItems;
                    totalAmountFinalUnmatched += amount * 2;
                    Log.e("Innerdata",  " "+item.getItemCode()+" amount " +amount+ " totalAmountItemsUnmatched " +totalAmountItemsUnmatched+ " totalAmountFinalUnmatched " +totalAmountFinalUnmatched);

                }
            } catch (NumberFormatException e) {
                // Handle parsing errors
                Log.e("Error", "Error parsing double value");
            }
        }
        double discount = amountItemsFinal * 0.05;
          roundedDiscount = BigDecimal.valueOf(discount)
            .setScale(2, RoundingMode.HALF_UP);

          discountedAmount = totalAmountItemsMatched - discount;
        double amount = discountedAmount *   gst / 100 / 2;
           finalTotalAmountItemsMatched = totalAmountItemsMatched;
          finalTotalAmountItemsUnmatched = totalAmountItemsUnmatched;
          finalTotalAmount = totalAmountItemsUnmatched+discountedAmount ;
        double gstAmount = finalTotalAmount * 0.05;

        double gstAmountFinal=gstAmount/2.0;
        dividedAmount = BigDecimal.valueOf(gstAmount)
            .setScale(2, RoundingMode.HALF_UP);
    dividedAmounthalf = BigDecimal.valueOf(gstAmountFinal)
            .setScale(2, RoundingMode.HALF_UP);

        BigDecimal dividedAmountBigDecimal = BigDecimal.valueOf(gstAmountFinal);
        BigDecimal multipliedAmount = dividedAmountBigDecimal.multiply(BigDecimal.valueOf(2));
        roundedgstAmount = multipliedAmount.setScale(2, RoundingMode.HALF_UP);
Log.e("dataofGST", String.valueOf(gstAmount));
        double finalAmountItemsFinal = amountItemsFinal;
        runOnUiThread(() -> {
            tvDiscountText.setText("Discount " + "(5% on " + "\u20B9 " + finalAmountItemsFinal + ")");
            tvDiscount.setText("- " + "\u20B9 " + roundedDiscount);
//            tvNetTotalOfBilltext.setText("Net Total After Discount on " +  "\u20B9 " + finalTotalAmountItemsMatched + ")");
//            tvNetTotalOfBill.setText("\u20B9 " + discountedAmount);
            tvNetTotalwithoutDiscount.setText( "\u20B9 " + finalTotalAmountItemsUnmatched);
            tvFinalNetTotal.setText("\u20B9 " + finalTotalAmount);
            tvCGSTAmount.setText("\u20B9 " + dividedAmounthalf);
            tvSGSTAmount.setText("\u20B9 " +  dividedAmounthalf);
        });


          roundedGrandMatched = BigDecimal.valueOf(totalAmountFinalUnmatched)
            .setScale(2, RoundingMode.HALF_UP);
        BigDecimal roundedGrandUnmatched = BigDecimal.valueOf(totalAmountItemsUnmatched + totalAmountFinalUnmatched)
            .setScale(2, RoundingMode.HALF_UP);

        BigDecimal cardAmountBigDecimal = new BigDecimal(String.valueOf(cardAmount));


        roundedGrandFinalUnmatched = BigDecimal.valueOf(finalTotalAmount)
            .add(dividedAmount)
            .setScale(2, RoundingMode.HALF_UP);
        closingBalanceAfterBill = cardAmountBigDecimal.subtract(roundedGrandFinalUnmatched);
        Log.e("Matched Data",  roundedGrandMatched+" "+cardAmount+ " " + cardAmountBigDecimal + " " + roundedGrandFinalUnmatched + " " + closingBalanceAfterBill);

        runOnUiThread(() -> {
            tvTotalOfGovTx.setText(String.valueOf("+ \u20B9 " + dividedAmount)); // Set total for unmatched items
            tvToPay.setText(String.valueOf("\u20B9 " + roundedGrandFinalUnmatched)); // Set total to pay for unmatched items
            tvGrandTotal.setText(String.valueOf("\u20B9 " + roundedGrandFinalUnmatched)); // Set grand total for unmatched items
            handler = new Handler();
            // Convert cardAmount string to BigDecimal
            BigDecimal cardAmountDecimal = new BigDecimal(cardAmount);

            if (roundedGrandFinalUnmatched != null && cardAmountDecimal != null) {
                if (roundedGrandFinalUnmatched.compareTo(cardAmountDecimal) > 0) {
                    // newGrandTotalSP is greater than cardAmount
//                    Toast.makeText(this, "Card balance is " + cardAmount + "\n Which is insufficient to cover the Bill amount\n Please Recharge Card", Toast.LENGTH_LONG).show();
                   showAlertDialog();
                    rlPaid.setVisibility(View.GONE);

//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            finish();
//                        }
//                    }, 5000); // 5000 milliseconds = 5 seconds
                } else if (roundedGrandFinalUnmatched.compareTo(cardAmountDecimal) < 0) {
                    // newGrandTotalSP is less than cardAmount
                    rlPaid.setVisibility(View.VISIBLE);
                } else {
                    // newGrandTotalSP is equal to cardAmount
                    rlPaid.setVisibility(View.VISIBLE);
                }
            } else {
                // Handle the case where one or both of the objects are null
                Toast.makeText(this, "Null", Toast.LENGTH_SHORT).show();
            }
        });

    }


    void init() {
        rvTablesOccupy = findViewById(R.id.rvTablesOccupy);
        rvBill = findViewById(R.id.rvBill);
        rvGST = findViewById(R.id.rvGST);
        llTableDeatils = findViewById(R.id.llTableDeatils);
        llDiscount = findViewById(R.id.llDiscount);
        llNetTotalOfBill = findViewById(R.id.llNetTotalOfBill);
        llNetTotalOfBill1 = findViewById(R.id.llNetTotalOfBill1);
        llNetTotalOfBill2= findViewById(R.id.llNetTotalOfBill2);
        tvMemberId = findViewById(R.id.tvMemberId);
        tvTableId = findViewById(R.id.tvTableId);
        tvtablenNoofBill = findViewById(R.id.tvtablenNoofBill);
        tvName = findViewById(R.id.tvName);
        tvLocationName = findViewById(R.id.tvLocationName);
        tvKOTNo = findViewById(R.id.tvKOTNo);
        tvTotalOfBill = findViewById(R.id.tvTotalOfBill);
        tvTotalOfGovTx = findViewById(R.id.tvTotalOfGovTx);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvDiscountText = findViewById(R.id.tvDiscountText);
        tvNetTotalOfBill = findViewById(R.id.tvNetTotalOfBill);
        tvNetTotalOfBilltext = findViewById(R.id.tvNetTotalOfBilltext);
        tvToPay = findViewById(R.id.tvToPay);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);
         tv_Information = findViewById(R.id.tv_Information);
        tvFinalNetTotal = findViewById(R.id.tvFinalNetTotal);
        tvNetTotalwithoutDiscount = findViewById(R.id.tvNetTotalwithoutDiscount);
        tvCGSTAmount = findViewById(R.id.tvCGSTAmount);
        tvSGSTAmount = findViewById(R.id.tvSGSTAmount);
        rlPaid = findViewById(R.id.rlPaid);
        rlLayout = findViewById(R.id.rlLayout);
    }
    public static String getCurrentDateTime() {
        // Get the current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy hh:mm a");

        // Format the current date and time
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
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
                alertDialog.dismiss();
                updateBillData(BillNoFinal, String.valueOf(yearCode), cardAmount,
                    String.valueOf(closingBalanceAfterBill),
                    String.valueOf(roundedGrandFinalUnmatched),currentDateTimeForUpdate);
                updateTableOrder(Integer.parseInt(tableId), 0, code);
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

    @Override
    public void waiterClick(int position, int aid, String memberID, String name, String memberType, String couponNo, String cashierCode, String userValue,
                            String locationName, String locationId, String waiterID, int bqnt, int tid, int locid) {
        Intent intent = new Intent(getApplicationContext(), Bill_Activity.class);
        intent.putExtra("aid", aid);
        intent.putExtra("memberID", memberID);
        intent.putExtra("name", name);
        intent.putExtra("memberType", memberType);
        intent.putExtra("couponNo", couponNo);
        intent.putExtra("cashierCode", cashierCode);
        intent.putExtra("userValue", userValue);
        intent.putExtra("locationName", locationName);
        intent.putExtra("locationId", locationId);
        intent.putExtra("waiterID", waiterID);
        intent.putExtra("tableId", tid);
        intent.putExtra("location", locid);
        intent.putExtra("yearCode", yearCode);

        startActivity(intent);
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
                        OpeningBalance = resultSet.getString("OpeningBalance");
                        // Extract only the date part
                        BillDate = BillDate.split(" ")[0]; // Assuming the date format is consistent
                        // Log the retrieved data for debugging
                        Log.e("Debug", "BillNo: " + BillNo + ", BillDate: " + BillDate);
                        // Check if the BillDate matches the current date
                        if (BillDate.equals(currentDate)) {
                            progressDialog.dismiss();
                            BillNoFinal = BillNo;
                            OpeningBalanceFinal = OpeningBalance;
                            // Log the final BillNo before calling getBarBillBody
                            Log.e("Debug", "Matching BillNo found: " + BillNoFinal + " " + OpeningBalanceFinal);
                            // Call getBarBillBody
//                            cardAmountData(MemberId);
                            getCardData(MemberId);
                            getBarBillBody(BillNoFinal);

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
            progressDialog = new ProgressDialog(OccupiedTables_Activity.this);
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

                // Handle when no match found
                // Handle the case where no matching records are found
            }
        }
    }

//    public void getBarKOThead(String code,String tableId) {
//        try (Connection connect = new ConnectionHelper(this).connectionclass()) {
//            if (connect != null) {
//                // Connected to the database
//                System.out.println("Connected to the database");
//                Log.e("shstrdh", "Connected to the database");
//
//                // Get the current date
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                String currentDate = dateFormat.format(new Date());
//
//                // Create a SQL query to select data from the table where BillNo matches
//                String selectQuery = "SELECT * FROM TI_ChargeableBarKOThead WHERE LocationCode = ? AND TableNo = ? AND PAX = 0";
//                try (PreparedStatement preparedStatement = connect.prepareStatement(selectQuery)) {
//                    preparedStatement.setString(1, code);
//                    preparedStatement.setString(2, tableId);
//                    ResultSet resultSet = preparedStatement.executeQuery();
//
//                    // Process the results
//                    while (resultSet.next()) {
//
//                        String CreationDate = resultSet.getString("KOTDate").split(" ")[0]; // Extracting date part
//                        if (CreationDate.equals(currentDate)) {
//                            String KOTNo = resultSet.getString("KOTNo");
//                            String MemberId = resultSet.getString("MemberId");
////                            String Rate = resultSet.getString("Rate");
////                            String Qty = resultSet.getString("Qty");
////                            String itemcode = resultSet.getString("itemcode");
//
////                            getBarKOTBody(KOTNo);
//
//
//                            Log.e("shstrdh", " " + KOTNo  +" " + MemberId   );
//                        }
//                    }
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                // Connection failed
//                System.out.println("Failed to connect to the database");
//                Log.e("shstrdh", "Failed to connect to the database");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

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
                                    Model_GST menu10 = new Model_GST(GST, Rate, Qty);
                                    model_gsts.add(menu10);
                                }

                            }
                        }
                    }

                    // Set adapters after processing all KOT numbers
                    runOnUiThread(() -> {
                        progressDialog.dismiss();
                        if (model_occupiedTable != null) {
                            for (Model_OccupiedTable item : model_occupiedTable) {
                                Log.d("Model_OccupiedTable", "ItemName: " + item.getIteName() +
                                    ", Rate: " + item.getItePrice() +
                                    ", Qty: " + item.getIteQnt() +
                                    ", GST: " + item.getItemSaletaxcode() +
                                    ", itemcode: " + item.getItemCode()+" data of matchedIdList"+matchedIdList);
                                amountofTotal= Double.parseDouble(item.getItePrice())*
                                    Double.parseDouble(item.getIteQnt());

                                getItemMasterData(item.getItemCode());
                                itemCodeList.add(item.getItemCode());

                            }
                        } else {
                            Log.d("Model_OccupiedTable", "model_occupiedTable is null");
                        }

                        compareItemCodes(model_occupiedTable,matchedIdList);
                        rlLayout.setVisibility(View.VISIBLE);
                        tv_Information.setVisibility(View.GONE);
                        rvBill.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        adapter_bill = new Adapter_Bill(getApplicationContext(), model_occupiedTable, tvTotalOfBill);
                        rvBill.setAdapter(adapter_bill);

                        rvGST.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        adapter_gst = new Adapter_GST(getApplicationContext(), model_gsts);
                        rvGST.setAdapter(adapter_gst);
                    });
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
                    MAINIDFinal=MAINID;
                    Log.e("dataofMainID",MAINIDFinal);
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

    public void updateBillData(String BillNoFinal, String YearCode, String OpeningBalanceFinal,
                               String ClosingBalance, String roundedGrand, String currentDateTimeForUpdate) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.new_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        try (Connection connect = new ConnectionHelper(this).connectionclass()) {
            if (connect != null) {
                // Connected to the database
                System.out.println("Connected to the database");
                Log.e("dataupdateBillHead", "Connected to the database");

                // Debug print the input values
                System.out.println("BillNoFinal: " + BillNoFinal);
                System.out.println("YearCode: " + YearCode);
                System.out.println("BillStatus: " + "0");

                // Create a SQL query to check if the record exists
                String checkQuery = "SELECT * FROM TI_BarBillHead WHERE BillNo = ? AND YearCode = ? AND BillStatus = ?";

                try (PreparedStatement checkStatement = connect.prepareStatement(checkQuery)) {
                    checkStatement.setString(1, BillNoFinal);
                    checkStatement.setString(2, YearCode);
                    checkStatement.setString(3, "0");

                    // Debug print the prepared statement parameters
                    System.out.println("Executing query: " + checkQuery);
 try (ResultSet resultSet = checkStatement.executeQuery()) {
                        if (resultSet.next()) {
                            // Record exists, perform the update
                            String updateQuery = "UPDATE TI_BarBillHead SET BillStatus = ?, printbalance = ?, " +
                                "OpeningBalance = ?, ClosingBalance = ?, amount = ?, CreationDate = ?, ModificationDate = ? " +
                                "WHERE BillNo = ? AND YearCode = ?";

                            try (PreparedStatement updateStatement = connect.prepareStatement(updateQuery)) {
                                updateStatement.setString(1, "1"); // Set the new BillStatus value
                                updateStatement.setString(2, "yes"); // Set the printbalance value
                                updateStatement.setString(3, OpeningBalanceFinal); // Set the OpeningBalance value
                                updateStatement.setString(4, ClosingBalance); // Set the ClosingBalance value
                                updateStatement.setString(5, roundedGrand); // Set the amount value
                                updateStatement.setString(6, currentDateTimeForUpdate); // Set the CreationDate value
                                updateStatement.setString(7, currentDateTimeForUpdate); // Set the ModificationDate value
                                updateStatement.setString(8, BillNoFinal); // Set the BillNo for which you want to update
                                updateStatement.setString(9, YearCode); // Set the YearCode for which you want to update

                                int rowsAffected = updateStatement.executeUpdate();
                                System.out.println("Rows affected: " + rowsAffected);

                                if (rowsAffected > 0) {
                                    System.out.println("BillStatus updated successfully for BillNo: " + BillNoFinal);
                                    Log.e("dataupdateBillHead", "BillStatus updated successfully for BillNo: " + BillNoFinal);
                                    updateMemberAmount(MAINIDFinal, String.valueOf(closingBalanceAfterBill));

                                } else {
                                    System.out.println("No rows were updated. Possible reasons: Record does not exist, or data hasn't changed.");
                                    Log.e("dataupdateBillHead", "No rows were updated for BillNo: " + BillNoFinal);
                                }
                            } catch (SQLException e) {
                                // Handle SQL exceptions during the update
                                e.printStackTrace();
                                System.out.println("Failed to execute SQL update: " + e.getMessage());
                                Log.e("dataupdateBillHead", "Failed to execute SQL update: " + e.getMessage());
                            }
                        } else {
                            // Record does not exist
                            System.out.println("No record found for BillNo: " + BillNoFinal + " and YearCode: " + YearCode);
                            Log.e("dataupdateBillHead", "No record found for BillNo: " + BillNoFinal + " and YearCode: " + YearCode);
                        }
                    }
                } catch (SQLException e) {
                    // Handle SQL exceptions during the check
                    e.printStackTrace();
                    System.out.println("Failed to execute SQL check: " + e.getMessage());
                    Log.e("dataupdateBillHead", "Failed to execute SQL check: " + e.getMessage());
                }
            } else {
                // Connection failed
                System.out.println("Failed to connect to the database");
                Log.e("dataupdateBillHead", "Failed to connect to the database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            progressDialog.dismiss(); // Ensure the progress dialog is dismissed
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

            Log.e("dagawgw", "Connected to the database");

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
                } else {

                    do {
                        cardAmount = rs.getString("amount");

                        if (cardAmount != null) {
                            Log.e("dagawgw", "Data found: " + cardAmount);

                        } else {
                            Log.e("dagawgw", "Incomplete data for this row");
                        }
                    } while (rs.next());
                }

                connect.close();
            } catch (Exception e) {
                // Handle the exception appropriately
                Log.e("dagawgw", "Exception while retrieving data: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Connection failed
            Log.e("dagawgw", "Failed to connect to the database");
        }
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


            int countqnt = 1;
            for (Model_OccupiedTable item : model_occupiedTable) {
                Log.d("Model_OccupiedTable", "ItemName: " + item.getIteName() +
                    ", Rate: " + item.getItePrice() +
                    ", Qty: " + item.getIteQnt() +
                    ", GST: " + item.getItemSaletaxcode() +
                    ", itemcode: " + item.getItemCode());
                int totalLengthofline = 60;
//                GrandTotal = Double.parseDouble(item.getItePrice()) * Double.parseDouble(item.getIteQnt());
                int qnt = (int) Double.parseDouble(item.getIteQnt());
//                mPrinter.setPrintAppendString("  "+countqnt+"            "+item.getItemCode() +
//                    "          " + item.getItePrice() +
//                     "     "+qnt + "pc  "
//                    + "         "+ ("\u20B9 " + Double.parseDouble(item.getItePrice()) * Double.parseDouble(item.getIteQnt())), format);
                // Use Double.parseDouble(item1) instead of Integer.parseInt(item1)

//                sumItem+=GrandTotal ;
countqnt++;
            }


//            printHorizontalLine(43, format);



            if (disCheckInner == 1) {

                format.setTextSize(22);
                format.setStyle(PrnTextStyle.NORMAL);
                format.setAli(Layout.Alignment.ALIGN_NORMAL);
                String Item = "Sub Total";
                String ItemTotal = "\u20B9 " + sumItem;
                int ItemL = 66;
                int spacesBeforeColonItem = ItemL - Item.length();
                String formatStringItem = Item + String.format("%" + spacesBeforeColonItem +
                    "s", "   ") + ItemTotal;
                mPrinter.setPrintAppendString(formatStringItem, format);
                format.setTextSize(22);
                format.setStyle(PrnTextStyle.NORMAL);
                format.setAli(Layout.Alignment.ALIGN_NORMAL);
                String discountLabel = "Discount ( 5% on ";
                String middle = "\u20B9 " + amountItemsFinal+" )";
                String last = "\u20B9 " + roundedDiscount;
                int itemTotalL = 47;

                int spacesBeforeColonDiscount = itemTotalL - (discountLabel.length() + middle.length());
                String formatStringDiscount = discountLabel + middle + String.format("%" +
                    spacesBeforeColonDiscount + "s", "") + last;

                mPrinter.setPrintAppendString(formatStringDiscount, format);



            }
            printHorizontalLine(43, format);


            format.setTextSize(22);
            format.setStyle(PrnTextStyle.NORMAL);
            format.setAli(Layout.Alignment.ALIGN_NORMAL);

            String netTotalLabel1 = "Net Total ";
            String netTotalValue = "\u20B9 " + finalTotalAmount;
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
            String CGST = "CGST  @  "+"2.5%" ;
            String CGSTTotal = "\u20B9 " + dividedAmounthalf;
            int CGSTL = 60;
            int spacesBeforeColonCGST = CGSTL - CGST.length();
            String formatStringCGST = CGST + String.format("%" + spacesBeforeColonCGST +
                "s", "") + CGSTTotal;
            mPrinter.setPrintAppendString(formatStringCGST, format);


            String SGST = "SGST  @  "+"2.5%" ;
            String SGSTTotal = "\u20B9 " + dividedAmounthalf;
            int SGSTL = 60;
            int spacesBeforeColonSGST = SGSTL - SGST.length();
            String formatStringSGST = SGST + String.format("%" + spacesBeforeColonSGST +
                "s", "") + SGSTTotal;
            mPrinter.setPrintAppendString(formatStringSGST, format);

            Log.e("dataofAdapter",  "  " + roundedGrand);


//            String cgstLabel = "CGST 2.5 % :";
//            String cgstValue = "\u20B9 " + dividedAmount;
//            mPrinter.setPrintAppendString(cgstLabel + String.format("%" + (62 - cgstLabel.length()) + "s", "") + cgstValue, format);
//
//            String sgstLabel = "SGST 2.5 % :";
//            String sgstValue = "\u20B9 " + dividedAmount;
//            mPrinter.setPrintAppendString(sgstLabel + String.format("%" + (62 - sgstLabel.length()) + "s", "") + sgstValue, format);

// Update the UI with the calculated amounts

            format.setTextSize(22);
            format.setStyle(PrnTextStyle.NORMAL);
            format.setAli(Layout.Alignment.ALIGN_NORMAL);
            String GST = "Total GST";
            String TotalGST = "\u20B9 " + dividedAmount;
            int GSTL = 63;
            int spacesBeforeColonGST = GSTL - GST.length();
            String formatStringGST = GST + String.format("%" + spacesBeforeColonGST +
                "s", " + ") + TotalGST;
            mPrinter.setPrintAppendString(formatStringGST, format);
            printHorizontalLine(43, format);
            String Pay = "Bill Amount";
            String ToPay = "\u20B9 " + roundedGrandFinalUnmatched;
            int ToPayL = 62;
            int spacesBeforeColonPay = ToPayL - Pay.length();
            String formatStringPay = Pay + String.format("%" + spacesBeforeColonPay +
                "s", "   ") + ToPay;
            mPrinter.setPrintAppendString(formatStringPay, format);
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
    }

    public void getSchemeHeadData(  String locationCode) {
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
                Log.e("qrfgfefg", "Connected to the database");

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

                             Log.e("qrfgfefg", "ID: " + ID + ", AppliedOn: " + AppliedOn+ ", SchemeType: " + SchemeType+ ", idList: " + idList);
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

     public void updateMemberAmount(String memberIdNo, String newAmount) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.new_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        try (Connection connect = new ConnectionHelper(this).connectionclass()) {
            if (connect != null) {
                // Connected to the database
                System.out.println("Connected to the database");
                Log.e("DatabaseConnection", "Connected to the database "+memberIdNo);

                // Create a SQL query to update the amount for the specified memberidno
                String updateQuery = "UPDATE TM_IssueCoupon SET amount = ? WHERE memberidno = ?";

                try (PreparedStatement preparedStatement = connect.prepareStatement(updateQuery)) {
                    preparedStatement.setString(1, newAmount); // Set the new amount value
                    preparedStatement.setString(2, memberIdNo); // Set the memberidno for which you want to update

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Amount updated successfully for memberidno: " + memberIdNo);
                        Log.e("DatabaseUpdate", "Amount updated successfully for memberidno: " + memberIdNo);

                    } else {
                        System.out.println("Failed to update amount for memberidno: " + memberIdNo);
                        Log.e("DatabaseUpdate", "Failed to update amount for memberidno: " + memberIdNo);
                    }
                } catch (SQLException e) {
                    // Handle SQL exceptions
                    e.printStackTrace();
                    System.out.println("Failed to execute SQL update: " + e.getMessage());
                    Log.e("DatabaseUpdate", "Failed to execute SQL update: " + e.getMessage());
                }
            } else {
                // Connection failed
                System.out.println("Failed to connect to the database");
                Log.e("DatabaseConnection", "Failed to connect to the database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            progressDialog.dismiss(); // Ensure the progress dialog is dismissed
        }
    }
    private void showAlertDialog() {

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_custom_layout, null);

        RelativeLayout rlYes  = dialogView.findViewById(R.id.rlYes);

        TextView tvMessage  = dialogView.findViewById(R.id.dialog_message);
        AlertDialog.Builder builder = new AlertDialog.Builder(OccupiedTables_Activity.this);
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();


        tvMessage.setText("Card balance is  "  + cardAmount + " Which is insufficient to cover the KOT amount");
        rlYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
                alertDialog.dismiss();

//
            }
        });

        alertDialog.show();
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
