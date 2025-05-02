package de.syss.MifareClassicTool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import de.syss.MifareClassicTool.Activities.MainMenu;

public class Splash_Activity extends AppCompatActivity {
    Handler handler;
    Animation topanimantion,bottomanimation,middleanimation,center;
    CardView cardLogo;
    TextView tvLogoLower,tvLogoMiddle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Utils.blackIconStatusBar( this, R.color.random);
        init();

        topanimantion = AnimationUtils.loadAnimation(this, R.anim.topanimantion);
        middleanimation = AnimationUtils.loadAnimation(this, R.anim.middleanimation);
        bottomanimation = AnimationUtils.loadAnimation(this, R.anim.bottomanimation);
        center = AnimationUtils.loadAnimation(this, R.anim.center_out);


        cardLogo.setAnimation(topanimantion);
        tvLogoMiddle.setAnimation(center);
        tvLogoLower.setAnimation(bottomanimation);

         handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                Intent intent = new Intent( Splash_Activity.this, Select_Location_Activity.class);
                startActivity(intent);
                finish();


            }
        }, 3000);
    }

    void init(){
        cardLogo = findViewById(R.id.cardLogo);
        tvLogoLower = findViewById(R.id.tvLogoLower);
        tvLogoMiddle = findViewById(R.id.tvLogoMiddle);

    }

}
