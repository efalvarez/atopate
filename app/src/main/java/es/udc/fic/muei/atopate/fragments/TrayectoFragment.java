package es.udc.fic.muei.atopate.fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.udc.fic.muei.atopate.R;
import es.udc.fic.muei.atopate.maps.MapsConfigurer;
import es.udc.fic.muei.atopate.maps.RouteFinder;

import static android.support.constraint.Constraints.TAG;

public class TrayectoFragment extends Fragment implements OnMapReadyCallback {

    private static final long TIME_REQUEST = 10000;
    private static final long TIME_FAST_REQUEST = 5000;

    MapView mapaVista;
    TextView textViewLugar;
    transient ArrayList<LatLng> camino = new ArrayList<>();
    MarkerOptions inicioTrayecto, posicionActual;

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
        fusedLocationClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }

    @Override
    public void onPause() {
        mapaVista.onPause();
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
        textViewLugar.setText(getAddress());

        //Configuración del boton de ruta
        FloatingActionButton directionButton = vista.findViewById(R.id.directionButton);
    }

    public String getAddress() {
        Geocoder geocoder;
        List<Address> addresses;
        String address;
        geocoder = new Geocoder(this.getContext(), Locale.getDefault());
        LatLng latLng = camino.get(0);
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
            startLocationUpdates(mMap); // Se generan actualizaciones breves según las estáticas TIME_REQUEST y TIME_FAST_REQUEST

            /*if (activity.trayecto != null && activity.trayecto.puntosTrayecto != null) {
                HomeActivity activity = (HomeActivity) getActivity();
                //LatLng destino = activity.trayecto.puntosTrayecto.coordenadas.get(activity.trayecto.puntosTrayecto.coordenadas.size() - 1);

                fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng latLngLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            try {
                                RouteFinder.drawRoute(latLngLocation, destino, mMap, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }

                        } else {
                            CustomToast toast = new CustomToast(getActivity(), "Necesario activar la ubicación ", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });
            } else {
                CustomToast toast = new CustomToast(getActivity(), "Ubicación del aparcamiento no disponible", Toast.LENGTH_LONG);
                toast.show();
            }*/
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location actual : locationResult.getLocations()) {
                    LatLng latLngActual = new LatLng(actual.getLatitude(), actual.getLongitude());
                    camino.add(latLngActual);
                    if(camino.size()!=0) {
                        //Dibujar la ruta cada segun el recorrido de cada actualización
                        RouteFinder.drawingRoute(camino, mMap, getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
                    }
                    //Poner una marca al inicio del trayecto (opcional)
                    /*
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(camino.get(0));
                    markerOptions.title("Inicio del trayecto");

                    mMap.addMarker(markerOptions);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camino.get(0),10));
                    */
                }
            };
        };

        try {
            fusedLocationClient.requestLocationUpdates(getLocationRequest(),
                    locationCallback,
                    null /* Looper */);
        } catch (SecurityException se) {
            se.printStackTrace();
        }
    }

    public boolean saveArray(ArrayList<LatLng> array, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("caminoRecorrido", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName +"_size", array.size());
        for(int i=0;i<array.size();i++) {
            String cadena = Double.toString(array.get(i).latitude) + "," + Double.toString(array.get(i).longitude);
            editor.putString(arrayName + "_" + i, cadena);
        }
        return editor.commit();
    }

    public ArrayList<LatLng> loadArray(String arrayName, Context mContext)
    {
        SharedPreferences prefs = mContext.getSharedPreferences("caminoRecorrido", 0);
        int size = prefs.getInt(arrayName + "_size", 0);
        ArrayList<LatLng> arrayList = new ArrayList<>();
        for(int i=0;i<size;i++) {
            String cadena = prefs.getString(arrayName + "_" + i, null);
            String coord[] = cadena.split(",");
            LatLng latLng = new LatLng(Double.parseDouble(coord[0]), Double.parseDouble(coord[1]));
            arrayList.add(latLng);
        }
        return arrayList;
    }
}
