package es.udc.fic.muei.atopate.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.udc.fic.muei.atopate.R;
import es.udc.fic.muei.atopate.activities.HomeActivity;
import es.udc.fic.muei.atopate.db.TrayectoService;
import es.udc.fic.muei.atopate.db.model.Trayecto;
import es.udc.fic.muei.atopate.entities.CustomToast;
import es.udc.fic.muei.atopate.maps.MapsConfigurer;
import es.udc.fic.muei.atopate.maps.RouteFinder;

import static android.content.Context.LOCATION_SERVICE;
import static android.support.constraint.Constraints.TAG;

public class TrayectoFragment extends Fragment implements OnMapReadyCallback {

    MapView mapaVista;
    TextView textViewLugar, textViewLugar2;
    LatLng latLngActual;
    transient ArrayList<LatLng> camino;
    MarkerOptions inicioTrayecto, posicionActual;
    HomeActivity activity;
    volatile MapaDrawing mapaDrawing;
    FloatingActionButton directionButton;

    public TrayectoFragment() {
        // Required empty public constructor
    }

    public static TrayectoFragment newInstance() {
        TrayectoFragment fragment = new TrayectoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vistaTrayecto = inflater.inflate(R.layout.fragment_trayecto, container, false);

        activity = (HomeActivity) getActivity();
        camino = getCoordenadas();
        configureMaps(vistaTrayecto, savedInstanceState);

        return vistaTrayecto;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // definicion de metodos con el fin de que el mapa se comporte de acorde a la vista

    @Override
    public void onResume() {
        mapaVista.onResume();
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapaVista.onStart();
    }

    @Override
    public void onStop() {
        mapaVista.onStop();
        super.onStop();
    }

    @Override
    public void onPause() {
        mapaVista.onPause();
        mapaDrawing.detener();
        mapaDrawing.interrupt();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapaVista.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapaVista.onLowMemory();
        super.onLowMemory();
    }

    private void configureMaps(View vista, Bundle savedInstanceState) {
        mapaVista = vista.findViewById(R.id.trayecto_mapa);
        MapsConfigurer.initializeMap(getActivity(), mapaVista, savedInstanceState, this);

        //Configuración del texto de dirección de inicio
        textViewLugar = vista.findViewById(R.id.textViewLugar);
        textViewLugar2 = vista.findViewById(R.id.textViewLugar2);
        if(!camino.isEmpty()) {
            textViewLugar.setText(getAddress(camino.get(0)));
        } else {
            textViewLugar.setText(getString(R.string.sin_punto_de_partida));
        }
        //Configuración del boton de ruta
        directionButton = vista.findViewById(R.id.directionButton);

    }

    public String getAddress(LatLng punto) {
        Geocoder geocoder;
        List<Address> addresses;
        String address;
        geocoder = new Geocoder(this.getContext(), Locale.getDefault());
        LatLng latLng = punto;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String street = addresses.get(0).getAddressLine(0);
            address = street;

        } catch (IOException e) {
            Log.d(TAG, "getAddress: e.getMessage();");
            address = "No se pudo obtener el lugar";
        }
        return address;
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        if (false) { // TODO: IF TRAYECTO EN CURSO (Se va marcando el trayecto según se avanza)

        } else { // TODO: IF MODO ATOPATE
            if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true); // Se habilita el puntero de ubicación en el mapa

            // --- EL siguiente bloque sólo posiciona el mapa --
            LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location myLocation = locationManager.getLastKnownLocation(provider);
            double latitude = myLocation.getLatitude();
            double longitude = myLocation.getLongitude();
            latLngActual = new LatLng(latitude, longitude);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngActual,16));
            // --------------------------------------------------
            textViewLugar2.setText(getAddress(latLngActual).split(",")[0]);
            mapaDrawing = new MapaDrawing(mMap, 20000);
            mapaDrawing.start();

            directionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new CustomToast(v.getContext(), getString(R.string.regreso_a_coche), Toast.LENGTH_SHORT);
                    mMap.clear();
                    if (ActivityCompat.checkSelfPermission(v.getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(v.getContext(),
                                    Manifest.permission.ACCESS_COARSE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    Trayecto ultimoTrayecto = new TrayectoService(v.getContext()).getLast();
                    Location myLocation = locationManager.getLastKnownLocation(provider);
                    LatLng tuPosicion = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    LatLng posicionCoche = ultimoTrayecto.puntosTrayecto.coordenadas.get(
                            ultimoTrayecto.puntosTrayecto.coordenadas.size() - 1);
                    RouteFinder.drawRoute(
                            tuPosicion,
                            posicionCoche,
                            mMap,
                            v.getResources().getDisplayMetrics().widthPixels,
                            v.getResources().getDisplayMetrics().heightPixels,
                            RouteFinder.WITHOUT_MARKERS);
                }
            });
        }
    }

    public ArrayList<LatLng> getCoordenadas() {
        ArrayList<LatLng> arrayList;
        try {
            arrayList = new ArrayList<>(activity.trayecto.puntosTrayecto.coordenadas);
        } catch (NullPointerException npe) {
            arrayList = new ArrayList<>();
        }
        return arrayList;
    }

    class MapaDrawing extends Thread {
        GoogleMap mapToDraw;
        long TIME_REQUEST;
        boolean hilo;

        MapaDrawing(GoogleMap map, long TIME_REQUEST) {
            this.mapToDraw = map;
            this.TIME_REQUEST = TIME_REQUEST;
            hilo = true;
        }

        void detener() {
            hilo = false;
        }

        @Override
        public void run() {
            try {
                if(!activity.isBluetoothConnectionEstablished) {
                    new Handler(Looper.getMainLooper()).post(() -> new CustomToast(
                            activity, "No hay conexión bluetooth", Toast.LENGTH_SHORT).show());
                }

                while(hilo) {
                    try {
                        camino = new ArrayList<>(activity.trayecto.puntosTrayecto.coordenadas);
                    } catch (NullPointerException npe) {
                        new Handler(Looper.getMainLooper()).post(() -> new CustomToast(
                                activity, "No has iniciado un trayecto. Conectate a OBD",
                                Toast.LENGTH_SHORT).show());
                        break;
                    }
                    new Handler(Looper.getMainLooper()).post(() -> startDrawingLocation(mapToDraw));
                    sleep(TIME_REQUEST);
                }
            } catch(InterruptedException ie) {
                Log.e(TAG, "MapaDrawing -> run: Se interrumpió el hilo de actualizaciones del mapa", ie);
            }
        }

        private void startDrawingLocation(GoogleMap mMap) {
            if (camino.size() != 0) {
                //Dibujar la ruta cada segun el recorrido de cada actualización
                RouteFinder.drawingRoute(camino, mMap, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
            }
            textViewLugar2.setText(getAddress(camino.get(camino.size() - 1)).split(",")[0]);
        }
    }
/*
    private static class setDrawingAsyncTask extends AsyncTask<MapaDrawing, Void, Void> {

        @Override
        protected Void doInBackground(MapaDrawing... params) {
            MapaDrawing thread = params[0];
            thread.run();
            return null;
        }
    }*/


}


