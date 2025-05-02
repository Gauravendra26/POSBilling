package de.syss.MifareClassicTool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionHelper {
    private Connection con;
    private Context context;
    private String ip, database, uname, pass, port;

    public ConnectionHelper(Context context) {
        this.context = context;
    }

    @SuppressLint("NewApi")
    public Connection connectionclass() {
        ip = "192.168.1.6";
        String instanceName = "immortal";
        pass = "Dsoi@123*";
        database = "DSOI_backup";
        uname = "sa";
        port = "1433";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionURL;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + "/" + database + ";instance=" + instanceName + ";user=" + uname + ";password=" + pass + ";";
            connection = DriverManager.getConnection(connectionURL);
            Log.e("ConnectionURL", connectionURL + " " + connection);
        } catch (Exception ex) {
            Log.e("error", "Connection failed: " + ex.getMessage());
        }
        return connection;
    }
}
