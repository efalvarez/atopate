package es.udc.fic.muei.atopate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

import es.udc.fic.muei.atopate.maps.MapsConfigurer;
import es.udc.fic.muei.atopate.trayecto.TrayectoActivity;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_atopate:
                    return true;
                case R.id.navigation_resumen:
                    return true;
                case R.id.navigation_historico:
                    return true;
                case R.id.navigation_ajustes:
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Log.d(TAG, "onCreate() - HomeActivity");
            /* restore state */
        } else {
            Log.d(TAG, "onCreate() - No saved HomeActivity");
            /* initialize app */
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        configureMaps();

        configureButtons();

        configureCharts();

        configureBottomNavigation();
    }


    private void configureMaps() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        MapsConfigurer.initializeMap(mapFragment, this);
    }

    private void configureButtons() {

        final Button buttonAtopate = findViewById(R.id.buttonAtopate);
        buttonAtopate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "onClick - buttonAtopate");
            }
        });

        final Button buttonCompartir = findViewById(R.id.buttonCompartir);
        buttonCompartir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "onClick - buttonCompartir");
            }
        });

        final Button buttonVerMas = findViewById(R.id.buttonVerMas);
        buttonVerMas.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "onClick - buttonVerMas");
                Intent trayectoIntent = new Intent(HomeActivity.this, TrayectoActivity.class);
                startActivity(trayectoIntent);
            }
        });

    }

    private void configureCharts() {

        GraphView graph1 = (GraphView) findViewById(R.id.graph1);
        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3)
        });

        graph1.addSeries(series1);

        GraphView graph2 = (GraphView) findViewById(R.id.graph2);
        BarGraphSeries<DataPoint> series2 = new BarGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3)
        });

        graph2.addSeries(series2);

        PieChartView pieChartView = findViewById(R.id.chart);
        List<SliceValue> pieData = new ArrayList<>();
        pieData.add(new SliceValue(15, Color.BLUE));
        pieData.add(new SliceValue(25, Color.GREEN));
        pieData.add(new SliceValue(10, Color.RED));
        pieData.add(new SliceValue(60, Color.YELLOW));

        PieChartData pieChartData = new PieChartData(pieData);

        pieChartData.setHasLabels(true);

        pieChartData.setHasLabels(true).setValueLabelTextSize(14);

        pieChartData.setHasCenterCircle(true);


        pieChartView.setPieChartData(pieChartData);
    }

    private void configureBottomNavigation() {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsConfigurer.onMapsReady(googleMap);
    }
}
