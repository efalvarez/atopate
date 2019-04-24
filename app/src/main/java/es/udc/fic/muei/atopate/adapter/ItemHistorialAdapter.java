package es.udc.fic.muei.atopate.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

import es.udc.fic.muei.atopate.R;
import es.udc.fic.muei.atopate.entities.itemHistorialEntity;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;


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

        //Detalles
        TextView horasItem =  view.findViewById(R.id.horas);
        horasItem.setText(dir.getHoras());

        configureCharts(view);
        configureMaps(view, null);

        return view;
    }

    private void configureCharts(View vista) {
        // Charts

        GraphView graph1 = vista.findViewById(R.id.graph1);
        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3)
        });
        graph1.addSeries(series1);

        GraphView graph2 = vista.findViewById(R.id.graph2);
        BarGraphSeries<DataPoint> series2 = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(2, 5),
                new DataPoint(4, 3)
        });
        graph2.addSeries(series2);

        PieChartView pieChartView = vista.findViewById(R.id.graph3);
        List<SliceValue> pieData = new ArrayList<>();
        pieData.add(new SliceValue(15, Color.BLUE));
        pieData.add(new SliceValue(25, Color.GRAY));
        pieData.add(new SliceValue(10, Color.RED));
        pieData.add(new SliceValue(60, Color.MAGENTA));

        PieChartData pieChartData = new PieChartData(pieData);

        pieChartData.setHasLabels(true);

        pieChartData.setHasLabels(true).setValueLabelTextSize(14);

        pieChartData.setHasCenterCircle(true);

        pieChartView.setPieChartData(pieChartData);
    }

    private void configureMaps(View vista, Bundle savedInstanceState) {
        //MapView mapaVista = vista.findViewById(R.id.mapView);
        //MapsConfigurer.initializeMap(this.activity, mapaVista, null);
    }
}