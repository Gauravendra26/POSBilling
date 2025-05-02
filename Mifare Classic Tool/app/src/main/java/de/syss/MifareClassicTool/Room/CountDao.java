package de.syss.MifareClassicTool.Room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
@Dao
public interface CountDao {

    @Insert
    void insertrecord(Count count);

    @Query("SELECT kqnt FROM Count WHERE aid = :targetAid")
    String getKqntByAid(String targetAid);


    @Query("SELECT EXISTS(SELECT * FROM Count WHERE aid = :productid)")
    Boolean is_exist(int productid);

    @Query("SELECT * FROM Count")
    List<Count> getdataofallproduct();
    @Query("SELECT * FROM Count WHERE locid = :locId AND tid = :tableId LIMIT 1")
    Count getCountByLocIdAndTableId(int locId, int tableId);
    @Query("DELETE FROM Count WHERE aid = :id")
    void deleteById(int id);

    @Query("UPDATE Count SET kqnt = :kqnt, bqnt = :bqnt WHERE aid = :id")
    public void updateRecord(int id ,int kqnt,int bqnt );

}
