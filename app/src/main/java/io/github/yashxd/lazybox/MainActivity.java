package io.github.yashxd.lazybox;

import android.app.LauncherActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button switchButton;
    Button powerHistoryButton;
    Button connectButton;

    Boolean switchState = false;

    String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchButton = findViewById(R.id.button_switch_activity_main);
        powerHistoryButton = findViewById(R.id.button_history_activity_main);
        connectButton = findViewById(R.id.button_connect_activity_main);

        //Initialize button in off state
        switchButton.setBackgroundColor(getResources().getColor(R.color.colorButtonOff));
        switchButton.setText(getText(R.string.switch_off));

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchState = !switchState;
                if(switchState){
                    switchButton.setBackgroundColor(getResources().getColor(R.color.colorButtonOn));
                    switchButton.setText(getText(R.string.switch_on));
                }
                else{
                    switchButton.setBackgroundColor(getResources().getColor(R.color.colorButtonOff));
                    switchButton.setText(getText(R.string.switch_off));
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

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Coming Soon!",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
