//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.example.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private BluetoothAdapter bluetoothAdapter;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d("onReceive 호출됨", "onReceive 호출됨");
            String action = intent.getAction();
            if (action.equals("android.bluetooth.device.action.FOUND")) {
                BluetoothDevice device = (BluetoothDevice)intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                if (ActivityCompat.checkSelfPermission(MainActivity.this, "android.permission.BLUETOOTH_CONNECT") != 0) {
                    return;
                }

                Log.d("기기이름", device.getName());
                Log.d("기기주소", device.getAddress());
                Log.d("rssi", "android.bluetooth.device.extra.RSSI");
            }

        }
    };

    public MainActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(2131427356);
        IntentFilter filter = new IntentFilter("android.bluetooth.device.action.FOUND");
        this.registerReceiver(this.broadcastReceiver, filter);
        this.button = (Button)this.findViewById(2131230818);
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_BACKGROUND_LOCATION", "android.permission.BLUETOOTH_ADMIN", "android.permission.BLUETOOTH_CONNECT", "android.permission.BLUETOOTH_SCAN"}, 1);
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.bluetoothAdapter == null) {
            Toast.makeText(this, "기기 자체가 지원을 안함", 0);
            this.finish();
        } else if (!this.bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "안켜짐", 0);
            this.finish();
        }

        if (ActivityCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_SCAN") == 0) {
            this.button.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, "android.permission.BLUETOOTH_CONNECT") == 0) {
                        MainActivity.this.onClickSearch(v);
                        MainActivity.this.onDestroy();
                    }
                }
            });
        }
    }

    public void onClickSearch(View view) {
        if (ActivityCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_SCAN") == 0) {
            if (this.bluetoothAdapter.isDiscovering()) {
                this.bluetoothAdapter.cancelDiscovery();
            }

            this.bluetoothAdapter.startDiscovery();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.broadcastReceiver);
    }
}
