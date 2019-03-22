package es.udc.fic.muei.atopate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import es.udc.fic.muei.atopate.maps.MapsConfigurer;

public class TrayectoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = TrayectoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            Log.d(TAG, "onCreate()");
        } else {
            Log.d(TAG, "onCreate() with previousState");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trayecto);

        configureMaps();
    }

    private void configureMaps() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        MapsConfigurer.initializeMap(mapFragment, this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsConfigurer.onMapsReady(googleMap);
    }

    public void onBottomButtonClick(View view) {
        Log.d(TAG, "Boton para cambio de actividad pulsado en Trayecto");
        Toast.makeText(getApplicationContext(), "Boton en trayecto pulsado", Toast.LENGTH_SHORT).show();
    }

    public void onDirectionsClick(View view) {
        Log.d(TAG, "Boton direceciones pulsado en Trayecto");
        Toast.makeText(getApplicationContext(), "Boton direcciones", Toast.LENGTH_SHORT).show();
    }
}
