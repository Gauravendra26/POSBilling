package de.syss.MifareClassicTool;


import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionHelper {
    Connection con;
    String uname,pass,ip,port,database;
    @SuppressLint("NewApi")
    public Connection connectionclass()
    {
        ip="B2CSERVER\\IMMORTAL";
        database="DSOI";
        uname="sa";
        pass="Dsoi@123*";
        port="1433";

        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection=null;
        String connectionURL=null;

        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";databaseName=" + database + ";user=" + uname + ";password=" + pass + ";";
            connection=DriverManager.getConnection(connectionURL);

        }
        catch (Exception ex){
            Log.e("error",ex.getMessage());

        }
        return connection;

    }
}
