package de.syss.MifareClassicTool;

public class Model_KOTCancel {

    String kotNumber;
    int position;

    public Model_KOTCancel(String kotNumber,int position) {
        this.kotNumber = kotNumber;
        this.position = position;

    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getKotNumber() {
        return kotNumber;
    }

    public void setKotNumber(String kotNumber) {
        this.kotNumber = kotNumber;
    }
}
