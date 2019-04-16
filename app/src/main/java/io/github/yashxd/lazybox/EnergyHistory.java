package io.github.yashxd.lazybox;

import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class EnergyHistory extends AppCompatActivity {

    LineGraphSeries<DataPoint> graphDat = new LineGraphSeries<>();
    float energyConsumed;
    GraphView graphView;
    View actionBarView;
    Button configButton;
    TextView textView;

    String TAG = "EnergyHistory";

    private int refreshInterval = 1000;
    private Handler handler;

    Runnable periodicUpdate = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d(TAG, "Running Periodic Update");
                MainActivity activity = new MainActivity();
                graphDat = activity.getGraphDat();
                graphView.removeAllSeries();
                graphView.addSeries(graphDat);
                energyConsumed = activity.getEnergyConsumed();
                textView.setText(""+energyConsumed);
                Log.d(TAG, "Power consumed = " + energyConsumed);
                Log.d(TAG, "Completed Periodic Update");

            } finally {
                handler.postDelayed(periodicUpdate, refreshInterval);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_energy_history);

        setupActionBar();

        handler = new Handler();

        graphView = findViewById(R.id.graphview_history_energy);
        textView = findViewById(R.id.energy_tv_energyhistory);
        graphDat = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 0),
                new DataPoint(1, 1),
                new DataPoint(2, 4),
                new DataPoint(3, 9),
                new DataPoint(4, 16),
                new DataPoint(5, 25)
        });
        graphView.addSeries(graphDat);
        startPeriodicUpdate();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPeriodicUpdate();
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

    private void startPeriodicUpdate(){
        periodicUpdate.run();
    }

    private void stopPeriodicUpdate() {
        handler.removeCallbacks(periodicUpdate);
    }
}
