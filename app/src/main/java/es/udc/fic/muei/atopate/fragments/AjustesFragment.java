package es.udc.fic.muei.atopate.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.support.v7.widget.AppCompatSpinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import es.udc.fic.muei.atopate.R;
import es.udc.fic.muei.atopate.activities.HomeActivity;
import es.udc.fic.muei.atopate.adapter.AjustesAdapter;
import es.udc.fic.muei.atopate.db.model.Trayecto;
import es.udc.fic.muei.atopate.entities.CustomToast;

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
        AppCompatSpinner spinner = vista.findViewById(R.id.spinner_tema);

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
                                    Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("tema", temas[pos]);
                    editor.apply();
                    new AjustesAdapter().setTema(activity, temas[pos]);

                    activity.startActivity(new Intent(activity, HomeActivity.class));
                    activity.finish();
                } catch (java.lang.NullPointerException e) {
                    Log.d(TAG, "onItemSelected: NullPointerException");
                    CustomToast toast = new CustomToast(activity, "No se puede cambiar tema, reinicie la aplicación", Toast.LENGTH_LONG);
                    toast.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {    }
        });

        final Button importarButton = vista.findViewById(R.id.btnImportar);
        importarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/plain");

                startActivityForResult(intent, 42);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == 42 && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());

                InputStream inputStream = null;
                try {
                    HomeActivity activity = (HomeActivity) getActivity();
                    inputStream = activity.getContentResolver().openInputStream(uri);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    inputStream.close();
                    reader.close();
                    String json = stringBuilder.toString();
                    Type listType = new TypeToken<List<Trayecto>>() {}.getType();
                    List<Trayecto> trayectos = new Gson().fromJson(json, listType);

                    for (Trayecto t : trayectos) {
                        activity.trayectoService.insert(t);
                    }
                    CustomToast toast = new CustomToast(getContext(), "Registros importados", Toast.LENGTH_LONG);
                    toast.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
