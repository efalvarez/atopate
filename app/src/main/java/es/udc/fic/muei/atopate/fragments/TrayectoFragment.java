package es.udc.fic.muei.atopate.fragments;

import android.Manifest;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.model.DirectionsResult;

import es.udc.fic.muei.atopate.R;
import es.udc.fic.muei.atopate.maps.MapsConfigurer;
import es.udc.fic.muei.atopate.maps.RouteFinder;
import okhttp3.Route;

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
        // TODO poner la funcionalidad del botón y generar la ruta,  asi como poner escribir en el texto donde se encuentra el mono
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
            LatLng marineda = new LatLng(43.3441932, -8.4282207);

            if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                mMap.addMarker(new MarkerOptions().position(marineda));
                return;
            }
            mMap.setMyLocationEnabled(true);
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    LatLng latLngLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    if (location != null) {
                        RouteFinder.drawRoute(latLngLocation, marineda, mMap);

                        // Route center
                        LatLng center = new LatLngBounds.Builder().include(latLngLocation).include(marineda).build().getCenter();

                        // For zooming automatically to the location of the marker
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(center).zoom(13).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }
            });
        }
    }
}
