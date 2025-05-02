package de.syss.MifareClassicTool;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.syss.MifareClassicTool.Room.Product;
import de.syss.MifareClassicTool.Room.ProductDao;
import de.syss.MifareClassicTool.Room.ProductDatabase;

public class Menu_Activity extends AppCompatActivity implements Adapter_Menu.ProductClick {

    ImageView imgBack;
    RelativeLayout rlContinue,rlOccupiedTables;
    TextView tvAdd4, tvAdd3, tvAdd2, tvAdd1, tvPrice,tvTableNo,tvCardHolderName;
    Animation centeroutanimantion;
    RecyclerView rvMenu, rvmenuSearch;

    String userValue,tableId,locationName,name,memberID,code,waiterID,location,memberType,
        couponNo,cashierCode,checkOccupiedtable,MAINID;
    EditText etSearch;
    ArrayList<Model_Menu> model_menu;

    Adapter_Menu adapter_menu;

int yearCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Utils.blackIconStatusBar(Menu_Activity.this, R.color.main);
        init();
        SharedPreferences sharedPreferences1 = getSharedPreferences("NecessaryData", MODE_PRIVATE);
        userValue = sharedPreferences1.getString("userValue", "");
        tableId = sharedPreferences1.getString("tableId", "");
        code = sharedPreferences1.getString("code", "");
        locationName = sharedPreferences1.getString("locationName", "");
//        memberID = sharedPreferences1.getString("memberID", "");
        waiterID = sharedPreferences1.getString("waiterID", "");
        location = sharedPreferences1.getString("location", "");
        checkOccupiedtable = sharedPreferences1.getString("checkOccupiedtable", "");

yearCodeData();
        if (checkOccupiedtable .matches(String.valueOf(1)) ){

        } else{
            Intent intent = getIntent();
            name = intent.getStringExtra("name");
            memberID = intent.getStringExtra("memberID");
            memberType = intent.getStringExtra("memberType");
            couponNo = intent.getStringExtra("couponNo");
            MAINID = intent.getStringExtra("MAINID");
            cashierCode = intent.getStringExtra("cashierCode");
        }

         if (name != null && !name.isEmpty()) {
            // Existing code inside this block
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences("dataOfcard", MODE_PRIVATE);
            name = sharedPreferences.getString("name", "");
            memberID = sharedPreferences.getString("memberId", "");
            memberType = sharedPreferences.getString("memberType", "");
            couponNo = sharedPreferences.getString("couponNo", "");
            cashierCode = sharedPreferences.getString("cashierCode", "");
        }

        Log.e("hhsbfdbd",""+name+" memberID "+memberID+" memberType "+memberType+" couponNo "+couponNo
            +" cashierCode "+cashierCode+" checkOccupiedtable "+checkOccupiedtable+" code "+code);
//        Toast.makeText(this, ""+name+" "+memberID, Toast.LENGTH_SHORT).show();
        tvCardHolderName.setText("Hi, "+name);
        tvTableNo.setText("Table : "+tableId);
        if (!userValue.isEmpty()){
//            itemData(Integer.parseInt(userValue));
            fetchitemData( code);
        }





        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter_menu.filterData(charSequence.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        rlContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Order_Activity.class);
                intent.putExtra("userValue",userValue);
                intent.putExtra("tableId",tableId);
                intent.putExtra("locationName",locationName);
                intent.putExtra("name",name);
                intent.putExtra("memberID",memberID);
                intent.putExtra("code",code);
                intent.putExtra("MAINID",MAINID);
                intent.putExtra("waiterID",waiterID);
                intent.putExtra("location",location);
                intent.putExtra("memberType",memberType);
                intent.putExtra("couponNo",couponNo);
                intent.putExtra("yearCode",yearCode);
                intent.putExtra("cashierCode",cashierCode);
                intent.putExtra("checkOccupiedtable",checkOccupiedtable);
                startActivity(intent);


            }
        });
        rlOccupiedTables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OccupiedTables_Activity.class);
                intent.putExtra("locationName", locationName);
                intent.putExtra("tableId", tableId);
                intent.putExtra("name", name);
                intent.putExtra("memberID", memberID);
                intent.putExtra("code", code);
                intent.putExtra("waiterID", waiterID);
                intent.putExtra("userValue", userValue);
                intent.putExtra("location", location);
                intent.putExtra("memberType",memberType);
                intent.putExtra("couponNo", couponNo);
                intent.putExtra("yearCode", yearCode);
                intent.putExtra("checkOccupiedtable", checkOccupiedtable);
                startActivity(intent);

            }
        });
        tvAdd4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomAlertDialog();
            }
        });

        tvAdd3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomAlertDialog();
            }
        });

        tvAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomAlertDialog();
            }
        });

        tvAdd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomAlertDialog();
            }
        });

//        tvAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showCustomAlertDialog();
//            }
//        });

        ProductDatabase db = Room.databaseBuilder(getApplicationContext(),
            ProductDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();

        List<Product> productList = productDao.getallproduct();

        double totalSp = 0; // Variable to store the total sp value

        for (Product product : productList) {
//            Log.e("DatabaseData", "Product ID: " + product.getPid() +
//                ", Product Name: " + product.getItemName() + ", Sp value: " + product.getSp() +
//                ", Sale Unit: " + product.getSaleUnit() + ", Qnt Unit: " + product.getQnt() +
//                ", SP: " + product.getSp());

            totalSp += product.getSp(); // Accumulate the sp values
        }
        tvPrice.setText("Total : "+"\u20B9"+  totalSp);
    }

    void init() {
        imgBack = findViewById(R.id.imgBack);
        rlContinue = findViewById(R.id.rlContinue);
        rlOccupiedTables = findViewById(R.id.rlOccupiedTables);
        tvPrice = findViewById(R.id.tvPrice);
        tvTableNo = findViewById(R.id.tvTableNo);
        tvAdd1 = findViewById(R.id.tvAdd1);
        tvAdd2 = findViewById(R.id.tvAdd2);
        tvAdd3 = findViewById(R.id.tvAdd3);
        tvAdd4 = findViewById(R.id.tvAdd4);
        tvCardHolderName = findViewById(R.id.tvCardHolderName);
        etSearch = findViewById(R.id.etSearch);
        rvMenu = findViewById(R.id.rvMenu);
        rvmenuSearch = findViewById(R.id.rvmenuSearch);

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

    public void notifyData(int position) {

    }

    @Override
    public void MenuClickAdd(int position, String itemName, int itemCode, String purchaseUnit, String saleUnit, double sp, int mainGroup, String itemGroup, String itemSubGroup, String Saletaxcode) {
        ProductDatabase db = Room.databaseBuilder(getApplicationContext(), ProductDatabase.class, "cart_db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration() // This line will recreate the database if the version is increased
            .build();
        ProductDao productDao = db.ProductDao();
        if (productDao.getallproduct().isEmpty()) {
            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        } else {
            Boolean check = productDao.is_exist(Integer.parseInt("" + itemCode));
            if (check == false) {

                int pid = itemCode;
                int qnt = Integer.parseInt("1");
                String PitemName = itemName;
                String PpurchaseUnit = purchaseUnit;
                String PsaleUnit = saleUnit;
                double Psp = sp;
                int PmainGroup = mainGroup;
                String PitemGroup = itemGroup;
                String PitemSubGroup = itemSubGroup;
                String PSaletaxcode = Saletaxcode;


                productDao.insertrecord(new Product(pid, qnt, PitemName, itemCode, PpurchaseUnit, PsaleUnit, Psp, PmainGroup,
                    PitemGroup, PitemSubGroup, PSaletaxcode));
                Toast.makeText(getApplicationContext(), " Added to List Successfully", Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(getApplicationContext(), "Product Already in List", Toast.LENGTH_SHORT).show();

            }
        }


    }

    @Override
    public void MenuClickMinus(int position, String itemName, int itemCode, String purchaseUnit, String saleUnit, double sp, int mainGroup, String itemGroup, String itemSubGroup, String Saletaxcode) {

    }

    public void itemData(int userValue) {
        ConnectionHelper connectionHelper = new ConnectionHelper(this);
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {
            try {
                String query = "SELECT ItemName, ItemCode, PurchaseUnit, SaleUnit, SP, MainGroup, " +
                    "ItemGroup, ItemSubGroup, Saletaxcode " +
                    "FROM ST_ItemMaster " +
                    "WHERE MainGroup = " + userValue + " AND Status = 'Active'";

                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);

                model_menu = new ArrayList<>();

                while (rs.next()) {
                    String itemName = rs.getString("ItemName");
                    int itemCode = rs.getInt("ItemCode");
                    String purchaseUnit = rs.getString("PurchaseUnit");
                    String saleUnit = rs.getString("SaleUnit");
                    double sp = rs.getDouble("SP");
                    int mainGroup = rs.getInt("MainGroup");
                    String itemGroup = rs.getString("ItemGroup");
                    String itemSubGroup = rs.getString("ItemSubGroup");
                    String Saletaxcode = rs.getString("Saletaxcode");

                    if (itemName != null && purchaseUnit != null && saleUnit != null
                        && itemGroup != null && itemSubGroup != null && Saletaxcode != null) {

                        Model_Menu menu = new Model_Menu(itemName, itemCode, purchaseUnit, saleUnit, sp, mainGroup,
                            itemGroup, itemSubGroup, Saletaxcode);
                        model_menu.add(menu);


//                        Log.e("ValueOFMenuModel", itemName + " " + itemCode + " " + purchaseUnit + " " + saleUnit + " " + sp +
//                            " " + mainGroup + " " + itemGroup + " " + itemSubGroup + " " + Saletaxcode + " " + model_menu.size());
                    } else {
                        Log.e("ValueOFMenuModel", "Incomplete data for this row");
                    }
                }

                adapter_menu = new Adapter_Menu(getApplicationContext(), model_menu,tvPrice);
                LinearLayoutManager layoutManagerSearch = new LinearLayoutManager(getApplicationContext(),
                    LinearLayoutManager.VERTICAL, true);
                layoutManagerSearch.setStackFromEnd(true);
                layoutManagerSearch.setReverseLayout(true);

                rvMenu.setLayoutManager(layoutManagerSearch);
                rvMenu.setHasFixedSize(true);
                rvMenu.setItemAnimator(new DefaultItemAnimator());
                rvMenu.setAdapter(adapter_menu);
                adapter_menu.set(Menu_Activity.this);

                rs.close();
                st.close();
                connect.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("sdgfa", "Failed to connect to the database");
        }
    }
    public void fetchitemData(String location) {
        Log.d("fetchitemData", "Method called with location: " + location);
        ConnectionHelper connectionHelper = new ConnectionHelper(this);
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {
            Log.d("fetchitemData", "Database connection established");
            try {
                String query = "SELECT ItemCode, ItemName, SP, PurchaseUnit, SaleUnit, SP, MainGroup, " +
                    "ItemGroup, ItemSubGroup, Saletaxcode " +
                    "FROM [DSOI].[dbo].ST_ItemMaster " +
                    "WHERE Maingroup IN (1, 2) " +
                    "AND ITEMGROUP IN (SELECT GroupCode " +
                    "FROM [DSOI].[dbo].TM_LocationWiseGroup " +
                    "WHERE [Location] = '" + location + "') " +
                    "AND status = 'Active' " +
                    "AND itemcode IN (SELECT DISTINCT BOMITEMCODE " +
                    "FROM [DSOI].[dbo].ST_ITEMBOMMASTER) " +
                    "UNION ALL " +
                    "SELECT ItemCode, ItemName, SP, PurchaseUnit, SaleUnit, SP, MainGroup, " +
                    "ItemGroup, ItemSubGroup, Saletaxcode " +
                    "FROM [DSOI].[dbo].ST_ItemMaster " +
                    "WHERE Maingroup IN (1, 2) " +
                    "AND ITEMGROUP IN (SELECT GroupCode " +
                    "FROM [DSOI].[dbo].TM_LocationWiseGroup " +
                    "WHERE [Location] = '" + location + "') " +
                    "AND status = 'Active' " +
                    "AND itemcode NOT IN (SELECT DISTINCT BOMITEMCODE " +
                    "FROM [DSOI].[dbo].ST_ITEMBOMMASTER)";

                Log.d("fetchitemData", "Executing query: " + query);

                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);
                Log.d("fetchitemData", "Query executed successfully");

                model_menu = new ArrayList<>();

                while (rs.next()) {
                    Log.d("fetchitemData", "Processing result set row");
                    String itemName = rs.getString("ItemName");
                    int itemCode = rs.getInt("ItemCode");
                    String purchaseUnit = rs.getString("PurchaseUnit");
                    String saleUnit = rs.getString("SaleUnit");
                    double sp = rs.getDouble("SP");
                    int mainGroup = rs.getInt("MainGroup");
                    String itemGroup = rs.getString("ItemGroup");
                    String itemSubGroup = rs.getString("ItemSubGroup");
                    String saleTaxCode = rs.getString("Saletaxcode");

                    if (itemName != null && purchaseUnit != null && saleUnit != null
                        && itemGroup != null && itemSubGroup != null && saleTaxCode != null) {
                        Log.d("fetchitemData", "Row data: itemName=" + itemName + ", itemCode=" + itemCode +
                            ", purchaseUnit=" + purchaseUnit + ", saleUnit=" + saleUnit + ", sp=" + sp +
                            ", mainGroup=" + mainGroup + ", itemGroup=" + itemGroup + ", itemSubGroup=" + itemSubGroup +
                            ", saleTaxCode=" + saleTaxCode);

                        Model_Menu menu = new Model_Menu(itemName, itemCode, purchaseUnit, saleUnit, sp, mainGroup,
                            itemGroup, itemSubGroup, saleTaxCode);
                        model_menu.add(menu);
                    } else {
                        Log.e("fetchitemData", "Incomplete data for this row: itemName=" + itemName +
                            ", purchaseUnit=" + purchaseUnit + ", saleUnit=" + saleUnit + ", itemGroup=" + itemGroup +
                            ", itemSubGroup=" + itemSubGroup + ", saleTaxCode=" + saleTaxCode);
                    }
                }

                Log.d("fetchitemData", "Setting up adapter and layout manager");
                adapter_menu = new Adapter_Menu(getApplicationContext(), model_menu, tvPrice);
                LinearLayoutManager layoutManagerSearch = new LinearLayoutManager(getApplicationContext(),
                    LinearLayoutManager.VERTICAL, true);
                layoutManagerSearch.setStackFromEnd(true);
                layoutManagerSearch.setReverseLayout(true);

                rvMenu.setLayoutManager(layoutManagerSearch);
                rvMenu.setHasFixedSize(true);
                rvMenu.setItemAnimator(new DefaultItemAnimator());
                rvMenu.setAdapter(adapter_menu);
                adapter_menu.set(Menu_Activity.this);

                rs.close();
                st.close();
                connect.close();
                Log.d("fetchitemData", "Resources closed and connection terminated");

            } catch (Exception e) {
                Log.e("fetchitemData", "Exception occurred", e);
            }
        } else {
            Log.e("fetchitemData", "Failed to connect to the database");
        }
    }

    private void showCustomAlertDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);
        RelativeLayout rlYes = dialogView.findViewById(R.id.rlYes);
        RelativeLayout rlNo = dialogView.findViewById(R.id.rlNo);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();

        rlYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        rlNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    public void MenuClick(int position, String itemName, int itemCode, String purchaseUnit, String saleUnit, double sp,
                          int mainGroup, String itemGroup, String itemSubGroup, String Saletaxcode) {
    }

    @Override
    public void NoteClick(int position, String itemName, int itemCode, String purchaseUnit, String saleUnit, double sp,
                          int mainGroup, String itemGroup, String itemSubGroup, String Saletaxcode) {
        showCustomAlertDialog();
    }

    @Override
    public void AddClick(int position, String itemName, int itemCode, String purchaseUnit, String saleUnit,
                         double sp, int mainGroup, String itemGroup, String itemSubGroup, String Saletaxcode) {
        ProductDatabase db = Room.databaseBuilder(getApplicationContext(), ProductDatabase.class, "cart_db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration() // This line will recreate the database if the version is increased
            .build();
        ProductDao productDao = db.ProductDao();
        Boolean check = productDao.is_exist(Integer.parseInt("" + itemCode));
        if (check == false) {

            int pid = itemCode;
            int qnt = Integer.parseInt("1");
            String PitemName = itemName;
            String PpurchaseUnit = purchaseUnit;
            String PsaleUnit = saleUnit;
            double Psp = sp;
            int PmainGroup = mainGroup;
            String PitemGroup = itemGroup;
            String PitemSubGroup = itemSubGroup;
            String PSaletaxcode = Saletaxcode;


            productDao.insertrecord(new Product(pid, qnt, PitemName, itemCode, PpurchaseUnit, PsaleUnit, Psp, PmainGroup,
                PitemGroup, PitemSubGroup, PSaletaxcode));
            Toast.makeText(getApplicationContext(), " Added to List Successfully", Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(getApplicationContext(), "Product Already in List", Toast.LENGTH_SHORT).show();

        }
    }
}
