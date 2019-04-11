package io.github.yashxd.lazybox;

import android.app.LauncherActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    Button switchButton;
    Button powerHistoryButton;
    Button configButton;

    Boolean switchState = false;

    String TAG = "MainActivity";

    MQTTHelper mqttHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchButton = findViewById(R.id.button_switch_activity_main);
        powerHistoryButton = findViewById(R.id.button_history_activity_main);
        configButton = findViewById(R.id.button_config_activity_main);

        //Initialize button in off state
        switchButton.setBackgroundColor(getResources().getColor(R.color.colorButtonOff));
        switchButton.setText(getText(R.string.switch_off));

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchState = !switchState;
                /*try {
                    mqttHelper.disconnect();
                } catch (MqttException e) {
                    e.printStackTrace();
                }*/
                if(switchState){
                    try {
                        mqttHelper.publishMessage("1");
                        switchButton.setBackgroundColor(getResources().getColor(R.color.colorButtonOn));
                        switchButton.setText(getText(R.string.switch_on));
                        Toast.makeText(getApplicationContext(),"Payload delivered", Toast.LENGTH_SHORT).show();
                    } catch (MqttException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"Error delivering payload", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    try {
                        mqttHelper.publishMessage("0");
                        switchButton.setBackgroundColor(getResources().getColor(R.color.colorButtonOff));
                        switchButton.setText(getText(R.string.switch_off));
                        Toast.makeText(getApplicationContext(),"Payload delivered", Toast.LENGTH_SHORT).show();
                    } catch (MqttException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),"Error delivering payload", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        powerHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PowerHistory.class);
                startActivity(intent);
            }
        });

        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Coming Soon!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        startMQTT();
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
                Log.w(TAG,mqttMessage.toString());
                Toast.makeText(getApplicationContext(), mqttMessage.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
}
