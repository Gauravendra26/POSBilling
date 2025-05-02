package de.syss.MifareClassicTool;

import android.content.Context;


import android.annotation.SuppressLint;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conclass {
    private Context context;
    private String ip, database, uname, pass, port;

    public conclass(Context context) {
        this.context = context;
    }

    @SuppressLint("NewApi")
    public Connection connectionclass() {
        ip = "192.168.1.6\\immortal";
        pass = "Dsoi@123*";
        database = "DSOI";
        uname = "sa";
        port = "1433";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionURL = null;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";databaseName=" + database + ";user=" + uname + ";password=" + pass + ";";
            Log.e("ConnectionURL", connectionURL);

            connection = DriverManager.getConnection(connectionURL);

        } catch (SQLException se) {
            Log.e("Error SQL", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("Error ClassNotFound", e.getMessage());
        } catch (Exception ex) {
            Log.e("Error Exception", ex.getMessage());
        }
        return connection;
    }
}


