package de.syss.MifareClassicTool;

public class Model_OccRecyclerview {
    String memberID,name,userValue,locationName,locationId,waiterID,memberType,couponNo,cashierCode;
int aid,bqnt,tid,locid;
    public Model_OccRecyclerview(int aid, String memberID,String name,String memberType,String couponNo, String cashierCode, String userValue, String locationName,
                                 String locationId, String waiterID, int bqnt, int tid, int locid) {
        this.aid = aid;
        this.memberID = memberID;
        this.name = name;
        this.memberType = memberType;
        this.couponNo = couponNo;
        this.cashierCode = cashierCode;
        this.userValue = userValue;
        this.locationName = locationName;
        this.locationId = locationId;
        this.waiterID = waiterID;
        this.bqnt = bqnt;
        this.tid = tid;
        this.locid = locid;
    }

    public String getCouponNo() {
        return couponNo;
    }

    public void setCouponNo(String couponNo) {
        this.couponNo = couponNo;
    }

    public String getCashierCode() {
        return cashierCode;
    }

    public void setCashierCode(String cashierCode) {
        this.cashierCode = cashierCode;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getWaiterID() {
        return waiterID;
    }

    public void setWaiterID(String waiterID) {
        this.waiterID = waiterID;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
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
