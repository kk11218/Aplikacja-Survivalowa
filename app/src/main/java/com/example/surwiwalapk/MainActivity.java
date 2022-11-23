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
import android.view.WindowManager;
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

    private TextView textView;// kompass

    private ImageView imageView;// kompass

    private SensorManager sensorManager;// kompass
    private Sensor accelerometerSensor, magnetometerSensor;// kompass

    private float [] lastAccelerometer = new float[3];// kompass
    private float [] lastMagnetometer = new float[3];// kompass
    private float [] rotationMatrix = new float[9];// kompass
    private float [] orientation = new float[3];// kompass

    boolean isLastAccelerometerArrayCopied = false;// kompass
    boolean isLastMagnetometerArrayCopied = false;// kompass
    long lastUpdatedTime = 0;// kompass
    float currentDegree = 0f;// kompass

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// kompass

        textView = findViewById(R.id.textView);// kompass

        imageView = findViewById(R.id.imageView);// kompass

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);// kompass
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// kompass
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);// kompass


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

        if(sensorEvent.sensor == accelerometerSensor) {// kompass
            System.arraycopy(sensorEvent.values, 0, lastAccelerometer, 0, sensorEvent.values.length);// kompass
            isLastAccelerometerArrayCopied = true;// kompass
        }else if(sensorEvent.sensor == magnetometerSensor) {// kompass
            System.arraycopy(sensorEvent.values, 0,lastMagnetometer, 0,sensorEvent.values.length);// kompass
            isLastMagnetometerArrayCopied = true;// kompass
        }

        if(isLastAccelerometerArrayCopied && isLastMagnetometerArrayCopied && System.currentTimeMillis() - lastUpdatedTime>250) {// kompass
            SensorManager.getRotationMatrix(rotationMatrix, null,lastAccelerometer,lastMagnetometer);// kompass
            SensorManager.getOrientation(rotationMatrix, orientation);

            float azimuthInRadians = orientation[0];// kompass
            float azimuthInDegree = (float) Math.toDegrees(azimuthInRadians);// kompass

            RotateAnimation rotateAnimation =// kompass
                    new RotateAnimation(currentDegree, -azimuthInDegree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);// kompass
            rotateAnimation.setDuration(250);// kompass
            rotateAnimation.setFillAfter(true);// kompass
            imageView.startAnimation(rotateAnimation);// kompass

            currentDegree = -azimuthInDegree;// kompass
            lastUpdatedTime = System.currentTimeMillis();// kompass

            int x = (int) azimuthInDegree;// kompass
            textView.setText(x +"°");// kompass
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);// kompass
        sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL);// kompass
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this, accelerometerSensor);// kompass
        sensorManager.unregisterListener(this, magnetometerSensor);// kompass
    }
}