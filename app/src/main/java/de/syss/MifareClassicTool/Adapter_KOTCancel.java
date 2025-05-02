package de.syss.MifareClassicTool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Adapter_KOTCancel extends RecyclerView.Adapter<Adapter_KOTCancel.MyViewHolder> {

    private Context context;
    private List<Model_KOTCancel> liveList;
    private ProductClick productClick;


    public interface ProductClick {

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvKOTNum;
        Button buttonCancel;

        public MyViewHolder(View view) {
            super(view);
            tvKOTNum = view.findViewById(R.id.tvKOTNum);
            buttonCancel = view.findViewById(R.id.buttonCancel);
//            tvCGSTAmount = view.findViewById(R.id.tvCGSTAmount);
//            tvSGSTAmount = view.findViewById(R.id.tvSGSTAmount);
        }
    }

    public Adapter_KOTCancel(Context context, List<Model_KOTCancel> liveList) {
        this.context = context;
        this.liveList = liveList;
    }

    public void setData(List<Model_KOTCancel> newData) {
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
            .inflate(R.layout.kotcancel_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model_KOTCancel live = liveList.get(position);


        holder.tvKOTNum.setText(live.getKotNumber());

         holder.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (productClick != null) {
//                    productClick.AddClick(position, live.getItemName(), live.getItemCode(),
//                        live.getPurchaseUnit(), live.getSaleUnit(), live.getSp(),
//                        live.getMainGroup(), live.getItemGroup(), live.getItemSubGroup(), live.getSaletaxcode());
//                    productClick.notifyData(position);
//
//                    updatePrice();
//                } else {
//                    Log.e("AdapterData", "productClick is null");
//                }
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
