package de.syss.MifareClassicTool;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.syss.MifareClassicTool.Room.Bill;
import de.syss.MifareClassicTool.Room.BillDao;
import de.syss.MifareClassicTool.Room.BillDatabase;

public class Adapter_Tableqnt extends RecyclerView.Adapter<Adapter_Tableqnt.MyViewHolder> {


    private Context context;
    private List<Model_Tableqnt> liveList;

    private ProductClick productClick;

    String BillNoFinal,MemberId,MemberIdFinal;
    int check=0;
    //make interface like this
    public interface ProductClick {


        void tableClick(int position, String tableId,String location);

        void tableEngageClick(int position, String tableId,String location,String MemberIdFinal);


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

        // Initiate async task to fetch data
        new GetBillDataTask(holder, live.getLocation(), live.getTableId()).execute();

        holder.rlTableEnagage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productClick.tableEngageClick(position, live.getTableId(), live.getLocation(),MemberIdFinal);
            }
        });

        holder.rlTablelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productClick.tableClick(position, live.getTableId(), live.getLocation());
            }
        });

        if (live != null) {
            holder.tvTablenumber.setText("Table : " + live.getTableId());
            holder.tvTNoEnagage.setText("Table : " + live.getTableId());
        } else {
            Log.e("Adapter_Tableqnt", "Data is null at position: ");
        }

        Log.e("ValtableId", String.valueOf(live.getTableId()));
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void set(ProductClick onClick) {
        this.productClick = onClick;
    }

    private class GetBillDataTask extends AsyncTask<Void, Void, Boolean> {
        private MyViewHolder holder;
        private String location;
        private String tableId;

        public GetBillDataTask(MyViewHolder holder, String location, String tableId) {
            this.holder = holder;
            this.location = location;
            this.tableId = tableId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return getBillData(location, tableId);
        }

        @Override
        protected void onPostExecute(Boolean isBillFound) {
            if (isBillFound) {
                holder.rlTableEnagage.setVisibility(View.VISIBLE);
                holder.rlTablelayout.setVisibility(View.GONE);
            } else {

                holder.rlTableEnagage.setVisibility(View.GONE);
                holder.rlTablelayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean getBillData(String code, String tableId) {
        boolean isBillFound = false;
        try (Connection connect = new ConnectionHelper(context).connectionclass()) {
            if (connect != null) {
                Log.e("billData", "Connected to the database");

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String currentDate = dateFormat.format(new Date());

                String selectQuery = "SELECT * FROM TI_BarBillHead WHERE LocationCode = ? AND TableId = ? AND BillStatus = 0";

                try (PreparedStatement preparedStatement = connect.prepareStatement(selectQuery)) {
                    preparedStatement.setString(1, code);
                    preparedStatement.setString(2, tableId);
                    Log.e("billData", "Executing query: " + preparedStatement.toString());
                    ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        String BillNo = resultSet.getString("BillNo");
                        String BillDate = resultSet.getString("BillDate");
                        MemberId = resultSet.getString("MemberId");
                        BillDate = BillDate.split(" ")[0]; // Assuming the date format is consistent
                        Log.e("billData", "BillNo: " + BillNo + ", BillDate: " + BillDate);
                        if (BillDate.equals(currentDate)) {
                            BillNoFinal = BillNo;
                            MemberIdFinal = MemberId;
                            memberIdFinalMethod( MemberIdFinal);
                            check = 1;
                            Log.e("billData", "Matching BillNo found: " + BillNoFinal+" "+MemberIdFinal);
                            isBillFound = true;
                            break;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.e("Error", "SQLException: " + e.getMessage());
                }
            } else {
                Log.e("Error", "Failed to connect to the database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("Error", "SQLException while retrieving data: " + e.getMessage());
        }
        return isBillFound;
    }

    public static String removeLastAlphabet(String memberIdFinal) {
        if (memberIdFinal == null || memberIdFinal.isEmpty()) {
            return memberIdFinal;
        }

        char lastChar = memberIdFinal.charAt(memberIdFinal.length() - 1);

        if (Character.isLetter(lastChar)) {
            return memberIdFinal.substring(0, memberIdFinal.length() - 1);
        }

        return memberIdFinal;
    }

    public static void memberIdFinalMethod( String memberId) {
        String memberIdFinal = memberId;
        String modifiedMemberId = removeLastAlphabet(memberIdFinal);
        System.out.println(modifiedMemberId);  // Output: P-2297
    }


}
