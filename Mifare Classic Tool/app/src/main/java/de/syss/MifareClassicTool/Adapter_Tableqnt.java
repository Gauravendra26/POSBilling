package de.syss.MifareClassicTool;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;

import de.syss.MifareClassicTool.Room.Count;
import de.syss.MifareClassicTool.Room.CountDao;
import de.syss.MifareClassicTool.Room.CountDatabase;

public class Adapter_Tableqnt extends RecyclerView.Adapter<Adapter_Tableqnt.MyViewHolder> {


    private Context context;
    private List<Model_Tableqnt> liveList;

    private ProductClick productClick;

    //make interface like this
    public interface ProductClick {


        void tableClick(int position, String tableId);

        void tableEngageClick(int position, String tableId);


    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTablenumber, tvTNoEnagage;

        RelativeLayout rlTablelayout, rlTableEnagage;

        public MyViewHolder(View view) {
            super(view);
            tvTNoEnagage = view.findViewById(R.id.tvTNoEnagage);
            tvTablenumber = view.findViewById(R.id.tvTablenumber);
            rlTableEnagage = view.findViewById(R.id.rlTableEnagage);
            rlTablelayout = view.findViewById(R.id.rlTablelayout);

        }
    }

    public Adapter_Tableqnt(Context context, List<Model_Tableqnt> liveList) {
        //List<SiderImageModel> slider_image_list
        this.context = context;
        this.liveList = liveList;
        ////this.slider_image_list = slider_image_list;

    }

    public void setData(List<Model_Tableqnt> newData) {
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
            .inflate(R.layout.table_layout, parent, false);
        return new MyViewHolder(itemView);


    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Model_Tableqnt live = liveList.get(position);
        CountDatabase dbC = Room.databaseBuilder(context,
            CountDatabase.class, "count_db").allowMainThreadQueries().build();
        CountDao countDao = dbC.CountDao();
        List<Count> allProductsCount = countDao.getdataofallproduct();

        boolean shouldShowEngageLayout = false;

        for (Count countProduct : allProductsCount) {
            if (countProduct.getLocid() == Integer.valueOf(live.getLocation())) {
                if (countProduct.getTid() == Integer.valueOf(live.getTableId())) {  // Change the condition based on your requirement
                    shouldShowEngageLayout = true;
                    break;
                }
            }
        }

        if (shouldShowEngageLayout) {

            holder.rlTableEnagage.setVisibility(View.VISIBLE);
            holder.rlTablelayout.setVisibility(View.GONE);

            holder.rlTableEnagage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Retrieve aid from the database based on locid and tid
                    int locId = Integer.valueOf(live.getLocation());
                    int tableId = Integer.valueOf(live.getTableId());

                    CountDao countDao = dbC.CountDao();
                    Count specificCount = countDao.getCountByLocIdAndTableId(locId, tableId);

                    if (specificCount != null) {
                        int aid = specificCount.getAid();
                        SharedPreferences sharedPreferences = context.getSharedPreferences("KotDetails", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("Aid",String.valueOf(aid));
                        myEdit.apply();
                        myEdit.commit();
                        productClick.tableEngageClick(position, live.getTableId());
                        Toast.makeText(context, "Engaged "+aid, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            holder.rlTableEnagage.setVisibility(View.GONE);
            holder.rlTablelayout.setVisibility(View.VISIBLE);
            holder.rlTablelayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    productClick.tableClick(position, live.getTableId());
                }
            });
        }


        if (live != null) {
            holder.tvTablenumber.setText("Table : " + live.getTableId());
            holder.tvTNoEnagage.setText("Table : " + live.getTableId());
        } else {
            Log.e("Adapter_Tableqnt", "Data is null at position: ");
        }

        Log.e("ValtableId", live.getTableId());


    }

    @Override
    public int getItemViewType(int position) {

        return super.getItemViewType(position);
    }

    public void set(ProductClick onClick) {
        this.productClick = onClick;
    }

}
