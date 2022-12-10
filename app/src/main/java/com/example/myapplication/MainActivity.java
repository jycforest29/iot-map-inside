package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 10;
    private TextView text;
    private double x;
    private double y;
    BluetoothAdapter bluetoothAdapter;

    private final static int roomWidth = 782;
    private final static int roomHeight = 1259;
    private final static int originX = -65;
    private final static int originY = 245;

    private int initialBotMargin;
    private int initialRightMargin;
    private int curX;
    private int curY;

    private int state = 0;
    private int rssi1 = 0;
    private int rssi2 = 0;

    private int imuX;
    private int imuY;
    private AccelPositioning AccelPositioning;

    Handler accelHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg)
        {
            imuX = (int)AccelPositioning.pos[0];
            imuY = (int)AccelPositioning.pos[1];

            setPos(imuX, imuY);
            Log.d("IMU update", imuX + ", " + imuY);
        }
    };

    ImageView room;
    ImageView locationPointer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        room = findViewById(R.id.room);
        locationPointer = findViewById(R.id.location_pointer);

        setInitialPos();

        // Create a BroadcastReceiver for ACTION_FOUND.
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address

                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("무지성 로그", "무지성 로그");
                        Log.d("기기주소", device.getAddress());
                        int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                        Log.d("rssi", String.valueOf(rssi));
                        setConnection(rssi, 80);
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


        AccelPositioning = new AccelPositioning(0, 0);
        AccelPositioning.handler = accelHandler;

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(AccelPositioning, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(AccelPositioning, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(AccelPositioning, sensor, SensorManager.SENSOR_DELAY_NORMAL);


        // 반복하기 위해 timer 생성
        Timer timer = new Timer();

        TimerTask TT = new TimerTask() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
        };

        timer.schedule(TT, 1000, 1000);


    }

    @Override
    protected void onResume(){
        super.onResume();
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_BACKGROUND_LOCATION",
                "android.permission.BLUETOOTH_ADMIN", "android.permission.BLUETOOTH_CONNECT",
                "android.permission.BLUETOOTH_SCAN"},1);
    }

    private void setConnection(int rssi, int t){
        Rssi rssiValue = new Rssi(rssi, t);
        Call<Coordinate> call = RetrofitClient.getInstance().getConnectionObj().getCoordinate(rssiValue);

        call.enqueue(new Callback<Coordinate>() {
            @Override
            public void onResponse(Call<Coordinate> call, Response<Coordinate> response) {
                Log.d("check:", String.valueOf(response.code()));
                if (response.isSuccessful()) {
                    x = response.body().getX();
                    y = response.body().getY();
                    setPos((int)x, (int)y);
                    // update IMU
                    if (AccelPositioning != null)
                        AccelPositioning.PositionUpdate((int)x, (int)y);
                } else {
                    Toast.makeText(getApplicationContext(), "response 에러", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Coordinate> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "연결이 안됨", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setInitialPos() {
        ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) locationPointer.getLayoutParams();

        Resources res = getResources();
        newLayoutParams.bottomMargin = (int)(res.getDimension(R.dimen.room_height) * ((float)originY / roomHeight));
        newLayoutParams.rightMargin = -(int)(res.getDimension(R.dimen.room_width) * ((float)originX / roomWidth));
        initialBotMargin = newLayoutParams.bottomMargin;
        initialRightMargin = newLayoutParams.rightMargin;
        locationPointer.setLayoutParams(newLayoutParams);
        curX = 0;
        curY = 0;
    }

    private void setPos(int x, int y) {
        ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) locationPointer.getLayoutParams();
        Resources res = getResources();
        newLayoutParams.bottomMargin = initialBotMargin + (int)(res.getDimension(R.dimen.room_height) * ((float)y / roomHeight));
        newLayoutParams.rightMargin = initialRightMargin - (int)(res.getDimension(R.dimen.room_width) * ((float)x / roomWidth));
        locationPointer.setLayoutParams(newLayoutParams);
    }


    private void findDevice() {
        // Create a BroadcastReceiver for ACTION_FOUND.
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceHardwareAddress = device.getAddress(); // MAC address

                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        if (device.getAddress().equals("A8:79:8D:8F:11:FD")) {
                            int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                            Log.d("test1", String.valueOf(rssi));
                            //bluetooth1
                            if(state == 0){
                                Log.d("무지성 로그, 1", "state 0 -> 1");
                                state = 1;
                                //int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                                rssi1 = rssi;
                            }
                            if(state == 2){
                                Log.d("무지성 로그 1", "완성");
                                state = 0;
                                //int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                                rssi1 = rssi;
                                setConnection(rssi1, rssi2);
                            }
                        }
                        if ((device.getAddress().equals("5C:CB:99:AA:4D:36"))) {
                            int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                            Log.d("test2", String.valueOf(rssi));
                            if(state == 0){
                                Log.d("무지성 로그, 2", "state 0 -> 2");
                                state = 2;
                                //int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                                rssi2 = rssi;
                            }
                            if(state == 1){
                                Log.d("무지성 로그 2", "완성");
                                state = 0;
                                //int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                                rssi2 = rssi;
                                setConnection(rssi1, rssi2);
                            }
                        }
                        else {
                        }
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter("android.bluetooth.device.action.FOUND");
        registerReceiver(receiver, filter);

        if (this.bluetoothAdapter == null) {
            Toast.makeText(this, "기기 자체가 지원을 안함", Toast.LENGTH_LONG).show();
        } else if (!this.bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "안켜짐", Toast.LENGTH_LONG).show();
        }

    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            findDevice();
            //            setPos(-300, 300);
        }
    };
}