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

public class Adapter_KOT extends RecyclerView.Adapter<Adapter_KOT.MyViewHolder> {

    private Context context;
    private List<Product> liveList;
    private ProductClick productClick;
    private TextView tvTotalBottom, tvTotal;

    public interface ProductClick {
        // Your interface methods (if any)
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvitemName, tvitemQnt;

        public MyViewHolder(View view) {
            super(view);
            tvitemName = view.findViewById(R.id.tvitemName);
            tvitemQnt = view.findViewById(R.id.tvitemQnt);
        }
    }

    public Adapter_KOT(Context context, List<Product> liveList, TextView tvTotalBottom, TextView tvTotal) {
        this.context = context;
        this.liveList = liveList;
        this.tvTotalBottom = tvTotalBottom;
        this.tvTotal = tvTotal;
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
                .inflate(R.layout.kot_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        int sum = 0;
        for (int i = 0; i < liveList.size(); i++) {
            sum = sum + liveList.get(i).getQnt();
        }

        tvTotalBottom.setText("Total Qty: " + sum + "pcs");
        tvTotal.setText(sum + "pcs");

        Product live = liveList.get(position);
        holder.tvitemName.setText(live.getItemName());
        holder.tvitemQnt.setText(String.valueOf(live.getQnt()) + "pc");
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void set(ProductClick onClick) {
        this.productClick = onClick;
    }


}
