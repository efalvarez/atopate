package es.udc.fic.muei.atopate.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.udc.fic.muei.atopate.R;
import es.udc.fic.muei.atopate.entities.itemHistorialEntity;


public class ItemHistorialAdapter extends BaseAdapter {

    private Activity activity;
    private List<itemHistorialEntity> items;

    public ItemHistorialAdapter(Activity activity, List<itemHistorialEntity> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void addAll(List<itemHistorialEntity> itemHistorials) {
        items.addAll(itemHistorials);
    }

    @Override
    public Object getItem(int arg0) {
        return items.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.activity_item_historial, parent, false);
        }

        itemHistorialEntity dir = items.get(position);

        TextView tiempoItem = view.findViewById(R.id.tiempoItem);
        tiempoItem.setText(dir.getTiempo());

        TextView lugarItem =  view.findViewById(R.id.lugarItem);
        lugarItem.setText(dir.getLugarOrigen() + "-" + dir.getLugarDestino());

        TextView distanciaItem =  view.findViewById(R.id.distanciaItem);
        distanciaItem.setText(dir.getDistancia());

        ImageView imagen = view.findViewById(R.id.imageView);
        imagen.setImageDrawable(dir.getIcono());

        return view;
    }
}