package com.example.abc.newfickrbrowser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class SplashActivity extends AppCompatActivity {

    LinearLayout mSplashLinearLayout;
    private static int SPLASH_TIME_OUT = 3500;
    private static int ANIMATION_FINISH = 1000;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mSplashLinearLayout = findViewById(R.id.launcher_logo_text);
        startAnimation();
        startLoginActivity();
    }

    private void startLoginActivity() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable()
        { @Override
        public void run()
        {
            Intent mLoginIntent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(mLoginIntent);
            overridePendingTransition(R.anim.anim_enter, R.anim.anim_out);
            finish();
        }

        },SPLASH_TIME_OUT) ;
    }

    private void startAnimation() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable()
        { @Override
        public void run()
        {
            Animation mStartAnim = AnimationUtils.loadAnimation(getApplication(),R.anim.anim_move_sac_logo_text);
            mSplashLinearLayout.startAnimation(mStartAnim);
        }
        },ANIMATION_FINISH) ;
    }
}