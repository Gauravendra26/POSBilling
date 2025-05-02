package de.syss.MifareClassicTool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter_OccRecyclerview extends RecyclerView.Adapter<Adapter_OccRecyclerview.MyViewHolder> {


    private Context context;
    private List<Model_OccRecyclerview> liveList;

    private ProductClick productClick;

    //make interface like this
    public interface ProductClick {

//        void notifyData(int position);
        void waiterClick(int position,int aid, String memberID,String name,String memberType,String couponNo,String cashierCode, String userValue, String locationName,
                         String locationId, String waiterID, int bqnt, int tid, int locid);
//        void AddNoteOrderClick(int position,String itemName, int itemCode,String purchaseUnit,String saleUnit,double sp,
//                               int mainGroup, String itemGroup, String itemSubGroup);


    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
LinearLayout llTableDeatils;
  TextView tvMemberId,tvTableId,tvName,tvLocationName,tvKOTNo;

        public MyViewHolder(View view) {
            super(view);
            llTableDeatils = view.findViewById(R.id.llTableDeatils);
            tvMemberId = view.findViewById(R.id.tvMemberId);
            tvTableId = view.findViewById(R.id.tvTableId);
            tvName = view.findViewById(R.id.tvName);
            tvLocationName = view.findViewById(R.id.tvLocationName);
            tvKOTNo = view.findViewById(R.id.tvKOTNo);

        }
    }

    public Adapter_OccRecyclerview(Context context, List<Model_OccRecyclerview> liveList) {
        //List<SiderImageModel> slider_image_list
        this.context = context;
        this.liveList = liveList;
        ////this.slider_image_list = slider_image_list;

    }

    public void setData(List<Model_OccRecyclerview> newData) {
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
                .inflate(R.layout.occupiedtable_layout, parent, false);
        return new MyViewHolder(itemView);


    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Model_OccRecyclerview live = liveList.get(position);

        holder.tvMemberId.setText(live.getMemberID());
        holder.tvName.setText(live.getName());
        holder.tvLocationName.setText(live.getLocationName());
        holder.tvTableId.setText(""+live.getTid());
        holder.tvKOTNo.setText(""+live.getAid());



        holder.llTableDeatils.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productClick.waiterClick(position, live.getAid(), live.getMemberID(), live.getName(),
                    live.getMemberType(),live.getCouponNo(),live.getCashierCode(),
                    live.getUserValue(), live.getLocationName(), live.getLocationId(),
                    live.getWaiterID(), live.getBqnt(), live.getTid(), live.getLocid() );

            }
        });
//


    }

    @Override
    public int getItemViewType(int position) {

        return super.getItemViewType(position);
    }

    public void set(ProductClick onClick) {
        this.productClick = onClick;
    }

}
