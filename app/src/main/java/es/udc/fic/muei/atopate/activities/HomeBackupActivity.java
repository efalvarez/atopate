package es.udc.fic.muei.atopate.activities;

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
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

import es.udc.fic.muei.atopate.AjustesActivity;
import es.udc.fic.muei.atopate.EstadisticasActivity;
import es.udc.fic.muei.atopate.R;
import es.udc.fic.muei.atopate.TrayectoActivity;
import es.udc.fic.muei.atopate.maps.MapsConfigurer;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class HomeBackupActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_atopate:
                    Toast.makeText(HomeBackupActivity.this, "Atópate",
                            Toast.LENGTH_SHORT).show();
                    Intent atopateIntent = new Intent(HomeBackupActivity.this, TrayectoActivity.class);
                    startActivity(atopateIntent);
                    return true;
                case R.id.navigation_resumen:
                    Toast.makeText(HomeBackupActivity.this, "Resumen",
                            Toast.LENGTH_SHORT).show();
                    Intent estadisticasIntent = new Intent(HomeBackupActivity.this, EstadisticasActivity.class);
                    startActivity(estadisticasIntent);
                    return true;
                case R.id.navigation_historico:
                    Toast.makeText(HomeBackupActivity.this, "Historial",
                            Toast.LENGTH_SHORT).show();
                    //Intent historialIntent = new Intent(HomeBackupActivity.this, HistorialActivity.class);
                    //startActivity(historialIntent);
                    return true;
                case R.id.navigation_ajustes:
                    Toast.makeText(HomeBackupActivity.this, "Ajustes",
                            Toast.LENGTH_SHORT).show();
                    Intent ajustesIntent = new Intent(HomeBackupActivity.this, AjustesActivity.class);
                    startActivity(ajustesIntent);
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
                .findFragmentById(R.id.home_map);
        MapsConfigurer.initializeMap(mapFragment, this);
    }

    private void configureButtons() {

        final Button buttonAtopate = findViewById(R.id.buttonAtopate);
        buttonAtopate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "onClick - buttonAtopate");
                Toast.makeText(HomeBackupActivity.this, "Atópate",
                        Toast.LENGTH_SHORT).show();
                Intent atopateIntent = new Intent(HomeBackupActivity.this, TrayectoActivity.class);
                startActivity(atopateIntent);
            }
        });

        final Button buttonCompartir = findViewById(R.id.buttonCompartir);
        buttonCompartir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "onClick - buttonCompartir");
                Toast.makeText(HomeBackupActivity.this, "Compartir",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void configureCharts() {

        GraphView graph1 = findViewById(R.id.graph1);
        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3)
        });

        graph1.addSeries(series1);

        GraphView graph2 = findViewById(R.id.graph2);
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
