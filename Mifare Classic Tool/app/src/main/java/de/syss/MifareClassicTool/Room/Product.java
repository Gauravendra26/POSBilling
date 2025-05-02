package de.syss.MifareClassicTool.Room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class Product {

    @PrimaryKey(autoGenerate = true)
    public int pid;

    @ColumnInfo(name = "qnt")
    public int qnt;

    @ColumnInfo(name = "PitemName")
    public String itemName;

    @ColumnInfo(name = "PitemCode")
    public int itemCode;

    @ColumnInfo(name = "PpurchaseUnit")
    public String purchaseUnit;

    @ColumnInfo(name = "PsaleUnit")
    public String saleUnit;

    @ColumnInfo(name = "PitemGroup")
    public String itemGroup;

    @ColumnInfo(name = "PitemSubGroup")
    public String itemSubGroup;

    @ColumnInfo(name = "PSaletaxcode")
    public String Saletaxcode;

    @ColumnInfo(name = "Psp")
    public double sp;

    @ColumnInfo(name = "PmainGroup")
    public int mainGroup;

    public Product() {
        // Default constructor required by Room
    }
    public Product(int pid,int qnt, String itemName, int itemCode, String purchaseUnit, String saleUnit, double sp,
                   int mainGroup, String itemGroup, String itemSubGroup, String saletaxcode) {
        this.pid = pid;
        this.qnt = qnt;
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.purchaseUnit = purchaseUnit;
        this.saleUnit = saleUnit;
        this.itemGroup = itemGroup;
        this.itemSubGroup = itemSubGroup;
        Saletaxcode = saletaxcode;
        this.sp = sp;
        this.mainGroup = mainGroup;
    }

    public int getQnt() {
        return qnt;
    }

    public void setQnt(int qnt) {
        this.qnt = qnt;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemCode() {
        return itemCode;
    }

    public void setItemCode(int itemCode) {
        this.itemCode = itemCode;
    }

    public String getPurchaseUnit() {
        return purchaseUnit;
    }

    public void setPurchaseUnit(String purchaseUnit) {
        this.purchaseUnit = purchaseUnit;
    }

    public String getSaleUnit() {
        return saleUnit;
    }

    public void setSaleUnit(String saleUnit) {
        this.saleUnit = saleUnit;
    }

    public String getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(String itemGroup) {
        this.itemGroup = itemGroup;
    }

    public String getItemSubGroup() {
        return itemSubGroup;
    }

    public void setItemSubGroup(String itemSubGroup) {
        this.itemSubGroup = itemSubGroup;
    }

    public String getSaletaxcode() {
        return Saletaxcode;
    }

    public void setSaletaxcode(String saletaxcode) {
        Saletaxcode = saletaxcode;
    }

    public double getSp() {
        return sp;
    }

    public void setSp(double sp) {
        this.sp = sp;
    }

    public int getMainGroup() {
        return mainGroup;
    }

    public void setMainGroup(int mainGroup) {
        this.mainGroup = mainGroup;
    }
}
