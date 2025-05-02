package de.syss.MifareClassicTool;

public class Model_Waiter {

    String id,displayAs;

    public Model_Waiter(String id, String displayAs) {
        this.id = id;
        this.displayAs = displayAs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayAs() {
        return displayAs;
    }

    public void setDisplayAs(String displayAs) {
        this.displayAs = displayAs;
    }
}
