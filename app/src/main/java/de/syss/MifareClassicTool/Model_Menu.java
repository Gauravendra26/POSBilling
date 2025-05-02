package de.syss.MifareClassicTool;

public class Model_Menu {
    String itemName,purchaseUnit,saleUnit,itemGroup,itemSubGroup,Saletaxcode;
    double sp;
int itemCode,mainGroup;

    public Model_Menu(String itemName, int itemCode, String purchaseUnit, String saleUnit, double sp,
                      int mainGroup, String itemGroup, String itemSubGroup, String Saletaxcode) {
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.purchaseUnit = purchaseUnit;
        this.saleUnit = saleUnit;
        this.sp = sp;
        this.mainGroup = mainGroup;
        this.itemGroup = itemGroup;
        this.itemSubGroup = itemSubGroup;
        this.Saletaxcode = Saletaxcode;
    }

    public String getSaletaxcode() {
        return Saletaxcode;
    }

    public void setSaletaxcode(String saletaxcode) {
        Saletaxcode = saletaxcode;
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
