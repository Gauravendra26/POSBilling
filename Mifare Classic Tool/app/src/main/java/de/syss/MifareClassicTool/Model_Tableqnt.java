package de.syss.MifareClassicTool;

public class Model_Tableqnt {

    String tableId,location ;


    public Model_Tableqnt(String tableId,String location) {
        this.tableId=tableId;
        this.location=location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }
}
