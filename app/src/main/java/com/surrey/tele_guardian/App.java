package com.surrey.tele_guardian;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.Executor;


public class App extends Application {
    private FusedLocationProviderClient client;
    private final static String TAG = "App";
    public static final String CHANNEL_HIGH_ID = "HighChannel";
    public static final String CHANNEL_LOW_ID = "LowChannel";
    public String mDeviceAddress = null;
    public BluetoothLeService mBluetoothLeService = null;
    public MainActivity mainActivity = null;
    public ArrayList<JSONObject> contacts = new ArrayList<>();
    private NotificationManagerCompat notificationManager;
    SharedPreferences prefs ;
    public void mainActivityOpen() {
        if (mBluetoothLeService != null) {
            String s = "O";
            byte[] bytes = s.getBytes(Charset.defaultCharset());
            mBluetoothLeService.writeCharacteristic(bytes[0]);
        }

    }

    public void mainActivityClosed() {
        if (mBluetoothLeService != null) {
            String s = "C";
            byte[] bytes = s.getBytes(Charset.defaultCharset());
            mBluetoothLeService.writeCharacteristic(bytes[0]);
        }
    }

    public void turnOnLED() {
        if (mBluetoothLeService != null) {
            String s = "X";
            byte[] bytes = s.getBytes(Charset.defaultCharset());
            mBluetoothLeService.writeCharacteristic(bytes[0]);
        }
    }

    public void turnOffLED() {
        if (mBluetoothLeService != null) {
            String s = "S";
            byte[] bytes = s.getBytes(Charset.defaultCharset());
            mBluetoothLeService.writeCharacteristic(bytes[0]);
        }
    }

    public boolean getBatteryLevel() {
        if (mBluetoothLeService != null) {
            String s = "B";
            byte[] bytes = s.getBytes(Charset.defaultCharset());
            return mBluetoothLeService.writeCharacteristic(bytes[0]);
        }
        return false;
    }

    public BluetoothLeService getBluetoothLeService() {
        return mBluetoothLeService;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setBluetoothLeService(BluetoothLeService bluetoothLeService) {
        this.mBluetoothLeService = bluetoothLeService;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void getDeviceCharacteristic() {
        BluetoothGattCharacteristic charac = mBluetoothLeService.getCharacteristic();
        mBluetoothLeService.setCharacteristicNotification(charac, true);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.this);
        String longandlat = sharedPreferences.getString("location","");
        contacts = getArrayList("contacts");

        Log.i("location",longandlat);
        createNotificationChannels();
        notificationManager = NotificationManagerCompat.from(this);
        IntentFilter filter = new IntentFilter("com.example.bluetooth.le.ACTION_DATA_AVAILABLE");
        registerReceiver(mBroadcastReceiver, filter);
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channelHigh = new NotificationChannel(
                    CHANNEL_HIGH_ID,
                    "High Importance Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channelHigh.setDescription("This is the low importance notifications channel");

            NotificationChannel channelLow = new NotificationChannel(
                    CHANNEL_LOW_ID,
                    "Low Importance Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            channelLow.setDescription("This is the low importance notifications channel");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channelHigh);
            manager.createNotificationChannel(channelLow);
        }
    }

    public void sendOnHighChannel() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_HIGH_ID)
                .setSmallIcon(R.drawable.ic_priority_high_black_24dp)
                .setContentTitle("HIGH")
                .setContentText("High Importance Message")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
    }

    public void sendOnLowChannel() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_LOW_ID)
                .setSmallIcon(R.drawable.ic_low_priority_black_24dp)
                .setContentTitle("LOW")
                .setContentText("Low Importance Message")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        notificationManager.notify(2, notification);
    }
    public ArrayList<JSONObject> getArrayList(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<JSONObject>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String incomingString = intent.getStringExtra("com.example.bluetooth.le.EXTRA_DATA");
            Log.d(TAG, "Incoming String = " + incomingString);
            char incomingSignal = incomingString.charAt(0);
            switch(incomingSignal){
                case 'B':
                    Log.d(TAG, "Incoming battery percent.");
                    break;
                case 'F':
                    Log.d(TAG, "Fall detected.");
                    break;
                case 'P':
                    Log.d(TAG, "Panic button pressed.");

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.this);
                    String longandlat = sharedPreferences.getString("location","");
                    String locationTXT = "I have not responded to the panic button, i was recently at " + longandlat;
                    Log.i("location",locationTXT);
                    if (contacts.size() != 0){
                        for (int i = 0; i < contacts.size(); i++){
                            final Object object = contacts.get(i);
                            String json = object.toString();
                            final JSONObject obj;
                            try {
                                obj = new JSONObject(json);
                                final String number = obj.get("NUMBER").toString();
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(number, null,  locationTXT, null, null);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }}
                    break;
                case 'D':
                    Log.d(TAG, "Dismiss button pressed.");
                    break;
                case 'U':
                    Log.d(TAG, "Unusual activity detected.");
                    break;
                case 'L':
                    Log.d(TAG, "Low Battery warning (20%).");
                    break;
                case 'V':
                    Log.d(TAG, "Very low Battery warning (10%).");
                    break;
                case 'E':
                    Log.d(TAG, "Extremely low Battery warning (5%).");
                    break;
                default:
                    break;
            }
        }

    };
}
