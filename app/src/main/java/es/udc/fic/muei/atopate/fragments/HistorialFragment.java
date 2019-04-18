package es.udc.fic.muei.atopate.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import es.udc.fic.muei.atopate.R;
import es.udc.fic.muei.atopate.adapter.ItemHistorialAdapter;
import es.udc.fic.muei.atopate.db.TrayectoService;
import es.udc.fic.muei.atopate.db.dao.TrayectoDao;
import es.udc.fic.muei.atopate.db.AppDatabase;
import es.udc.fic.muei.atopate.db.model.Trayecto;
import es.udc.fic.muei.atopate.entities.itemHistorialEntity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistorialFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HistorialFragment extends Fragment {

    private TrayectoService trayectoService;

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

        trayectoService = new TrayectoService(getContext());

        historials.addAll(trayectoService.getHistorial());

        ItemHistorialAdapter adapter = new ItemHistorialAdapter(this.getActivity(), historials);

        listV.setAdapter(adapter);

        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
