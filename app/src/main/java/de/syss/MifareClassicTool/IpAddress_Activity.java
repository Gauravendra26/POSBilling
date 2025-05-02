package de.syss.MifareClassicTool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import de.syss.MifareClassicTool.Activities.MainMenu;

public class IpAddress_Activity extends AppCompatActivity {

    EditText etIPAddress,etPassword;
    RelativeLayout rlMainpage;
    String ipAddress,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_address);
        Utils.blackIconStatusBar(this, R.color.white);

        init();


        rlMainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areEditTextsEmpty()) {
                    // Both EditTexts are empty
                    Toast.makeText(getApplicationContext(), "Please enter IP Address and Password  ", Toast.LENGTH_SHORT).show();
                } else if (etIPAddress.getText().toString().trim().isEmpty()) {
                    // IP Address EditText is empty
                    Toast.makeText(getApplicationContext(), "Please enter IP Address", Toast.LENGTH_SHORT).show();
                } else if (etPassword.getText().toString().trim().isEmpty()) {
                    // Password EditText is empty
                    Toast.makeText(getApplicationContext(), "Please enter Password", Toast.LENGTH_SHORT).show();
                } else {
                    ipAddress = etIPAddress.getText().toString().trim();
                    password = etPassword.getText().toString().trim();

                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("IPAddress",
                        MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("ipAddress",ipAddress);
                    myEdit.putString("password",password);

                    myEdit.apply();
                    myEdit.commit();
                    Intent intent=new Intent(getApplicationContext(), MainMenu.class);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }
    void  init(){
        etIPAddress=findViewById(R.id.etIPAddress);
        etPassword=findViewById(R.id.etPassword);
        rlMainpage=findViewById(R.id.rlMainpage);

    }

    private boolean areEditTextsEmpty() {
        return etIPAddress.getText().toString().trim().isEmpty() &&
            etPassword.getText().toString().trim().isEmpty();
    }

}
