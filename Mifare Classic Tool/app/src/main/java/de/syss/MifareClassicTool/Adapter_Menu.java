package de.syss.MifareClassicTool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;



import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.syss.MifareClassicTool.Room.Product;
import de.syss.MifareClassicTool.Room.ProductDao;
import de.syss.MifareClassicTool.Room.ProductDatabase;

public class Adapter_Menu extends RecyclerView.Adapter<Adapter_Menu.MyViewHolder> {

    private Context context;
    private List<Model_Menu> liveList;
    private List<Model_Menu> filteredList; // Added for search functionality
    private ProductClick productClick;
TextView tvPrice;
    public interface ProductClick {
        void notifyData(int position);
        void MenuClick(int position, String itemName, int itemCode, String purchaseUnit, String saleUnit,
                       double sp, int mainGroup, String itemGroup, String itemSubGroup, String Saletaxcode);

        void  NoteClick(int position, String itemName, int itemCode, String purchaseUnit, String saleUnit,
                          double sp, int mainGroup, String itemGroup, String itemSubGroup, String Saletaxcode);

        void AddClick(int position, String itemName, int itemCode, String purchaseUnit, String saleUnit,
                          double sp, int mainGroup, String itemGroup, String itemSubGroup, String Saletaxcode);
     }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNameOfItem, tvItemPrice, tvAddText, tvQnt;
        LinearLayout llMenu,llAddOrder,llAddGreyOrder;
        ImageView imgAddQnt, imgMinusQnt;

        public MyViewHolder(View view) {
            super(view);
            tvNameOfItem = view.findViewById(R.id.tvNameOfItem);
            tvItemPrice = view.findViewById(R.id.tvItemPrice);

            tvAddText = view.findViewById(R.id.tvAddText);
            llAddGreyOrder = view.findViewById(R.id.llAddGreyOrder);
            llAddOrder = view.findViewById(R.id.llAddOrder);
            llMenu = view.findViewById(R.id.llMenu);
        }
    }

    public Adapter_Menu(Context context, List<Model_Menu> liveList, TextView tvPrice) {
        this.context = context;
        this.liveList = liveList;
        this.tvPrice = tvPrice;
        this.filteredList = new ArrayList<>(liveList);
    }

    public void setData(List<Model_Menu> newData) {
        this.liveList = newData;
        this.filteredList = new ArrayList<>(newData);
        notifyDataSetChanged();
    }

    public void filterData(String query) {
        filteredList.clear();
        if (TextUtils.isEmpty(query)) {
            filteredList.addAll(liveList);
        } else {
            String lowerCaseQuery = query.toLowerCase(Locale.getDefault());
            for (Model_Menu live : liveList) {
                if (live.getItemName().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery)) {
                    filteredList.add(live);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Model_Menu live = filteredList.get(position);
        if (live != null) {
            holder.tvNameOfItem.setText(live.getItemName());

            holder.tvItemPrice.setText(""); // Clear any previous text

            String priceText = "Price : ";
            String mainText = "\u20B9 " + live.getSp();

// Create a SpannableString with the desired colors
            SpannableString spannableString = new SpannableString(priceText + mainText);

// Set color for "Price : "
            spannableString.setSpan(new ForegroundColorSpan(holder.itemView.getContext().getResources().getColor(R.color.black)), 0, priceText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

// Set color for "\u20B9 " + live.getSp()
            spannableString.setSpan(new ForegroundColorSpan(holder.itemView.getContext().getResources().getColor(R.color.main)), priceText.length(), spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

// Apply the SpannableString to the TextView
            holder.tvItemPrice.setText(spannableString);

            holder.llMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (productClick != null) {
                        productClick.MenuClick(position, live.getItemName(), live.getItemCode(),
                                live.getPurchaseUnit(), live.getSaleUnit(), live.getSp(),
                                live.getMainGroup(), live.getItemGroup(), live.getItemSubGroup(), live.getSaletaxcode());
                    } else {
                        Log.e("AdapterData", "productClick is null");
                    }
                }
            });

//            holder.tvAddNote.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (productClick != null) {
//                        productClick.NoteClick(position, live.getItemName(), live.getItemCode(),
//                                live.getPurchaseUnit(), live.getSaleUnit(), live.getSp(),
//                                live.getMainGroup(), live.getItemGroup(), live.getItemSubGroup(), live.getSaletaxcode());
//                    } else {
//                        Log.e("AdapterData", "productClick is null");
//                    }
//                }
//            });


            holder.llAddOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (productClick != null) {
                        productClick.AddClick(position, live.getItemName(), live.getItemCode(),
                                live.getPurchaseUnit(), live.getSaleUnit(), live.getSp(),
                                live.getMainGroup(), live.getItemGroup(), live.getItemSubGroup(), live.getSaletaxcode());
 productClick.notifyData(position);
//                        holder.tvAddText.setText("Added");
                        updatePrice();
                    } else {
                        Log.e("AdapterData", "productClick is null");
                    }
                }
            });
        } else {
            Toast.makeText(context, "Data is Null", Toast.LENGTH_SHORT).show();
        }
    }

    public void set(ProductClick onClick) {
        this.productClick = onClick;
    }

    public void updatePrice() {
        ProductDatabase db = Room.databaseBuilder(context,
                ProductDatabase.class, "cart_db").allowMainThreadQueries().build();
        ProductDao productDao = db.ProductDao();

        List<Product> productList = productDao.getallproduct();

        double totalSp = 0; // Variable to store the total sp value

        for (Product product : productList) {
            Log.e("DatabaseData", "Product ID: " + product.getPid() +
                    ", Product Name: " + product.getItemName() + ", Sp value: " + product.getSp() +
                    ", Sale Unit: " + product.getSaleUnit() + ", Qnt Unit: " + product.getQnt() +
                    ", SP: " + product.getSp());

            totalSp += product.getSp(); // Accumulate the sp values
        }



        tvPrice.setText("Total : "+"\u20B9"+  totalSp);
    }

}
