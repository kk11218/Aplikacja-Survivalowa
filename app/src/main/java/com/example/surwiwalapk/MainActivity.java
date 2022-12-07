package com.example.surwiwalapk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private ImageButton toggleButton;//latarka
    private ImageButton toggleButton2;//nowa strona tajmer

    Switch switcher;//tryb ciemny
    boolean nightMODE;//tryb ciemny
    SharedPreferences sharedPreferences;//tryb ciemny
    SharedPreferences.Editor editor;//tryb ciemny

    boolean hasCameraFlash = false;//latarka
    boolean flashOn = false;//latarka

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
        //---------------------------------------------------------------------------------------------------
    toggleButton2 = findViewById(R.id.imageButton2);//nowa strona tajmer
    toggleButton2.setOnClickListener(new View.OnClickListener() {//nowa strona tajmer
        @Override//nowa strona tajmer
        public void onClick(View view) {//nowa strona tajmer
            openTajmer();//nowa strona tajmer
        }
    });
        //---------------------------------------------------------------------------------------------------
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// kompass
//---------------------------------------------------------------------------------------------------
        getSupportActionBar().hide();//tryb ciemny

        switcher = findViewById(R.id.switcher);//tryb ciemny

        sharedPreferences = getSharedPreferences("MODE",Context.MODE_PRIVATE);//tryb ciemny
        nightMODE = sharedPreferences.getBoolean("night", false);//tryb ciemny

        if(nightMODE){//tryb ciemny
            switcher.setChecked(true);//tryb ciemny
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);//tryb ciemny
        }

switcher.setOnClickListener(new View.OnClickListener() {//tryb ciemny
    @Override
    public void onClick(View view) {//tryb ciemny
        if(nightMODE){//tryb ciemny
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);//tryb ciemny
            editor = sharedPreferences.edit();//tryb ciemny
            editor.putBoolean("night", false);//tryb ciemny
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);//tryb ciemny
            editor = sharedPreferences.edit();//tryb ciemny
            editor.putBoolean("night", true);//tryb ciemny
        }
        editor.apply();//tryb ciemny

    }
});


//---------------------------------------------------------------------------------------------------
        textView = findViewById(R.id.textView);// kompass

        imageView = findViewById(R.id.imageView);// kompass

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);// kompass
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);// kompass
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);// kompass

//---------------------------------------------------------------------------------------------------
        toggleButton = findViewById(R.id.imageButton);//latarka

        hasCameraFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);//latarka

        toggleButton.setOnClickListener(new View.OnClickListener() {//latarka
            @Override
            public void onClick(View view) {



              if (hasCameraFlash){//latarka
                  if (flashOn){//latarka
                      flashOn = false;//latarka
                      toggleButton.setImageResource(R.drawable.off);//latarka
                      try {
                          flashLightOff();//latarka
                      } catch (CameraAccessException e) {//latarka
                          e.printStackTrace();//latarka
                      }
                  }
                  else{//latarka
                      flashOn = true;//latarka
                      toggleButton.setImageResource(R.drawable.on);//latarka
                      try {//latarka
                          flashLightOn();//latarka
                      } catch (CameraAccessException e) {//latarka
                          e.printStackTrace();//latarka
                      }
                  }
              }
              else{//latarka
                  Toast.makeText(MainActivity.this, "No flash available on your device", Toast.LENGTH_LONG).show();//latarka
              }
            }
        });
    }
    //---------------------------------------------------------------------------------------------------
    public void openTajmer()
    {
        Intent intent = new Intent(this, Tajmer.class);//nowa strona tajmer
        startActivity(intent);//nowa strona tajmer
    }

    private void flashLightOn() throws CameraAccessException {//latarka
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);//latarka
        assert cameraManager != null;//latarka
        String cameraId = cameraManager.getCameraIdList()[0];//latarka
        cameraManager.setTorchMode(cameraId, true);//latarka
        Toast.makeText(MainActivity.this, "FlashLight is ON", Toast.LENGTH_SHORT).show();//latarka
    }

    private void flashLightOff() throws CameraAccessException {//latarka
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);//latarka
        assert cameraManager != null;//latarka
        String cameraId = cameraManager.getCameraIdList()[0];//latarka
        cameraManager.setTorchMode(cameraId, false);//latarka
        Toast.makeText(MainActivity.this, "FlashLight is OFF", Toast.LENGTH_SHORT).show();//latarka
    }
    //---------------------------------------------------------------------------------------------------
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