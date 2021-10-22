package it.bonny.app.wisetimer2.manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import it.bonny.app.wisetimer2.R;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash_screen);
            ImageView imageView=findViewById(R.id.idLogo);
            TextView textView = findViewById(R.id.textLogo);
            Animation zoomIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
            Animation move = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);
            textView.setAnimation(move);
            zoomIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            imageView.startAnimation(zoomIn);
        }catch (Exception e){
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
}
