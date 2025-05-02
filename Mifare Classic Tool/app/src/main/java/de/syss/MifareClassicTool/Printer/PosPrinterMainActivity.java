package de.syss.MifareClassicTool.Printer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;

import com.zcs.sdk.DriverManager;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.Sys;

import de.syss.MifareClassicTool.R;

public class PosPrinterMainActivity extends AppCompatActivity {

    private androidx.appcompat.app.ActionBar actionBar; // Change to AppCompatActivity

    private com.zcs.sdk.DriverManager mDriverManager;
    private Sys mSys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos_printer_main);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.demo_title));
        }
        Fragment fragment = new SettingsFragment();
        if (savedInstanceState == null)
            getFragmentManager().beginTransaction().add(R.id.frame_container, fragment).commit();

        mDriverManager = DriverManager.getInstance();
        mSys = mDriverManager.getBaseSysDevice();
        initSdk();
    }

    private void initSdk() {
        int status = mSys.sdkInit();
        if(status != SdkResult.SDK_OK) {
            mSys.sysPowerOn();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        status = mSys.sdkInit();
        if(status != SdkResult.SDK_OK) {
            //init failed.
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getFragmentManager().getBackStackEntryCount() <= 1) {
            if (actionBar != null) {
                actionBar.setTitle(getString(R.string.demo_title));
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
