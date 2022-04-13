package com.omega.serialbt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private BluetoothManager btManager;
    private TextView devicesText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btManager = BluetoothManager.getInstance();
        devicesText = findViewById(R.id.main_devices);

        if(btManager == null) {
            Toast.makeText(MainActivity.this, "BT not supported", Toast.LENGTH_SHORT).show();
            finish();
        }
        ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
        Collection<BluetoothDevice> pairedDevices = btManager.getPairedDevicesList();
        for (BluetoothDevice device : pairedDevices) {
            Log.d("My Bluetooth App", "Device name: " + device.getName());
            Log.d("My Bluetooth App", "Device MAC Address: " + device.getAddress());
            devicesText.append(device.getName() + " : " + device.getAddress());
            devices.add(device);
        }


        Runnable testRunnable = new Runnable() {
            @Override
            public void run() {
                /*if(devices.size() == 1)
                {
                    connectDevice(devices.get(0).getAddress());
                }*/
                connectDevice("30:AE:A4:E9:F5:8E");
            }
        };

        Thread testThread = new Thread(testRunnable);
        testThread.start();
    }

    private SimpleBluetoothDeviceInterface deviceInterface;

    private void connectDevice(String mac) {
        btManager.openSerialDevice(mac).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::onConnected, this::onError);
    }

    private void onConnected(BluetoothSerialDevice connectedDevice) {
        // You are now connected to this device!
        // Here you may want to retain an instance to your device:
        deviceInterface = connectedDevice.toSimpleDeviceInterface();

        // Listen to bluetooth events
        deviceInterface.setListeners(this::onMessageReceived, this::onMessageSent, this::onError);

        // Let's send a message:
        deviceInterface.sendMessage("Hello world!");
        Toast.makeText(MainActivity.this, "Connexion OK", Toast.LENGTH_SHORT).show();
    }

    private void onMessageSent(String message) {
        // We sent a message! Handle it here.
        Toast.makeText(MainActivity.this, "Sent a message! Message was: " + message, Toast.LENGTH_LONG).show(); // Replace context with your context instance.
    }

    private void onMessageReceived(String message) {
        // We received a message! Handle it here.
        Toast.makeText(MainActivity.this, "Received a message! Message was: " + message, Toast.LENGTH_LONG).show(); // Replace context with your context instance.
    }

    private void onError(Throwable error) {
        // Handle the error
        Toast.makeText(MainActivity.this, "ERREUR !!!", Toast.LENGTH_SHORT).show();
    }

}