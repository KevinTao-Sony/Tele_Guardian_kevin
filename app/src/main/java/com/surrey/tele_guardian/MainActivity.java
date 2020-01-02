package com.surrey.tele_guardian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.animation.Animator;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView textView;

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
                        Intent intent = new Intent(MainActivity.this, SettingsPage.class);
                        startActivity(intent);
                    }
                }
        );

        textView = findViewById(R.id.batterylevel_text);
        progressBar =  findViewById(R.id.batterylevel);
        battery(50);

        startAnimation();
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

    @Override
    protected void onStart() {
        super.onStart();
        ((App) getApplication()).mainActivityOpen();
        boolean status = ((App) getApplication()).getBatteryLevel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ((App) getApplication()).mainActivityClosed();
    }

    public void battery(int progress){
        int batteryLevel = (progress * 75) / 10;
        progressBar.setProgress(batteryLevel);
        textView.setText("" + progress + "%");
    }
}