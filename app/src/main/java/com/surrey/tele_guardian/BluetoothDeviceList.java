package com.surrey.tele_guardian;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BluetoothDeviceList extends ArrayAdapter<BluetoothDevice> {

    private LayoutInflater layoutInflater;
    private ArrayList<BluetoothDevice> bluetoothDevices;
    private int viewResourceID;

    public BluetoothDeviceList(Context context, int resourceID, ArrayList<BluetoothDevice> devices){
        super(context, resourceID, devices);
        this.bluetoothDevices = devices;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewResourceID = resourceID;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(viewResourceID, null);
        BluetoothDevice bluetoothDevice = bluetoothDevices.get(position);

        if (bluetoothDevice != null){
            TextView bluetoothDeviceName = (TextView) convertView.findViewById(R.id.bluetoothDeviceName);
            TextView bluetoothDeviceAddress = (TextView) convertView.findViewById(R.id.bluetoothDeviceAddress);

            if (bluetoothDeviceName != null){
                bluetoothDeviceName.setText(bluetoothDevice.getName());
            }
            if (bluetoothDeviceAddress != null){
                bluetoothDeviceAddress.setText(bluetoothDevice.getAddress());
            }
        }
        return convertView;
    }
}
