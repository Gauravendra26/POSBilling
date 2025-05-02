package de.syss.MifareClassicTool;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.syss.MifareClassicTool.Room.Product;
import de.syss.MifareClassicTool.Room.ProductDao;
import de.syss.MifareClassicTool.Room.ProductDatabase;

public class Order_Activity extends AppCompatActivity implements Adapter_Order.ProductClick {

    RecyclerView rvOrder;
    ImageView imgBack;
    RelativeLayout rlKOT, rlBill;
    ArrayList<Model_Menu> model_menu;
    Adapter_Order adapter_menu;
    TextView tvCardHolderName;

    String userValue, locationName, tableId, name, memberID, code, currentDateTime, currentYear,waiterID;
    List<Product> products = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Utils.blackIconStatusBar(Order_Activity.this, R.color.main);
        init();
        Intent intent = getIntent();
        userValue = intent.getStringExtra("userValue");
        locationName = intent.getStringExtra("locationName");
        tableId = intent.getStringExtra("tableId");
        name = intent.getStringExtra("name");
        memberID = intent.getStringExtra("memberID");
        code = intent.getStringExtra("code");
        waiterID = intent.getStringExtra("waiterID");

        currentDateTime = DateTimeUtil.getCurrentDateTime();
        currentYear = DateTimeUtil.getCurrentYear();

        Log.e("currentDateTime", currentDateTime + "  " + currentYear);


        tvCardHolderName.setText("Hi, " + name);
//        Toast.makeText(this, ""+userValue, Toast.LENGTH_SHORT).show();
        if (userValue != null && !userValue.isEmpty()) {
//            itemData(Integer.parseInt(userValue));
        }
        ProductDatabase db = Room.databaseBuilder(getApplicationContext(),
            ProductDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();
        rvOrder.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter_menu = new
            Adapter_Order(getApplicationContext(), products);
        rvOrder.setAdapter(adapter_menu);
        products.addAll(productDao.getallproduct());
        adapter_menu.set(Order_Activity.this);
        if (products.isEmpty()) {
            rlKOT.setVisibility(View.GONE);
            rlBill.setVisibility(View.GONE);
        } else {
            rlKOT.setVisibility(View.VISIBLE);
            rlBill.setVisibility(View.VISIBLE);
        }


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rlKOT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), KOT_Activity.class);
                intent.putExtra("locationName", locationName);
                intent.putExtra("tableId", tableId);
                intent.putExtra("name", name);
                intent.putExtra("memberID", memberID);
                intent.putExtra("code", code);
                intent.putExtra("currentDateTime", currentDateTime);
                intent.putExtra("currentYear", currentYear);
                intent.putExtra("waiterID", waiterID);
                startActivity(intent);

            }
        });
        rlBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Bill_Activity.class);
                intent.putExtra("locationName", locationName);
                intent.putExtra("tableId", tableId);
                intent.putExtra("name", name);
                intent.putExtra("memberID", memberID);
                intent.putExtra("code", code);
                intent.putExtra("currentDateTime", currentDateTime);
                intent.putExtra("currentYear", currentYear);
                intent.putExtra("waiterID", waiterID);
                startActivity(intent);

            }
        });

    }

    void init() {
        imgBack = findViewById(R.id.imgBack);
        rlKOT = findViewById(R.id.rlKOT);
        rlBill = findViewById(R.id.rlBill);
        rvOrder = findViewById(R.id.rvOrder);
        tvCardHolderName = findViewById(R.id.tvCardHolderName);

    }


    private void showCustomAlertDialog() {
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);

        // Initialize views inside the custom layout
        RelativeLayout rlYes = dialogView.findViewById(R.id.rlYes);
        RelativeLayout rlNo = dialogView.findViewById(R.id.rlNo);

        // Create the AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView); // Set custom layout view

        final AlertDialog alertDialog = builder.create();

        // Set button click listeners
        rlYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle "Yes" button click
                alertDialog.dismiss(); // Close the dialog
            }
        });

        rlNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle "No" button click
                alertDialog.dismiss(); // Close the dialog
            }
        });
//
        alertDialog.show(); // Show the custom AlertDialog
    }


    @Override
    public void notifyData(int position) {
        ProductDatabase db = Room.databaseBuilder(getApplicationContext(),
            ProductDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();
        rvOrder.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        List<Product> products = productDao.getallproduct();
    }

    @Override
    public void OrderClick(int position, String itemName, int itemCode, String purchaseUnit, String saleUnit, double sp,
                           int mainGroup, String itemGroup, String itemSubGroup) {
        Toast.makeText(this, "OrderPlan", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void AddNoteOrderClick(int position, String itemName, int itemCode, String purchaseUnit, String saleUnit, double sp,
                                  int mainGroup, String itemGroup, String itemSubGroup) {
//        Toast.makeText(this, "Order", Toast.LENGTH_SHORT).show();
        showCustomAlertDialog();
    }


}
