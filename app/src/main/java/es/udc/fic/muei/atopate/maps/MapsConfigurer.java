package es.udc.fic.muei.atopate.maps;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import es.udc.fic.muei.atopate.fragments.HomeFragment;

public class MapsConfigurer {

    private static GoogleMap mMap;

    private static final String TAG = MapsConfigurer.class.getName();

    @Deprecated
    public static void initializeMap(SupportMapFragment mapFragment, OnMapReadyCallback callback) {
        mapFragment.getMapAsync(callback);
    }

    @Deprecated
    public static void onMapsReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng inicioTrayecto = getInicioTrayecto();
        mMap.addMarker(new MarkerOptions().position(inicioTrayecto).title("Marker in FIC"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(inicioTrayecto));
    }

    public static LatLng getInicioTrayecto() {
        LatLng inicioTrayecto = new LatLng(43.333024, -8.410868);

        /* TODO Obtener inicio del trayecto en vez de esto
        * Probablemente sea mejor que este método no exista y tomarlo
        * directamente en el inicioTrayecto del método onMapsReady desde la clase
        * que lo tome para tomarlo de ahí mismo en el TrayectoFragment
        */
        try {
            Bundle saveInstance = new Bundle();
            saveInstance.getParcelable("location");
        } catch (Exception ev) {
            inicioTrayecto = new LatLng(43.333024, -8.410868);
        }
        //--------------------------------------------------------------
        return inicioTrayecto;
    }

    public static void initializeMap(Activity containerActivity, MapView vistaMapa, Bundle savedInstanceState) {

        vistaMapa.onCreate(savedInstanceState);

        vistaMapa.onResume(); // necesario para que el mapa funcione correctamente

        try {
            MapsInitializer.initialize(containerActivity.getApplicationContext());
        } catch (Exception e) {
            Log.i(TAG, "Ha habido un problema a la hora de recuperar los datos del mapa");
            Toast.makeText(containerActivity.getApplicationContext(), "Error al cargar el mapa.",Toast.LENGTH_LONG).show();
        }

        vistaMapa.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mapa) {
                // For dropping a marker at a point on the Map
                LatLng fic = new LatLng(43.333024, -8.410868);
                mapa.addMarker(new MarkerOptions().position(fic).title("FIC").snippet("Marker in FIC"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(fic).zoom(12).build();
                mapa.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
    }

    public static GoogleMap getmMap() {
        return mMap;
    }
}
