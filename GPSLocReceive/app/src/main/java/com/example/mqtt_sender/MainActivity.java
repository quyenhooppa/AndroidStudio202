package com.example.mqtt_sender;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
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
    MQTTHelper mqttHelper;
    Button btn;
    Integer counter = 0;
    Boolean btnStatus = false;
    Boolean changed = true;
    NotificationManager notificationManager;
    String CHANNEL_ID;
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
    }


    // Creates and displays a notification
    private void addNotification(int counter, NotificationManager notificationManager, String CHANNEL_ID, String content) {
        int NOTIFICATION_ID = counter;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_vol_type_speaker_dark)
//                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("GPS Receiver") // NOTI TITLE
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

    private void updateLocation(String mess) throws JSONException {
        Log.d("location", mess);

        JSONObject json = new JSONObject(mess.toString());
        String lat = json.getString("lat");
        String lon = json.getString("lon");

        latitude.setText(lat);
        longitude.setText(lon);
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

        Log.d("long-ABC", "Publish: " + msg);
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
                Log.d("long-Arrive", mess + "-" + topic + topic.equals(mqttHelper.feedName + "gps-location"));

                if (topic.equals(mqttHelper.feedName + "buttonsignal")) {
                    if (changed == true) {
                        updateButton(mess);
                    }
                    changed = true;
                }

                if (topic.equals(mqttHelper.feedName + "gps-location")) {
                    updateLocation(mess);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

}