package de.syss.MifareClassicTool.Room;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
@Dao
public interface BillDao {

    @Insert
    void insertrecord(Bill bill);


    @Query("SELECT * FROM Bill")
    List<Bill> getdataofallproduct();

    @Query("SELECT kqnt FROM Bill WHERE aid = :targetAid")
    String getKqntByAid(String targetAid);

    @Query("SELECT * FROM Bill WHERE aid = :aid")
    List<Bill> getBillByAid(int aid);
    @Query("SELECT EXISTS(SELECT * FROM Bill WHERE aid = :productid)")
    Boolean is_exist(int productid);


    @Query("SELECT * FROM Bill WHERE locid = :locId AND tid = :tableId LIMIT 1")
    Bill getCountByLocIdAndTableId(int locId, int tableId);
    @Query("DELETE FROM Bill WHERE aid = :id")
    void deleteById(int id);

    @Query("UPDATE Bill SET kqnt = :kqnt, bqnt = :bqnt WHERE aid = :id")
    public void updateRecord(int id ,int kqnt,int bqnt );





    @Query("UPDATE Bill SET memberID = :memberID, userValue = :userValue,locationName = :locationName," +
        "waiterID = :waiterID,locationId = :locationId,kqnt = :kqnt,kqnt1 = :kqnt1,kqnt2 = :kqnt2," +
        "kqnt3 = :kqnt3 ,kqnt4 = :kqnt4,kqnt5 = :kqnt5,bqnt = :bqnt,tid = :tid,locid = :locid  WHERE aid = :id")
    public void updateRecordByAid( int id, String memberID,   String userValue,   String locationName,
                                    String waiterID,String locationId,String kqnt,String kqnt1,String kqnt2
        ,String kqnt3,String kqnt4,String kqnt5,int bqnt,int tid,int locid );




}
