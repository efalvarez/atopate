package es.udc.fic.muei.atopate.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

import es.udc.fic.muei.atopate.R;
import es.udc.fic.muei.atopate.activities.HomeActivity;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class EstadisticasFragment extends Fragment {

    private static final String TAG = EstadisticasFragment.class.getSimpleName();

    public EstadisticasFragment() {
        // Required empty public constructor
    }

    public static EstadisticasFragment newInstance() {
        EstadisticasFragment fragment = new EstadisticasFragment();
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
        View viewinflated = inflater.inflate(R.layout.fragment_estadisticas, container, false);

        configureCharts(viewinflated);
        configureSpinner(viewinflated);

        return viewinflated;
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
        void onFragmentInteraction(Uri uri);
    }

    private void configureSpinner(View vista) {
        String[] arraySpinner = new String[] {
                "Hoy", "Ayer", "Última semana"
        };
        Spinner s = vista.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(vista.getContext(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                /*Object item = adapterView.getItemAtPosition(position);
                if (item != null) {
                    Log.d(TAG, item.toString());
                }*/
                Log.d(TAG, "Selected " + position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void configureCharts(View vista) {

        GraphView graph1 = vista.findViewById(R.id.graph2);
        HomeActivity activity = (HomeActivity) getActivity();

        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(new DataPoint[]{});
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[]{});
        if (activity.trayecto != null && !activity.trayecto.datosOBD.isEmpty()) {
            DataPoint[] dataPoints = new DataPoint[activity.trayecto.datosOBD.size()+1];
            DataPoint[] dataPointsFuel = new DataPoint[activity.trayecto.datosOBD.size()+1];
            dataPoints[0] = new DataPoint(0, 0);
            dataPointsFuel[0] = new DataPoint(0, 0);
            for (int i = 0; i < activity.trayecto.datosOBD.size(); i++) {
                dataPoints[i+1] = new DataPoint(i+1, activity.trayecto.datosOBD.get(i).speed);
                dataPointsFuel[i+1] = new DataPoint(i+1, activity.trayecto.datosOBD.get(i).fuelLevel);
            }
            series1 = new LineGraphSeries<>(dataPoints);
            series1.setColor(Color.RED);
            series1.setThickness(10);
            series1.setTitle("Velocidad");
            series1.setDrawDataPoints(Boolean.TRUE);
            series1.setDataPointsRadius(10);
            series2 = new LineGraphSeries<>(dataPointsFuel);
            series2.setTitle("Combustible");
            series2.setThickness(10);
            series2.setDrawDataPoints(Boolean.TRUE);
            series2.setDataPointsRadius(10);
        } else {
            series1 = new LineGraphSeries<>(new DataPoint[]{
                    new DataPoint(0,0),
            });
            series2 = new LineGraphSeries<>(new DataPoint[]{
                    new DataPoint(0,0),
            });
        }

        graph1.addSeries(series1);
        graph1.addSeries(series2);
        graph1.getLegendRenderer().setVisible(Boolean.TRUE);
        graph1.getLegendRenderer().setFixedPosition(0,0);

        graph1.getGridLabelRenderer().setGridColor(Color.rgb(150,150,150));
        graph1.getGridLabelRenderer().setHorizontalLabelsColor(Color.rgb(150,150,150));
        graph1.getGridLabelRenderer().setVerticalLabelsColor(Color.rgb(150,150,150));
        graph1.getLegendRenderer().setBackgroundColor(Color.rgb(200,200,200));


        PieChartView pieChartView = vista.findViewById(R.id.graph3);
        List<SliceValue> pieData = new ArrayList<>();
        pieData.add(new SliceValue(15, Color.BLUE));
        pieData.add(new SliceValue(25, Color.GREEN));
        pieData.add(new SliceValue(10, Color.RED));

        PieChartData pieChartData = new PieChartData(pieData);

        pieChartData.setHasLabels(true);

        pieChartData.setHasLabels(true).setValueLabelTextSize(14);

        pieChartData.setHasCenterCircle(true);


        pieChartView.setPieChartData(pieChartData);
    }
}
