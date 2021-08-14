package com.example.mqtt_sender;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    MQTTHelper mqttHelper;
    Button btn;
    String slat;
    String slong;
    Integer counter = 0;
    Boolean btnStatus = true, changed = true;
    Integer countSendLocation = 10;
//    private GPSTracker gpsTracker;
    NotificationManager notificationManager;
    String CHANNEL_ID;
    private LocationManager mLocationManager;
    private TextView latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn= findViewById(R.id.btn);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);

        startMQTT();

        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        CHANNEL_ID = createNotificationChannel(notificationManager);

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                double min = 38.1;
                double max = 38.2;
                Random r = new Random();
                double random1 = min + r.nextDouble() * (max - min);

                min = -121.7;
                max = -121.6;
                r = new Random();
                double random2 = min + r.nextDouble() * (max - min);

                JSONObject jsonString = new JSONObject();
                try {
                    jsonString.put("lon", String.valueOf(random2));
                    jsonString.put("lat", String.valueOf(random1));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String data = jsonString.toString();

                sendDataMQTT("gps-location", data);
//                setLocation();
            }
        },500,countSendLocation*1000);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changed = false;
                if (btnStatus == true) {
                    sendDataMQTT("buttonsignal", "0");
                    btn.setText("OFF");
                    btn.setBackgroundColor(Color.RED);
                } else {
                    sendDataMQTT("buttonsignal", "1");
                    btn.setText("ON");
                    btn.setBackgroundColor(Color.GREEN);
                }
                btnStatus = !btnStatus;
            }
        });

//        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        try {
//            if (ActivityCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//    //            return;
//            }
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
    }

    public void setLocation(){
        try {
            Context mContext = MainActivity.this;

            LocationManager locationManager;
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            Boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Location location = null;

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    //check the network permission
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);

                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location != null) {
                            slat = location.getLatitude() + "";
                            slong = location.getLongitude() + "";
                        }
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        //check the network permission
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                        }
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);

                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (location != null) {
                                slat = location.getLatitude() + "";
                                slong = location.getLongitude() + "";
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("location", slat + slong);
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
//            String slat = location.getLatitude()+ "";
//            String slong = location.getLongitude()+ "";
//            latitude.setText(slat);
//            longitude.setText(slong);
        }
    };

    // Creates and displays a notification
    private void addNotification(int counter, NotificationManager notificationManager, String CHANNEL_ID, String content) {
        int NOTIFICATION_ID = counter;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_vol_type_speaker_dark)
//                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("GPS Location") // NOTI TITLE
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true);

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    // Create a notification channel
    private String createNotificationChannel(NotificationManager notificationManager) {
        String CHANNEL_ID = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CHANNEL_ID = "LQ_channel_01";
            CharSequence name = "LQ_channel";
            String Description = "This is LQ's channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }
        return CHANNEL_ID;
    }

    private void updateButton(String mess) {
        Log.d("button", mess);
        if (mess.equals("1")) {
            btnStatus = true;
            btn.setText("ON");
            btn.setBackgroundColor(Color.GREEN);
            counter += 1;
            String content = "Button pressed ON";
            addNotification(counter, notificationManager,  CHANNEL_ID, content);
        } else if (mess.equals("0")) {
            btnStatus = false;
            btn.setText("OFF");
            btn.setBackgroundColor(Color.RED);
            counter += 1;
            String content = "Button pressed OFF";
            addNotification(counter, notificationManager,  CHANNEL_ID, content);
        }
    }

    private void sendDataMQTT(String feed, String data){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(true);

        byte[] b = data.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        Log.d("quyen-ABC", "Publish: " + msg);
        String topic = mqttHelper.feedName + feed;
        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        } catch (MqttException e) {

        }
    }

    private void startMQTT () {
        mqttHelper  = new MQTTHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                String mess = mqttMessage.toString();
                Log.d("quyen-Arrive", mess + "-" + topic);

                if (topic.equals(mqttHelper.feedName + "buttonsignal")) {
                    if (changed == true) {
                        updateButton(mess);
                    }
                    changed = true;
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
}