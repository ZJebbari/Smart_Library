package zyh.smartlibrary.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import zyh.smartlibrary.R;

public class Splash_Screen extends AppCompatActivity {

    TextView title,text;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        title = findViewById(R.id.textViewSplashTitle);
        text = findViewById(R.id.textViewSplashText);
        image = findViewById(R.id.imageViewSplash);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.library_anim);
        Animation animationImage = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.image_anim);
        title.startAnimation(animation);
        text.startAnimation(animation);
        image.setAnimation(animationImage );


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(Splash_Screen.this, MainActivity.class);
                startActivity(i);
                finish();

            }
        },5000);
    }
}