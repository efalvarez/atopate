package es.udc.fic.muei.atopate.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import es.udc.fic.muei.atopate.R;
import es.udc.fic.muei.atopate.adapter.ItemHistorialAdapter;
import es.udc.fic.muei.atopate.entities.itemHistorialEntity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistorialFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HistorialFragment extends Fragment {

    private List<Integer> clicked;

    private OnFragmentInteractionListener mListener;
    ArrayList historials = new ArrayList<itemHistorialEntity>();

    public HistorialFragment() {
        // Required empty public constructor
    }

    public static HistorialFragment getInstance(){
        HistorialFragment fragment = new HistorialFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.clicked = new ArrayList<>();
//        if (getArguments() != null) {
//            // do nothing
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_historial, container, false);

        ListView listV = view.findViewById(R.id.historial_list);
        Drawable icono = Drawable.createFromPath("@drawable/ic_launcher_background.xml");

        //TODO enlazar a la base de datos y alimentar desde ahí en vez de toda esta cosa -->
        historials.add(new itemHistorialEntity("Hace 3 horas", "A coruña", "Madrid", "530km", icono, "2 horas", "hoy"));
        historials.add(new itemHistorialEntity("Lunes, 25/02/2019", "Av. dos Mallos", "Pza Pontevedra", "2km", icono, "15 minutos", "ayer"));
        historials.add(new itemHistorialEntity("Domingo, 24/02/2019", "Ronda de outeiro", "Pza Pontevedra", "1km", icono, "20 minutos", "ayer"));
        /*for (int i = 0; i<10; i++) {
            historials.add(new itemHistorialEntity());
        }*/
        //  <--------------------------------------------------------

        ItemHistorialAdapter adapter = new ItemHistorialAdapter(this.getActivity(), historials);

        listV.setAdapter(adapter);

        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(view.getContext(), "Apretado " + Integer.toString(position), Toast.LENGTH_SHORT).show();
                LinearLayout detallesItem = view.findViewById(R.id.detallesItem);
                if (!clicked.contains(position)) {
                    Log.d("HERE", "Pruebas: INTENTANDO INFLAR");
                    detallesItem.setVisibility(View.VISIBLE);
                    clicked.add(position);
                } else {
                    Log.d("HERE", "Pruebas: DESININFLANDO");
                    detallesItem.setVisibility(View.GONE);
                    int clickedPos = clicked.indexOf(position);
                    clicked.remove(clickedPos);
                }
            }
        });

        return view;
    }

    /*// TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
