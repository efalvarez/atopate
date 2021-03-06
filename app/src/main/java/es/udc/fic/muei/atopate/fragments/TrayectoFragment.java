package es.udc.fic.muei.atopate.fragments;

import android.Manifest;
import android.content.Context;
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
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import es.udc.fic.muei.atopate.R;
import es.udc.fic.muei.atopate.activities.HomeActivity;
import es.udc.fic.muei.atopate.maps.MapsConfigurer;
import es.udc.fic.muei.atopate.maps.RouteFinder;

import static android.support.constraint.Constraints.TAG;
import static es.udc.fic.muei.atopate.maps.MapsConfigurer.getInicioTrayecto;

public class TrayectoFragment extends Fragment implements OnMapReadyCallback {

    MapView mapaVista;
    TextView textViewDestino;
    MarkerOptions inicioTrayecto, posicionActual;

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
        textViewDestino = vista.findViewById(R.id.textViewDestino);
        textViewDestino.setText(getAddress());

        //Configuración del boton de ruta
        FloatingActionButton directionButton = vista.findViewById(R.id.directionButton);
    }

    public String getAddress() {
        Geocoder geocoder;
        List<Address> addresses;
        String address;
        geocoder = new Geocoder(this.getContext(), Locale.getDefault());
        LatLng latLng = getInicioTrayecto();
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String street = addresses.get(0).getAddressLine(0);
            address = street;

        } catch (IOException e) {
            Log.d(TAG, "getAddress: e.getMessage();");
            address = "No se pudo obtener el destino";
        }
        return address;
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        if (false) { // TODO: IF TRAYECTO EN CURSO (Se va marcando el trayecto según se avanza)

        } else { // TODO: IF MODO ATOPATE
            HomeActivity activity = (HomeActivity) getActivity();

            if (activity.trayecto != null && activity.trayecto.puntosTrayecto != null) {
                LatLng destino = activity.trayecto.puntosTrayecto.coordenadas.get(activity.trayecto.puntosTrayecto.coordenadas.size() - 1);

                if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

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
                            Toast.makeText(getActivity(), "Necesario activar la ubicación ", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Ubicación del aparcamiento no disponible", Toast.LENGTH_LONG).show();
            }
        }
    }
}
