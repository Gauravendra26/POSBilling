package de.syss.MifareClassicTool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.List;

import de.syss.MifareClassicTool.Room.Product;

public class Adapter_Bill extends RecyclerView.Adapter<Adapter_Bill.MyViewHolder> {

    private Context context;
    private List<Product> liveList;
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

    public Adapter_Bill(Context context, List<Product> liveList, TextView tvTotalOfBill ) {
        this.context = context;
        this.liveList = liveList;
        this.tvTotalOfBill = tvTotalOfBill;

    }

    public void setData(List<Product> newData) {
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
        Product live = liveList.get(position);
        double sum=0;
        for (int i = 0; i < liveList.size()  ; i++){
            Product live1 = liveList.get(i);
            sum =sum + (live1.getSp()*live1.getQnt());
        }
//        Toast.makeText(context, ""+sum, Toast.LENGTH_SHORT).show();
        tvTotalOfBill.setText( "\u20B9 "+sum );
        holder.tvItemnameWithQnt.setText(live.getItemName()+"-"+live.getQnt()+"pcs");
        holder.tvItemAmount.setText( "\u20B9 "+live.getSp()*live.getQnt());
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void set(ProductClick onClick) {
        this.productClick = onClick;
    }


}
