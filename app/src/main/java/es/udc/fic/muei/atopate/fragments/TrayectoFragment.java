package es.udc.fic.muei.atopate.fragments;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import es.udc.fic.muei.atopate.R;
import es.udc.fic.muei.atopate.maps.MapsConfigurer;

import static android.support.constraint.Constraints.TAG;
import static es.udc.fic.muei.atopate.maps.MapsConfigurer.getInicioTrayecto;

public class TrayectoFragment extends Fragment {

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

        configureMaps(vistaTrayecto,savedInstanceState);

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
        MapsConfigurer.initializeMap(getActivity(), mapaVista, savedInstanceState);

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
            /*String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            address = String.format("%s, %s, %s - %s", street.split(",")[0], city, state, country);*/
            address = street;

        } catch (IOException e) {
            Log.d(TAG, "getAddress: e.getMessage();");
            address = "No se pudo obtener el destino";
        }
        return address;
    }

}
