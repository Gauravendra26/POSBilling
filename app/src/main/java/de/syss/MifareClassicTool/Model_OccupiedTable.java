package de.syss.MifareClassicTool;

public class Model_OccupiedTable {

    String iteName,itePrice,iteQnt, ItemSaletaxcode,ItemCode;

    public Model_OccupiedTable(String iteName, String itePrice, String iteQnt
        , String ItemSaletaxcode, String ItemCode) {
        this.iteName = iteName;
        this.itePrice = itePrice;
        this.iteQnt = iteQnt;

        this.ItemSaletaxcode = ItemSaletaxcode;
        this.ItemCode = ItemCode;
    }


    public String getItemSaletaxcode() {
        return ItemSaletaxcode;
    }

    public void setItemSaletaxcode(String itemSaletaxcode) {
        ItemSaletaxcode = itemSaletaxcode;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public String getIteName() {
        return iteName;
    }

    public void setIteName(String iteName) {
        this.iteName = iteName;
    }

    public String getItePrice() {
        return itePrice;
    }

    public void setItePrice(String itePrice) {
        this.itePrice = itePrice;
    }

    public String getIteQnt() {
        return iteQnt;
    }

    public void setIteQnt(String iteQnt) {
        this.iteQnt = iteQnt;
    }
}
