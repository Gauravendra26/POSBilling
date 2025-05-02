package de.syss.MifareClassicTool;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.List;

import de.syss.MifareClassicTool.Room.Product;
import de.syss.MifareClassicTool.Room.ProductDao;
import de.syss.MifareClassicTool.Room.ProductDatabase;

public class Adapter_Waiter extends RecyclerView.Adapter<Adapter_Waiter.MyViewHolder> {


    private Context context;
    private List<Model_Waiter> liveList;

    private ProductClick productClick;

    //make interface like this
    public interface ProductClick {

//        void notifyData(int position);
        void waiterClick(int position,String id, String displayAs);
//        void AddNoteOrderClick(int position,String itemName, int itemCode,String purchaseUnit,String saleUnit,double sp,
//                               int mainGroup, String itemGroup, String itemSubGroup);


    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

  Button buttonWaiter;

        public MyViewHolder(View view) {
            super(view);
            buttonWaiter = view.findViewById(R.id.buttonWaiter);


        }
    }

    public Adapter_Waiter(Context context, List<Model_Waiter> liveList) {
        //List<SiderImageModel> slider_image_list
        this.context = context;
        this.liveList = liveList;
        ////this.slider_image_list = slider_image_list;

    }

    public void setData(List<Model_Waiter> newData) {
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
                .inflate(R.layout.waiter_layout, parent, false);
        return new MyViewHolder(itemView);


    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Model_Waiter live = liveList.get(position);

        holder.buttonWaiter.setText(live.getDisplayAs());



        holder.buttonWaiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productClick.waiterClick(position, live.getId(), live.getDisplayAs() );
//                SharedPreferences sharedPreferences = context.getSharedPreferences("Waiter", MODE_PRIVATE);
//                SharedPreferences.Editor myEdit = sharedPreferences.edit();
//                myEdit.putString("waiterid", String.valueOf(live.getId()));
//                myEdit.putString("waiterDisplay", String.valueOf(live.getDisplayAs()));
//                myEdit.apply();
//                myEdit.commit();
//                Toast.makeText(context, "Waiter Added", Toast.LENGTH_SHORT).show();
//                final Animation myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
//                holder.rlNotEvaluated.startAnimation(myAnim);

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
