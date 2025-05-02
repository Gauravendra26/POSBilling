package de.syss.MifareClassicTool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;


import java.util.List;

import de.syss.MifareClassicTool.Room.Product;
import de.syss.MifareClassicTool.Room.ProductDao;
import de.syss.MifareClassicTool.Room.ProductDatabase;

public class Adapter_Order extends RecyclerView.Adapter<Adapter_Order.MyViewHolder> {


    private Context context;
    private List<Product> liveList;

    private ProductClick productClick;

    //make interface like this
    public interface ProductClick {

        void notifyData(int position);
        void OrderClick(int position,String itemName, int itemCode,String purchaseUnit,String saleUnit,double sp,
                        int mainGroup, String itemGroup, String itemSubGroup);
        void AddNoteOrderClick(int position,String itemName, int itemCode,String purchaseUnit,String saleUnit,double sp,
                               int mainGroup, String itemGroup, String itemSubGroup);


    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNameOfItem, tvAddNote,tvPriceofItem,tvQnt;

        LinearLayout llOrderLayout;
ImageView imgAdd, imgMinus;
        public MyViewHolder(View view) {
            super(view);
             tvNameOfItem = view.findViewById(R.id.tvNameOfItem);
            tvAddNote = view.findViewById(R.id.tvAddNote);
            tvPriceofItem = view.findViewById(R.id.tvPriceofItem);
            tvQnt = view.findViewById(R.id.tvQnt);
            imgAdd = view.findViewById(R.id.imgAdd);
            imgMinus = view.findViewById(R.id.imgMinus);
            llOrderLayout = view.findViewById(R.id.llOrderLayout);

        }
    }

    public Adapter_Order(Context context, List<Product> liveList) {
        //List<SiderImageModel> slider_image_list
        this.context = context;
        this.liveList = liveList;
        ////this.slider_image_list = slider_image_list;

    }

    public void setData(List<Product> newData) {
        this.liveList = newData;
        // Save data to cache
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {

        return liveList.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_layout, parent, false);
        return new MyViewHolder(itemView);


    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Product live = liveList.get(position);

        holder.tvNameOfItem.setText(live.getItemName());

        holder.tvPriceofItem.setText("");
        String priceText = "Price : ";
        String mainText = "\u20B9 " + live.getSp();
        SpannableString spannableString = new SpannableString(priceText + mainText);
        spannableString.setSpan(new ForegroundColorSpan(holder.itemView.getContext().getResources().getColor(R.color.black)), 0, priceText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(holder.itemView.getContext().getResources().getColor(R.color.main)), priceText.length(), spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tvPriceofItem.setText(spannableString);
        holder.tvQnt.setText(""+live.getQnt());

//        holder.llOrderLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                productClick.OrderClick(position, live.getItemName(), live.getItemCode(),live.getPurchaseUnit(),
//                        live.getSaleUnit(),live.getSp(),live.getMainGroup(),live.getItemGroup(),live.getItemSubGroup() );
//
//
////                final Animation myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
////                holder.rlNotEvaluated.startAnimation(myAnim);
//
//            }
//        });
        holder.tvAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productClick.AddNoteOrderClick(position, live.getItemName(), live.getItemCode(),live.getPurchaseUnit(),
                        live.getSaleUnit(),live.getSp(),live.getMainGroup(),live.getItemGroup(),live.getItemSubGroup() );


//                final Animation myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
//                holder.rlNotEvaluated.startAnimation(myAnim);

            }
        });
        holder.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = (int) liveList.get(position).getQnt();
                qty++;
                liveList.get(position).setQnt(qty);
                holder.tvQnt.setText("" + qty);
//                onClick.setNotify(position);
                ProductDatabase db = Room.databaseBuilder(context,
                        ProductDatabase.class, "cart_db").allowMainThreadQueries().build();
                ProductDao productDao = db.ProductDao();
                productDao.updateRecord(live.getPid(), live.getQnt());
                productClick.notifyData(position);
                Log.e("Dataofnotify", ""+productDao.getallproduct().get(0).qnt);

            }
        });

        holder.imgMinus.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(holder.tvQnt.getText().toString()) > 0) {
                    int qty = (int) liveList.get(position).getQnt();

                    if (qty > 0) {
                        qty = qty - 1;

                        // Ensure the quantity doesn't go below 0
                        if (qty < 0) {
                            qty = 0;
                        }

                        ProductDatabase db = Room.databaseBuilder(context,
                                ProductDatabase.class, "cart_db").allowMainThreadQueries().build();
                        ProductDao productDao = db.ProductDao();
                        productDao.updateRecord(live.getPid(), qty);
                        liveList.get(position).setQnt(qty);
                        holder.tvQnt.setText("" + qty);
                        productClick.notifyData(position);
                    }
                }

            }
        });

    }

    @Override
    public int getItemViewType(int position) {

        return super.getItemViewType(position);
    }

    public void set(ProductClick onClick) {
        this.productClick = onClick;
    }

}
