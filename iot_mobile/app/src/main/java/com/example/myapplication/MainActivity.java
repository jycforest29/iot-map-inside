package com.example.myapplication;

import static android.os.SystemClock.sleep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 10;
    private TextView text;
    BluetoothAdapter bluetoothAdapter;
    private View button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.text);

        //receiver 등록

        // Create a BroadcastReceiver for ACTION_FOUND.
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    text.setText("hi");

                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("무지성 로그", "무지성 로그");
                        Log.d("기기이름", device.getName());
                        Log.d("기기주소", device.getAddress());
                        //Log.d("rssi", device.);
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter("android.bluetooth.device.action.FOUND");
        registerReceiver(receiver, filter);

        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (this.bluetoothAdapter == null) {
            Toast.makeText(this, "기기 자체가 지원을 안함", Toast.LENGTH_LONG).show();
        } else if (!this.bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "안켜짐", Toast.LENGTH_LONG).show();
        }
        // 여기까지돼요 text.setText("startdiscovery?");
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {

            if (this.bluetoothAdapter.isDiscovering()) {
                this.bluetoothAdapter.cancelDiscovery();
            }
            this.bluetoothAdapter.startDiscovery();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_BACKGROUND_LOCATION",
                "android.permission.BLUETOOTH_ADMIN", "android.permission.BLUETOOTH_CONNECT",
                "android.permission.BLUETOOTH_SCAN"},1);
    }
}