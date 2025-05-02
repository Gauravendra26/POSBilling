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
import java.util.ArrayList;
import java.util.List;

public class Adapter_GST extends RecyclerView.Adapter<Adapter_GST.MyViewHolder> {

    private Context context;
    private List<Model_GST> liveList;
    private ProductClick productClick;

    private ArrayList<Double> totalAmountFinalList = new ArrayList<>();

    public interface ProductClick {
        // Define your interface methods here
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvCGST, tvSGST, tvCGSTAmount, tvSGSTAmount;

        public MyViewHolder(View view) {
            super(view);
            tvCGST = view.findViewById(R.id.tvCGST);
            tvSGST = view.findViewById(R.id.tvSGST);
            tvCGSTAmount = view.findViewById(R.id.tvCGSTAmount);
            tvSGSTAmount = view.findViewById(R.id.tvSGSTAmount);
        }
    }

    public Adapter_GST(Context context, List<Model_GST> liveList) {
        this.context = context;
        this.liveList = liveList;
    }

    public void setData(List<Model_GST> newData) {
        this.liveList = newData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return liveList != null ? liveList.size() : 0;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.gst_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model_GST live = liveList.get(position);

        double gst = Double.parseDouble(live.getGst());
        double cgstPercentage = gst / 2;
        double sgstPercentage = gst / 2;

        double amountValue = Double.parseDouble(live.getAmount());
        double amount1 = amountValue * Double.parseDouble(live.getQty());
        double amount = (amount1 * gst) / 100;
        double totalAmount = amount / 2;

        holder.tvCGST.setText(cgstPercentage + " %");
        holder.tvSGST.setText(sgstPercentage + " %");

        BigDecimal roundedGrand = new BigDecimal(totalAmount).setScale(2, RoundingMode.HALF_UP);
        holder.tvCGSTAmount.setText("+ \u20B9 " + roundedGrand);

        // Calculate SGST amount separately if needed
        BigDecimal roundedSGST = new BigDecimal(totalAmount).setScale(2, RoundingMode.HALF_UP);
        holder.tvSGSTAmount.setText("+ \u20B9 " + roundedSGST);

        // Calculate the total amount including both CGST and SGST
        double totalAmountFinal = totalAmount * 2;
        totalAmountFinalList.add(totalAmountFinal);

        // Log all data for the current item
        Log.d("Adapter_GST", "GST: " + live.getGst() +
            ", Amount: " + live.getAmount() +
            ", CGST Percentage: " + cgstPercentage +
            ", SGST Percentage: " + sgstPercentage +
            ", Amount with GST: " + amount +
            ", Total Amount: " + totalAmountFinal +
            ", Total Amount List: " + totalAmountFinalList.toString());

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void set(ProductClick onClick) {
        this.productClick = onClick;
    }
}
