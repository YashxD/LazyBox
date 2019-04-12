package io.github.yashxd.lazybox;

import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class PowerHistory extends AppCompatActivity {

    LineGraphSeries<DataPoint> graphDat = new LineGraphSeries<>();
    GraphView graphView;
    View actionBarView;
    Button configButton;

    String TAG = "PowerHistory";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_history);

        setupActionBar();
        graphView = findViewById(R.id.graphview_history_power);

        graphDat = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 0),
                new DataPoint(1, 1),
                new DataPoint(2, 4),
                new DataPoint(3, 9),
                new DataPoint(4, 16),
                new DataPoint(5, 25)
        });

        graphView.addSeries(graphDat);
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
}
