package de.syss.MifareClassicTool.Room;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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

import de.syss.MifareClassicTool.Model_GST;
import de.syss.MifareClassicTool.Model_OccupiedTable;
import de.syss.MifareClassicTool.R;

public class Adapter_OrderCancel extends RecyclerView.Adapter<Adapter_OrderCancel.MyViewHolder> {

    private Context context;
    private List<Model_OccupiedTable> liveList;
    private ProductClick productClick;

    private ArrayList<Double> totalAmountFinalList = new ArrayList<>();

    public interface ProductClick {
        // Define your interface methods here
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvNameOfItem, tvPriceofItem, tvQuantity ;

        public MyViewHolder(View view) {
            super(view);
            tvNameOfItem = view.findViewById(R.id.tvNameOfItem);
            tvPriceofItem = view.findViewById(R.id.tvPriceofItem);
            tvQuantity = view.findViewById(R.id.tvQuantity);
         }
    }

    public Adapter_OrderCancel(Context context, List<Model_OccupiedTable> liveList) {
        this.context = context;
        this.liveList = liveList;
    }

    public void setData(List<Model_OccupiedTable> newData) {
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
            .inflate(R.layout.order_cancel_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model_OccupiedTable live = liveList.get(position);


        holder.tvNameOfItem.setText(live.getIteName());

        holder.tvPriceofItem.setText("");
        String priceText = "Price : ";
        String mainText = "\u20B9 " + live.getItePrice();
        SpannableString spannableString = new SpannableString(priceText + mainText);
        spannableString.setSpan(new ForegroundColorSpan(holder.itemView.getContext().getResources()
            .getColor(R.color.black)), 0, priceText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(holder.itemView.getContext().getResources()
            .getColor(R.color.main)), priceText.length(), spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tvPriceofItem.setText(spannableString);

        holder.tvQuantity.setText("");
        String QuantityText = "Quantity : ";
        String QuantitymainText =  live.getIteQnt();
        SpannableString spannableQuantityString = new SpannableString(QuantityText + QuantitymainText);
        spannableQuantityString.setSpan(new ForegroundColorSpan(holder.itemView.getContext().getResources()
            .getColor(R.color.black)), 0, QuantityText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableQuantityString.setSpan(new ForegroundColorSpan(holder.itemView.getContext().getResources()
            .getColor(R.color.main)), QuantityText.length(), spannableQuantityString.length(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tvQuantity.setText(spannableQuantityString);


    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void set(ProductClick onClick) {
        this.productClick = onClick;
    }
}
