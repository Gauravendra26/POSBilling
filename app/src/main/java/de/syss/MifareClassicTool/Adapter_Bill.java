package de.syss.MifareClassicTool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import de.syss.MifareClassicTool.Room.Bill;
import de.syss.MifareClassicTool.Room.Product;

public class Adapter_Bill extends RecyclerView.Adapter<Adapter_Bill.MyViewHolder> {

    private Context context;
    private List<Model_OccupiedTable> liveList;
    private ProductClick productClick;
     TextView tvTotalOfBill ;

    public interface ProductClick {
        // Your interface methods (if any)
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvItemnameWithQnt, tvItemAmount;

        public MyViewHolder(View view) {
            super(view);
            tvItemnameWithQnt = view.findViewById(R.id.tvItemnameWithQnt);
            tvItemAmount = view.findViewById(R.id.tvItemAmount);
        }
    }

    public Adapter_Bill(Context context, List<Model_OccupiedTable> liveList, TextView tvTotalOfBill ) {
        this.context = context;
        this.liveList = liveList;
        this.tvTotalOfBill = tvTotalOfBill;

    }

    public void setData(List<Model_OccupiedTable> newData) {
        this.liveList = newData;
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
                .inflate(R.layout.bill_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Model_OccupiedTable live = liveList.get(position);
        double sum=0;
        for (int i = 0; i < liveList.size(); i++) {
            Model_OccupiedTable live1 = liveList.get(i);
            try {
                double price = Double.parseDouble(live1.getItePrice());
                double quantity = Double.parseDouble(live1.getIteQnt());
                sum += (price * quantity);
                Log.e("dataofPrice", price + " " + quantity);
            } catch (NumberFormatException e) {
                // Handle the case where parsing fails
                Log.e("ParsingError", "Error parsing price or quantity for item " + i);
                // Optionally, you can log the problematic data or display an error message
            }
        }

//        Toast.makeText(context, ""+sum, Toast.LENGTH_SHORT).show();
        tvTotalOfBill.setText("\u20B9 " + sum);

        double price = Double.parseDouble(live.getItePrice());
        double quantity = Double.parseDouble(live.getIteQnt());
        int quantityInt = (int) quantity; // If you need quantity as an integer later
if (quantityInt==1){
    holder.tvItemnameWithQnt.setText(live.getIteName() + "-" + quantityInt + "pc");

} else{
    holder.tvItemnameWithQnt.setText(live.getIteName() + "-" + quantityInt + "pcs");

}

        holder.tvItemAmount.setText("\u20B9 " + (price * quantity));


    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void set(ProductClick onClick) {
        this.productClick = onClick;
    }


}
