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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
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
import es.udc.fic.muei.atopate.db.model.PuntosTrayecto;
import es.udc.fic.muei.atopate.maps.MapsConfigurer;
import es.udc.fic.muei.atopate.maps.RouteFinder;

import static android.content.Context.LOCATION_SERVICE;
import static android.support.constraint.Constraints.TAG;

public class TrayectoFragment extends Fragment implements OnMapReadyCallback {

    private static final long TIME_REQUEST = 10000;
    private static final long TIME_FAST_REQUEST = 5000;

    MapView mapaVista;
    TextView textViewLugar, textViewLugar2;
    transient ArrayList<LatLng> camino = new ArrayList<>();
    MarkerOptions inicioTrayecto, posicionActual;
    HomeActivity activity;

    FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

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


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
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
        setCoordenadas(camino);
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        } catch(NullPointerException npe) {
            Log.e(TAG, "onPause: Sin actualizaciones de localización");
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
        textViewLugar = vista.findViewById(R.id.textViewLugar);
        textViewLugar2 = vista.findViewById(R.id.textViewLugar2);
        if(!camino.isEmpty()) {
            textViewLugar.setText(getAddress(camino.get(0)));
        }
        //Configuración del boton de ruta
        FloatingActionButton directionButton = vista.findViewById(R.id.directionButton);
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
            LatLng latLng = new LatLng(latitude, longitude);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
            // --------------------------------------------------

            startLocationUpdates(mMap); // Se generan actualizaciones breves según las estáticas TIME_REQUEST y TIME_FAST_REQUEST
        }
    }

    protected LocationRequest getLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        // Intervalos en milisegundos
        locationRequest.setInterval(TIME_REQUEST);
        locationRequest.setFastestInterval(TIME_FAST_REQUEST);
        // Exactitud del mapa
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;
    }

    private void startLocationUpdates(GoogleMap mMap) {
        if (activity.bluetoothRecordIsActivated) {
            if (camino.isEmpty()) {
                Toast.makeText(activity, "Iniciando trayecto", Toast.LENGTH_SHORT).show();
            }
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location actual : locationResult.getLocations()) {
                        LatLng latLngActual = new LatLng(actual.getLatitude(), actual.getLongitude());
                        if (camino.size() != 0) {
                            //Dibujar la ruta cada segun el recorrido de cada actualización
                            RouteFinder.drawingRoute(camino, mMap, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
                        } else {
                            textViewLugar.setText(getAddress(latLngActual));
                        }
                        camino.add(latLngActual);
                    }

                }

                ;
            };

            try {
                fusedLocationClient.requestLocationUpdates(getLocationRequest(),
                        locationCallback,
                        null /* Looper */);
            } catch (SecurityException se) {
                se.printStackTrace();
            }
        }
    }

    public boolean setCoordenadas(ArrayList<LatLng> array) {
        try {
            PuntosTrayecto puntosTrayecto = activity.trayecto.puntosTrayecto;
            puntosTrayecto.coordenadas = array;
            return true;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public ArrayList<LatLng> getCoordenadas() {
        ArrayList<LatLng> arrayList;
        try {
            arrayList = new ArrayList<>(activity.trayecto.puntosTrayecto.coordenadas);
        } catch (NullPointerException npe) {
            Toast.makeText(activity, "No has iniciado un trayecto", Toast.LENGTH_SHORT).show();
            arrayList = new ArrayList<>();
        }
        return arrayList;
    }

}
