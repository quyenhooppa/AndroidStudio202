package com.example.mqtt_sender;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.Manifest;
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
    MQTTHelper mqttHelperSend, mqttHelperReceive;
    LocationManager mLocationManager;
    TextView latitude, longitude;
    String slat;
    String slong;
    Button btn;
    Integer counter = 0;
    Boolean btnStatus = true;
    Integer count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn= findViewById(R.id.btn);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);

        startMQTTSend();
        startMQTTReceive();

        final NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        final String CHANNEL_ID = createNotificationChannel(notificationManager);

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                double min = 37;
                double max = 37.5;
                Random r = new Random();
                double random1 = min + r.nextDouble() * (max - min);

                sendDataMQTT(slat, "latitude");

                min = -121.5;
                max = -121;
                r = new Random();
                double random2 = min + r.nextDouble() * (max - min);

                sendDataMQTT(slong, "longitude");

                count += 1;
                String data = "{\"lon\":"+slong+",\"lat\":"+slat+"}";
                sendDataMQTT(data, "gps-location");

            }
        },500,10000);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnStatus == false) {
                    sendDataMQTT("0", "ButtonSignal");
                    btn.setText("OFF");
                    btn.setBackgroundColor(Color.RED);
                    counter += 1;
                    String content = "Button pressed - ON to OFF";
                    addNotification(counter, notificationManager,  CHANNEL_ID, content);
                } else {
                    sendDataMQTT("1", "ButtonSignal");
                    btn.setText("ON");
                    btn.setBackgroundColor(Color.GREEN);
                    counter += 1;
                    String content = "Button pressed - OFF to ON";
                    addNotification(counter, notificationManager,  CHANNEL_ID, content);
                }
                btnStatus = !btnStatus;
            }
        });


        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
//            speed = location.getSpeed()+ "";
            slat = location.getLatitude()+ "";
            slong = location.getLongitude()+ "";
//            alti = location.getAltitude()+ "";
            latitude.setText(slat);
            longitude.setText(slong);
        }
    };

    // Creates and displays a notification
    private void addNotification(int counter, NotificationManager notificationManager, String CHANNEL_ID, String content) {
        int NOTIFICATION_ID = counter;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_vol_type_speaker_dark)
//                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Button Notification") // NOTI TITLE
//                .setContentText("This is a test notification " + counter) // NOTI TEXT
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

    private void sendDataMQTT(String data, String feed){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(true);

        byte[] b = data.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        Log.d("ABC", "Publish :" + msg);
        String topic = "longnguyen29798/feeds/" + feed;
        try {
            mqttHelperSend.mqttAndroidClient.publish(topic, msg);
//            result.setText("Successful");
        } catch (MqttException e) {

        }
    }
    private void startMQTTSend() {
        mqttHelperSend = new MQTTHelper(getApplicationContext(), "quyenho");
        mqttHelperSend.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    private void startMQTTReceive() {
        mqttHelperReceive = new MQTTHelper(getApplicationContext(), "longnguyen");
        mqttHelperReceive.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug", mqttMessage.toString());
//                dataReceived.setText(mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
}