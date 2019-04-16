package io.github.yashxd.lazybox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    Button switchButton;
    Button energyHistoryButton;
    Button configButton;
    TextView energyTextView;
    View actionBarView;
    Boolean switchState = false;

    String TAG = "MainActivity";

    MQTTHelper mqttHelper;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    boolean isInitial = false;
    static float energyConsumed = 0;
    static LineGraphSeries<DataPoint> graphDat = new LineGraphSeries<>();
    String graphDatStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupActionBar();

        //Initialize Shared Preferences.
        sharedPreferences = getApplicationContext().getSharedPreferences("graphdat", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        graphDatStr = sharedPreferences.getString("graphDat", "");
        energyConsumed = sharedPreferences.getFloat("energyTot", 0);

        energyTextView = findViewById(R.id.energy_tv);

        if(graphDatStr.equals("")||energyConsumed==0){
            //Initialize new session
           isInitial = true;
           graphDat = new LineGraphSeries<>(new DataPoint[]{
                   new DataPoint(0,0)
           });
        } else {
            isInitial = false;
            energyConsumed = 0;
            Log.d(TAG, "graphDatStr = "+graphDatStr);
            updateEnergy(graphDatStr);
            isInitial = true;
        }
        switchButton = findViewById(R.id.button_switch_activity_main);
        energyHistoryButton = findViewById(R.id.button_history_activity_main);

        //configButton = findViewById(R.id.button_config_activity_main);

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
                        Log.d(TAG, "Payload delivered");
                        //Toast.makeText(getApplicationContext(),"Payload delivered", Toast.LENGTH_SHORT).show();
                    } catch (MqttException e) {
                        e.printStackTrace();
                        //Toast.makeText(getApplicationContext(),"Error delivering payload", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    try {
                        mqttHelper.publishMessage("0");
                        switchButton.setBackgroundColor(getResources().getColor(R.color.colorButtonOff));
                        switchButton.setText(getText(R.string.switch_off));
                        //Log.d(TAG, "Payload delivered");
                        //Toast.makeText(getApplicationContext(),"Payload delivered", Toast.LENGTH_SHORT).show();
                    } catch (MqttException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error delivering payload");
                        //Toast.makeText(getApplicationContext(),"Error delivering payload", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        energyHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EnergyHistory.class);
                startActivity(intent);
            }
        });
        startMQTT();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    void setupActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.layout_actionbar);
        actionBarView = getSupportActionBar().getCustomView();
        configButton = actionBarView.findViewById(R.id.button_init_actionbar);
        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Coming Soon!",Toast.LENGTH_SHORT).show();
            }
        });
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
            public void messageArrived(String topic, MqttMessage mqttMessage){
                Log.w(TAG,"Message on " + topic + ": " +mqttMessage.toString());
                Log.d(TAG, "Updating Energy");
                updateEnergy(mqttMessage.toString());
                //Toast.makeText(getApplicationContext(), mqttMessage.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
    
    public void updateEnergy(String msg) {
        int msgSize = msg.length();
        DataPoint dat;
        float val = 0;
        float energy = 0;
        String tempStr;
        Log.d(TAG, "msg = " + msg);
        for(int i=0; i<msgSize; i+=5){
            tempStr = msg.substring(i,i+5);
            Log.d(TAG, "Temptr = " + tempStr);
            //val = floatFromStr(tempStr);
            val=Float.parseFloat(tempStr);
            energy = (float) ( val * 3.8334);
            Log.d(TAG, "new Energy = " + energy);
            energyConsumed += energy;
            float a = (float) graphDat.getHighestValueX()+1;
            dat = new DataPoint(a,energyConsumed);
            graphDat.appendData(dat,true,60);
            String.format("%.3f",energyConsumed);
            graphDatStr+=val;
        }
        if(isInitial) {
            editor.putString("graphDat", graphDatStr);
            editor.putFloat("energyTot", energyConsumed);
            editor.apply();
        }
        energyTextView.setText(""+energyConsumed);
    }

    public float floatFromStr(String str) {
        float val;
        //val = str.charAt(0) - 48;
        //val += (str.charAt(2) - 48)/10;
        //val += (str.charAt(3) - 48)/100;
        //val += (str.charAt(4) - 48)/1000;
        val=Float.parseFloat(str);
        return val;
    }

    public LineGraphSeries<DataPoint> getGraphDat() {
        return graphDat;
    }

    public float getEnergyConsumed(){
        return energyConsumed;
    }
    /*public void updateEnergy(MqttMessage mqttMessage) {
        String msg = mqttMessage.toString();
        int msgSize = msg.length();
        DataPoint dat;
        float val = 0;
        float energy = 0;
 
            val=Float.parseFloat(msg);
            energy = val * 230;
            energyConsumed += energy;
            Log.d(TAG,"parsed val="+val+" cummlative pwr="+energyConsumed);
            float a = (float) graphDat.getHighestValueX()+1;
            dat = new DataPoint(a,val);
            graphDat.appendData(dat,true,60);
            graphDatStr+=val;
        editor.putString("graphDat",graphDatStr);
        editor.putFloat("energyTot",energyConsumed);
        editor.apply();
    }
*/
}
