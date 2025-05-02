package de.syss.MifareClassicTool.Room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity
public class Bill {

    @PrimaryKey(autoGenerate = true)
    public int aid;

    @ColumnInfo(name = "memberID")
    public String memberID;
    @ColumnInfo(name = "userValue")
    public String userValue;
    @ColumnInfo(name = "locationName")
    public String locationName;
    @ColumnInfo(name = "waiterID")
    public String waiterID;
    @ColumnInfo(name = "locationId")
    public String locationId;
    @ColumnInfo(name = "kqnt")
    public String kqnt;

    @ColumnInfo(name = "kqnt1")
    public String kqnt1;

    @ColumnInfo(name = "kqnt2")
    public String kqnt2;
    @ColumnInfo(name = "kqnt3")
    public String kqnt3;
    @ColumnInfo(name = "kqnt4")
    public String kqnt4;
    @ColumnInfo(name = "kqnt5")
    public String kqnt5;

    @ColumnInfo(name = "bqnt")
    public int bqnt;

    @ColumnInfo(name = "tid")
    public int tid;

    @ColumnInfo(name = "locid")
    public int locid;

    public Bill(int aid, String memberID, String userValue, String locationName, String waiterID, String locationId, String kqnt, String kqnt1, String kqnt2, String kqnt3, String kqnt4, String kqnt5, int bqnt, int tid, int locid) {
        this.aid = aid;
        this.memberID = memberID;
        this.userValue = userValue;
        this.locationName = locationName;
        this.waiterID = waiterID;
        this.locationId = locationId;
        this.kqnt = kqnt;
        this.kqnt1 = kqnt1;
        this.kqnt2 = kqnt2;
        this.kqnt3 = kqnt3;
        this.kqnt4 = kqnt4;
        this.kqnt5 = kqnt5;
        this.bqnt = bqnt;
        this.tid = tid;
        this.locid = locid;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getMemberID() {
        return memberID;
    }

    public void setMemberID(String memberID) {
        this.memberID = memberID;
    }

    public String getUserValue() {
        return userValue;
    }

    public void setUserValue(String userValue) {
        this.userValue = userValue;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getWaiterID() {
        return waiterID;
    }

    public void setWaiterID(String waiterID) {
        this.waiterID = waiterID;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getKqnt() {
        return kqnt;
    }

    public void setKqnt(String kqnt) {
        this.kqnt = kqnt;
    }

    public String getKqnt1() {
        return kqnt1;
    }

    public void setKqnt1(String kqnt1) {
        this.kqnt1 = kqnt1;
    }

    public String getKqnt2() {
        return kqnt2;
    }

    public void setKqnt2(String kqnt2) {
        this.kqnt2 = kqnt2;
    }

    public String getKqnt3() {
        return kqnt3;
    }

    public void setKqnt3(String kqnt3) {
        this.kqnt3 = kqnt3;
    }

    public String getKqnt4() {
        return kqnt4;
    }

    public void setKqnt4(String kqnt4) {
        this.kqnt4 = kqnt4;
    }

    public String getKqnt5() {
        return kqnt5;
    }

    public void setKqnt5(String kqnt5) {
        this.kqnt5 = kqnt5;
    }

    public int getBqnt() {
        return bqnt;
    }

    public void setBqnt(int bqnt) {
        this.bqnt = bqnt;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getLocid() {
        return locid;
    }

    public void setLocid(int locid) {
        this.locid = locid;
    }
}
