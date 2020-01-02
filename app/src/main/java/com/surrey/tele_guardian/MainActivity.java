package com.surrey.tele_guardian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import static android.view.View.VISIBLE;

public class MainActivity extends Activity {

    ImageView icon;
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        icon = findViewById(R.id.icon);
        layout = findViewById(R.id.mainmenu);

        Button account_info = findViewById(R.id.account_info);
        Button Contact_Page = findViewById(R.id.Contact_Page);
        Button settings = findViewById(R.id.settings);
        ProgressBar batter_level = findViewById(R.id.batterylevel);
        account_info.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Account.class);
                        startActivity(intent);
                    }
                }
        );
        Contact_Page.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, ContactsPage.class);
                        startActivity(intent);
                    }
                }
        );
        settings.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("settings");
                    }
                }
        );
        startAnimation();
        batter_level.setProgress(50);
    }
    private void startAnimation() {
        ViewPropertyAnimator viewPropertyAnimator = icon.animate();

        viewPropertyAnimator.y(100f);
        viewPropertyAnimator.setDuration(2000);
        viewPropertyAnimator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                layout.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }
}
