package es.udc.fic.muei.atopate;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class AdapterItem extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<ItemHistorialActivity> items;

    public AdapterItem (Activity activity, ArrayList<ItemHistorialActivity> items) {
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

    public void addAll(ArrayList<ItemHistorialActivity> itemHistorials) {
        for (int i = 0; i < itemHistorials.size(); i++) {
            items.add(itemHistorials.get(i));
        }
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
            view = inf.inflate(R.layout.activity_item_historial, null);
        }

        ItemHistorialActivity dir = items.get(position);

        TextView tiempoItem = (TextView) view.findViewById(R.id.tiempoItem);
        tiempoItem.setText(dir.getTiempo());

        TextView lugarItem = (TextView) view.findViewById(R.id.lugarItem);
        lugarItem.setText(dir.getLugarOrigen() + "-" + dir.getLugarDestino());

        TextView distanciaItem = (TextView) view.findViewById(R.id.distanciaItem);
        distanciaItem.setText(dir.getDistancia());

        ImageView imagen = (ImageView) view.findViewById(R.id.imageView);
        imagen.setImageDrawable(dir.getIcono());

        return view;
    }
}