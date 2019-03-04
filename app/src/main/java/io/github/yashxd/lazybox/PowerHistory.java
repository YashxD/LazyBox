package io.github.yashxd.lazybox;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class PowerHistory extends AppCompatActivity {

    LineGraphSeries<DataPoint> graphDat = new LineGraphSeries<>();
    GraphView graphView;

    String TAG = "PowerHistory";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_history);

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
}
