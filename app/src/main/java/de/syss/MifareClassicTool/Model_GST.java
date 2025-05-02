package de.syss.MifareClassicTool;

public class Model_GST {


    String gst,amount,qty;

    public Model_GST( String gst, String amount, String qty) {
        this.gst = gst;

        this.amount = amount;
        this.qty = qty;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
