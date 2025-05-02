package de.syss.MifareClassicTool.Room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Bill.class}, version = 1)
public abstract class BillDatabase extends RoomDatabase {


    public abstract BillDao BillDao();

}
