package de.syss.MifareClassicTool.Room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Count {

    @PrimaryKey(autoGenerate = true)
    public int aid;

    @ColumnInfo(name = "kqnt")
    public String kqnt;

    @ColumnInfo(name = "bqnt")
    public int bqnt;

    @ColumnInfo(name = "tid")
    public int tid;

    @ColumnInfo(name = "locid")
    public int locid;

    public Count(int aid, String kqnt, int bqnt, int tid, int locid) {
        this.aid = aid;
        this.kqnt = kqnt;
        this.bqnt = bqnt;
        this.tid = tid;
        this.locid = locid;
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

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getKqnt() {
        return kqnt;
    }

    public void setKqnt(String kqnt) {
        this.kqnt = kqnt;
    }

    public int getBqnt() {
        return bqnt;
    }

    public void setBqnt(int bqnt) {
        this.bqnt = bqnt;
    }
}

