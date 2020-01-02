package com.surrey.tele_guardian;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsPage extends AppCompatActivity {
    private final static String TAG = "SettingsPage";
    public String mDeviceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);
        Button pair_device = findViewById(R.id.pair_device);
        pair_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Pair Device.");
                Intent intent = new Intent(SettingsPage.this, PairDevice.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                mDeviceAddress = getIntent().getStringExtra("Address");
                Log.d(TAG, "Device Address = " + mDeviceAddress);
            }
        }
    }
}