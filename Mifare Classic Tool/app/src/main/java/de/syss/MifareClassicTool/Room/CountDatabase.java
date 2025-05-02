package de.syss.MifareClassicTool.Room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Count.class}, version = 1)
public abstract class CountDatabase extends RoomDatabase {
    public abstract CountDao CountDao();
}

