package es.udc.fic.muei.atopate.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import es.udc.fic.muei.atopate.AjustesActivity;
import es.udc.fic.muei.atopate.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AjustesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AjustesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AjustesFragment extends Fragment {

    private static final String TAG = AjustesFragment.class.getSimpleName();


    public AjustesFragment() {
        // Required empty public constructor
    }

    public static AjustesFragment newInstance() {
        AjustesFragment fragment = new AjustesFragment();
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

        View viewinflated = inflater.inflate(R.layout.fragment_ajustes, container, false);

        configureButtons(viewinflated);

        return viewinflated;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void configureButtons(View vista) {
        Button clickBtnExportar = (Button) vista.findViewById(R.id.btnExportar);
        clickBtnExportar.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Exportando copia de seguridad");
                /*Toast.makeText(AjustesActivity.this, "Exportando copia de seguridad...",
                        Toast.LENGTH_SHORT).show();*/
            }
        });

        Button clickBtnImportar = (Button) vista.findViewById(R.id.btnImportar);
        clickBtnImportar.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Importando copia de seguridad");
                /*Toast.makeText(AjustesActivity.this, "Importando copia de seguridad...",
                        Toast.LENGTH_SHORT).show();*/
            }
        });

        Button clickBtnEliminar = (Button) vista.findViewById(R.id.btnEliminar);
        clickBtnEliminar.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Eliminando registros");
                /*Toast.makeText(AjustesActivity.this, "Eliminando registros...",
                        Toast.LENGTH_SHORT).show();*/
            }
        });
    }
}
