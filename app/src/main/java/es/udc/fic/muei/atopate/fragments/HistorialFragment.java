package es.udc.fic.muei.atopate.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import es.udc.fic.muei.atopate.R;
import es.udc.fic.muei.atopate.adapter.ItemHistorialAdapter;
import es.udc.fic.muei.atopate.db.TrayectoService;
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
    ArrayList<itemHistorialEntity> historials = new ArrayList<itemHistorialEntity>();

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

        RecyclerView listV = view.findViewById(R.id.historial_list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        listV.setLayoutManager(layoutManager);


        ItemHistorialAdapter adapter = new ItemHistorialAdapter(getContext(), R.layout.activity_item_historial, historials);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            Trayecto trayectoEliminado;
            int position;

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                position = viewHolder.getAdapterPosition();
                Long idTrayecto = historials.get(position).getId();
                trayectoEliminado = trayectoService.getById(idTrayecto);
                trayectoService.delete(trayectoEliminado);
                historials.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                Snackbar.make(view, "Trayecto eliminado", Snackbar.LENGTH_LONG)
                        .setAction("Deshacer", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                trayectoService.insert(trayectoEliminado);
                                historials.add(position, new itemHistorialEntity(trayectoEliminado));
                                adapter.notifyItemInserted(position);
                            }
                        }).show();
            }

        };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(listV);

        Drawable icono = Drawable.createFromPath("@drawable/ic_launcher_background.xml");

        trayectoService = new TrayectoService(getContext());

        historials.addAll(trayectoService.getHistorial());

        listV.setAdapter(adapter);

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
