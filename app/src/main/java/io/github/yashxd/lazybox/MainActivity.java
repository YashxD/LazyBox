package io.github.yashxd.lazybox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button grbt;
    TextView powtv;
    Switch swch1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        grbt=(Button)findViewById(R.id.graph_btn);
        powtv=(TextView)findViewById(R.id.power_tv);
        swch1=(Switch)findViewById(R.id.tog1);

        swch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //send ping to server
                Toast.makeText(getApplicationContext(),"Switch toggled",Toast.LENGTH_SHORT).show();
            }
        });
        grbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start graph activity
            }
        });

    }

}
