package com.example.surwiwalapk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private ImageButton toggleButton;

    boolean hasCameraFlash = false;
    boolean flashOn = false;


    private TextView TextView;
    private ImageView imageView;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor, magnetometerSensor;
    private float [] lastAccelerometer = new float[3];
    private float [] lastMagnetometer = new float[3];
    private float [] rotationMatrix = new float[9];
    private float [] orientation = new float[3];

    boolean isLastAccelerometerArrayCopied = false;
    boolean isLastMagnetometerArrayCopied = false;
    long lastUpdatedTime = 0;
    float currentDegree = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggleButton = findViewById(R.id.imageButton);

        hasCameraFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if (hasCameraFlash){
                  if (flashOn){
                      flashOn = false;
                      toggleButton.setImageResource(R.drawable.off);
                      try {
                          flashLightOff();
                      } catch (CameraAccessException e) {
                          e.printStackTrace();
                      }
                  }
                  else{
                      flashOn = true;
                      toggleButton.setImageResource(R.drawable.on);
                      try {
                          flashLightOn();
                      } catch (CameraAccessException e) {
                          e.printStackTrace();
                      }
                  }
              }
              else{
                  Toast.makeText(MainActivity.this, "No flash available on your device", Toast.LENGTH_LONG).show();
              }
            }
        });
    }

    private void flashLightOn() throws CameraAccessException {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        assert cameraManager != null;
        String cameraId = cameraManager.getCameraIdList()[0];
        cameraManager.setTorchMode(cameraId, true);
        Toast.makeText(MainActivity.this, "FlashLight is ON", Toast.LENGTH_SHORT).show();
    }

    private void flashLightOff() throws CameraAccessException {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        assert cameraManager != null;
        String cameraId = cameraManager.getCameraIdList()[0];
        cameraManager.setTorchMode(cameraId, false);
        Toast.makeText(MainActivity.this, "FlashLight is OFF", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor == accelerometerSensor) {
            System.arraycopy(sensorEvent.values, 0, lastAccelerometer, 0, sensorEvent.values.length);
            isLastAccelerometerArrayCopied = true;
        }else if(sensorEvent.sensor == magnetometerSensor) {
            System.arraycopy(sensorEvent.values, 0,lastMagnetometer, 0,sensorEvent.values.length);
            isLastMagnetometerArrayCopied= true;
        }
        if(isLastAccelerometerArrayCopied && isLastMagnetometerArrayCopied && System.currentTimeMillis() - lastUpdatedTime>250) {
            SensorManager.getRotationMatrix(rotationMatrix, null,lastAccelerometer,lastMagnetometer);
            SensorManager.getOrientation(rotationMatrix, orientation);

            float azimuthInRadians= orientation[0];
            float azimuthInDegree = (float) Math.toDegrees(azimuthInRadians);

            RotateAnimation rotateAnimation =
                    new RotateAnimation(currentDegree, -azimuthInDegree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(250);
            rotateAnimation.setFillAfter(true);
            imageView.startAnimation(rotateAnimation);
            currentDegree = -azimuthInDegree;
            lastUpdatedTime= System.currentTimeMillis();

            int x = (int) azimuthInDegree;
            TextView.setText(x+"°");

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this, accelerometerSensor);
        sensorManager.unregisterListener(this, magnetometerSensor);
    }
}