package com.example.myapplication;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.text.DecimalFormat;

public class AccelPositioning  implements SensorEventListener {

    public float[] gravity = null;
    public float[] magnetic = null;
    public float[] orientation = new float[3];

    public float[] pos = new float[2];
    private float[] vel = new float[2];
    private float[] acc = new float[2];

    public Handler handler;
    private DecimalFormat format = new DecimalFormat("##.00");

    private long timestamp;
    private static final float NS2S = 1.0f / 1000000000.0f;

    private Kalman[] linearKalman = null;

    float[] values = new float[3];
    float[] newAcc = new float[2];
    float[] newVel = new float[2];

    public AccelPositioning(int x, int y)
    {
        pos[0] = x;
        pos[1] = y;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = sensorEvent.values;
        }
        else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magnetic = sensorEvent.values;
        }
        else if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
        {
            float dt = (sensorEvent.timestamp - timestamp) * NS2S;
            timestamp = sensorEvent.timestamp;
            values[0] = -sensorEvent.values[0] * 100;
            values[1] = -sensorEvent.values[1] * 100;
            values[2] = -sensorEvent.values[2] * 100;

            if (linearKalman == null)
                linearKalman = new Kalman[] { new Kalman(values[0]), new Kalman(values[1]), new Kalman(values[2]) };
            else
            {
                values[0] = linearKalman[0].update(values[0]);
                values[1] = linearKalman[1].update(values[1]);
                values[2] = linearKalman[2].update(values[2]);
            }

            if (!UpdateRotation())
                return;

            values[0] = (float)(Math.cos(orientation[2]) * values[0] + Math.sin(orientation[2]) * values[2]);
            values[1] = (float)(Math.cos(orientation[1]) * values[1] + Math.sin(orientation[1]) * values[2]);

            newAcc[0] = (float)(Math.cos(orientation[0]) * values[0] + Math.sin(orientation[0]) * values[1]);
            newAcc[1] = (float)(-Math.sin(orientation[0]) * values[0] + Math.cos(orientation[0]) * values[1]);

            synchronized (this) {
                newVel[0] = vel[0] + (newAcc[0] + acc[0]) * 0.5f * dt;
                newVel[1] = vel[1] + (newAcc[1] + acc[1]) * 0.5f * dt;

                float mag = (float) Math.sqrt(newVel[0] * newVel[0] + newVel[1] * newVel[1]);
                if (mag > 70)
                {
                    newVel[0] *= 70f / mag;
                    newVel[1] *= 70f / mag;
                }

                pos[0] += (vel[0] + newVel[0]) * 0.5f * dt;
                pos[1] += (vel[1] + newVel[1]) * 0.5f * dt;

                if (pos[0] > 65)
                    pos[0] = 65;
                else if (pos[0] < -662)
                    pos[0] = -662;

                if (pos[1] > 966)
                    pos[1] = 966;
                else if (pos[1] < -245)
                    pos[1] = -245;

                acc = newAcc;
                vel = newVel;
            }
            handler.sendMessage(new Message());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }

    private boolean UpdateRotation()
    {
        if (gravity != null && magnetic != null) {
            float R[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, null, gravity, magnetic);
            if (success) {
                SensorManager.getOrientation(R, orientation);
                return true;
            }
        }
        return false;
    }

    private long lastTime;

    public void PositionUpdate(int x, int y)
    {
        synchronized (this) {
            vel[0] += 2 * (x - pos[0]) / ((timestamp - lastTime) * NS2S);
            vel[1] += 2 * (y - pos[1]) / ((timestamp - lastTime) * NS2S);

            pos[0] = x;
            pos[1] = y;
            lastTime = timestamp;
        }
    }

    public void Reset() {
        acc[0] = acc[1] = 0;
        vel[0] = vel[1] = 0;
        pos[0] = pos[1] = 0;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("\nACC: ").append(format.format(acc[0])).append(" / ").append(format.format(acc[1]));
        builder.append("\nVEL: ").append(format.format(vel[0])).append(" / ").append(format.format(vel[1]));
        builder.append("\nPOS: ").append(format.format(pos[0])).append(" / ").append(format.format(pos[1]));
        builder.append("\nROT: ").append(format.format(orientation[0] * 180 / Math.PI)).append(" / ").append(format.format(orientation[1] * 180 / Math.PI)).append(" / ").append(format.format(orientation[2] * 180 / Math.PI));
        return  builder.toString();
    }

    public class Kalman
    {
        private float Q = 0.00001f;
        private float R = 0.001f;
        private float P = 1;
        private float X = 0;
        private float K;

        public Kalman(float initValue)
        {
            X = initValue;
        }

        private void measurementUpdate()
        {
            K = (P + Q) / (P + Q + R);
            P = R * (P + Q) / (P + Q + R);
        }

        public float update(float measurement)
        {
            measurementUpdate();
            X = X + (measurement - X) * K;
            return X;
        }
    }

}