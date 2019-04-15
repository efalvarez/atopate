package es.udc.fic.muei.atopate.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.support.v7.widget.AppCompatSpinner;
import android.widget.Toast;

import es.udc.fic.muei.atopate.R;
import es.udc.fic.muei.atopate.activities.HomeActivity;
import es.udc.fic.muei.atopate.adapter.AjustesAdapter;

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
        Button clickBtnExportar = vista.findViewById(R.id.btnExportar);
        AppCompatSpinner spinner = vista.findViewById(R.id.spinner_tema);

        clickBtnExportar.setOnClickListener(v -> {
            Log.d(TAG, "Exportando copia de seguridad");
        });

        Button clickBtnImportar = vista.findViewById(R.id.btnImportar);
        clickBtnImportar.setOnClickListener(v -> {
            Log.d(TAG, "Importando copia de seguridad");
        });

        Button clickBtnEliminar = vista.findViewById(R.id.btnEliminar);
        clickBtnEliminar.setOnClickListener(v -> {
            Log.d(TAG, "Eliminando registros");
        });

        Log.d(TAG, "configureButtons: " + spinner.getClass().toString());

        spinner.setSelection(0,false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {

                HomeActivity activity = (HomeActivity) getActivity();

                String temas[] = getResources().getStringArray(R.array.temas_array);
                SharedPreferences pref = null;
                try {
                    pref = activity
                            .getSharedPreferences("PreferenciasAtopate",
                                    activity.MODE_PRIVATE);

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("tema", temas[pos]);
                    editor.apply();
                    new AjustesAdapter().setTema(activity, temas[pos]);

                    activity.startActivity(new Intent(activity, HomeActivity.class));
                    activity.finish();
                } catch (java.lang.NullPointerException e) {
                    Log.d(TAG, "onItemSelected: NullPointerException");
                    Toast.makeText(activity, "No se puede cambiar tema, reinicie la aplicación",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {    }
        });
    }
}
