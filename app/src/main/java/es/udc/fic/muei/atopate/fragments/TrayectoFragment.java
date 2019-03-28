package es.udc.fic.muei.atopate.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapView;

import es.udc.fic.muei.atopate.R;
import es.udc.fic.muei.atopate.maps.MapsConfigurer;

public class TrayectoFragment extends Fragment {

    MapView mapaVista;

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
    }

}
