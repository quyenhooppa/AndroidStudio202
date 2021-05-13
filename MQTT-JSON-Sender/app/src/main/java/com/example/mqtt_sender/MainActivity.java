package com.example.mqtt_sender;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {
    MQTTHelper mqttHelper;
    EditText id;
    EditText temp;
    EditText light;
    Button btn;

    private void sendDataMQTT(String data){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(true);

        byte[] b = data.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        Log.d("ABC", "Publish :" + msg);
        try {
            mqttHelper.mqttAndroidClient.publish("longnguyen29798/feeds/json-string", msg);
        } catch (MqttException e) {

        }
    }
    private void startMQTT(){
        mqttHelper = new MQTTHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id = findViewById(R.id.id);
        temp = findViewById(R.id.temp);
        light = findViewById(R.id.light);
        btn = findViewById(R.id.btn);

        startMQTT();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sentId = id.getText().toString();
                String sentTemp = temp.getText().toString();
                String sentLight = light.getText().toString();

                JSONObject jsonString = new JSONObject();
                try {
                    jsonString.put("ID", sentId);
                    jsonString.put("Temperature", sentTemp);
                    jsonString.put("Light", sentLight);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String data = jsonString.toString();
                sendDataMQTT(data);
            }
        });
    }
}