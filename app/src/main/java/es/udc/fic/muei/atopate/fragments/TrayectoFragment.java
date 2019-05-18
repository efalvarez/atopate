package es.udc.fic.muei.atopate.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

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
    TextView direccionInicio, ubicacionActual;
    transient ArrayList<LatLng> camino;
    HomeActivity activity;
    volatile MapaDrawing mapaDrawing;
    FloatingActionButton directionButton;

    private TrayectoService trayectoService;

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

        trayectoService = new TrayectoService(this.getContext());
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

        if (mapaDrawing != null) {
            mapaDrawing.detener();
        }

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
        direccionInicio = vista.findViewById(R.id.textViewLugar);
        ubicacionActual = vista.findViewById(R.id.textViewLugar2);

        if (!CollectionUtils.isEmpty(camino)) {

            direccionInicio.setText(getAddress(camino.get(0)));

        } else {

            direccionInicio.setText(getString(R.string.sin_punto_de_partida));

        }
        //Configuración del boton de ruta
        directionButton = vista.findViewById(R.id.directionButton);

    }

    public String getAddress(LatLng punto) {

        Geocoder geocoder;
        List<Address> addresses;
        String address;
        geocoder = new Geocoder(this.getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(punto.latitude, punto.longitude, 1);
            address = addresses.get(0).getAddressLine(0);

        } catch (IOException e) {
            Log.d(TAG, "getAddress: e.getMessage();");
            address = "No se pudo obtener el lugar";
        }

        return address;
    }

    @Override
    public void onMapReady(GoogleMap mMap) {

        boolean accessFineNotConceeded = ActivityCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;

        boolean accessCoarseNotConceeded = ActivityCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;

        if (accessFineNotConceeded && accessCoarseNotConceeded) {
            // en caso de que no tengamos los permisos lo paramos
            return;
        }

        mMap.setMyLocationEnabled(true); // Se habilita el puntero de ubicación en el mapa

        // --- Intentamos posicionar inicialmente el mapa
        LocationManager locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

        boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        String provider = locationManager.getBestProvider(new Criteria(), true);

        if (!gps_enabled) {
            // comprobamos si esta activado el GPS

            new AlertDialog.Builder(getContext())
                    .setTitle("Aviso")
                    .setMessage("Es necesario activar el servicio del GPS. ¿Quieres activarlo?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();

        } else {

            Location myLocation = locationManager.getLastKnownLocation(provider);

            if (myLocation != null) {
                // intentamos establecer la posicion en la que se encuentra actualmente

                double latitude = myLocation.getLatitude();
                double longitude = myLocation.getLongitude();
                LatLng latLngActual = new LatLng(latitude, longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngActual, 16));
                ubicacionActual.setText(getAddress(latLngActual).split(",")[0]);
            }

            // si somos capaces de recuperar las localizaciones podremos tirar de ellas
            mapaDrawing = new MapaDrawing(mMap, 20000);
            mapaDrawing.start();
        }


        directionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Trayecto currentTrayecto = trayectoService.getCurrentTrayecto();

                if (currentTrayecto != null) {
                    // si estamos en un trayecto en curso ya estamos mostrando el camino
                    new CustomToast(v.getContext(), "Finaliza el trayecto en curso para poder " +
                            "volver a la ultima posicion", Toast.LENGTH_SHORT).show();
                    return;
                }

                mMap.clear();
                Trayecto ultimoTrayecto = trayectoService.getLast();

                boolean accessFineNotConceeded = ActivityCompat.checkSelfPermission(v.getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;

                boolean accessCoarseNotConceeded = ActivityCompat.checkSelfPermission(v.getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;

                if (accessFineNotConceeded && accessCoarseNotConceeded) {
                    // en caso de que no tengamos los permisos lo paramos
                    return;
                }

                new CustomToast(v.getContext(), getString(R.string.regreso_a_coche), Toast.LENGTH_SHORT).show();

                try {
                    Location myLocation = locationManager.getLastKnownLocation(provider);
                    LatLng tuPosicion = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    LatLng posicionCoche = ultimoTrayecto.puntosTrayecto.coordenadas.get(
                            ultimoTrayecto.puntosTrayecto.coordenadas.size() - 1);

                    RouteFinder.drawRoute(tuPosicion, posicionCoche, mMap,
                            v.getResources().getDisplayMetrics().widthPixels,
                            v.getResources().getDisplayMetrics().heightPixels,
                            RouteFinder.WITHOUT_MARKERS);

                    mapaDrawing.detener();

                } catch (Exception e) {
                    Log.e(TAG, "onClick: No se tiene posición del auto", e);
                    new CustomToast(v.getContext(), getString(R.string.no_hay_posicion_de_coche), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public ArrayList<LatLng> getCoordenadas() {

        ArrayList<LatLng> arrayList = new ArrayList<>();

        Trayecto trayecto = trayectoService.getCurrentTrayecto();

        // en caso de que no tengamos un trayecto en curso, vamos a mostrar las coordenadas del
        // ultimo trayecto
        if (trayecto == null) {
            trayecto = trayectoService.getLast();
        }

        if (trayecto != null
                && trayecto.puntosTrayecto != null
                && CollectionUtils.isEmpty(trayecto.puntosTrayecto.coordenadas)) {
            // o bien pintamos las coordenadas del trayecto en curso o bien las del ultimo trayecto

            arrayList.addAll(trayecto.puntosTrayecto.coordenadas);
        }

        return arrayList;
    }

    private class MapaDrawing extends Thread {
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

            Trayecto currentTrayecto = trayectoService.getCurrentTrayecto();


            if (currentTrayecto == null) {
                // en caso de no tengamos un trayecto en curso que pintar, mostramos el ultimo

                Trayecto lastTrayecto = trayectoService.getLast();

                if (lastTrayecto != null && lastTrayecto.puntosTrayecto != null
                    && !CollectionUtils.isEmpty(lastTrayecto.puntosTrayecto.coordenadas)) {

                    camino = (ArrayList<LatLng>) lastTrayecto.puntosTrayecto.coordenadas;

                    new Handler(Looper.getMainLooper()).post(() -> startDrawingLocation(mapToDraw));

                } else {
                    // no tenemos ningun trayecto que pintar

                    new Handler(Looper.getMainLooper()).post(() -> new CustomToast(
                            activity, "No se ha encontrado ningun trayecto", Toast.LENGTH_SHORT).show());
                }


            } else {
                // caso de que exista algun trayecto en curso

                try {

                    while (currentTrayecto != null) {

                        if (currentTrayecto.puntosTrayecto != null
                                && !CollectionUtils.isEmpty(currentTrayecto.puntosTrayecto.coordenadas)) {

                            camino = (ArrayList<LatLng>) currentTrayecto.puntosTrayecto.coordenadas;

                        } else {

                            Log.d(TAG, "No se han encontrado coordenadas asociadas al trayecto actual, pintando las del ultimo...");
                            break;
                        }

                        new Handler(Looper.getMainLooper()).post(() -> startDrawingLocation(mapToDraw));
                        sleep(TIME_REQUEST);
                        // nos actualizamos para ver si hay algun dato
                        currentTrayecto = trayectoService.getCurrentTrayecto();
                    }

                } catch (InterruptedException e) {
                    Log.e(TAG, "MapaDrawing -> run: Se interrumpió el hilo de actualizaciones del mapa", e);
                }

            }

        }

        private void startDrawingLocation(GoogleMap mMap) {
            if (!CollectionUtils.isEmpty(camino)) {
                //Dibujar la ruta cada segun el recorrido de cada actualización
                RouteFinder.drawingRoute(camino, mMap, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
            }
            ubicacionActual.setText(getAddress(camino.get(camino.size() - 1)).split(",")[0]);
        }
    }


}


