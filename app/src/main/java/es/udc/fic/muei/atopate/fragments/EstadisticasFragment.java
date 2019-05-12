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

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

import es.udc.fic.muei.atopate.R;
import es.udc.fic.muei.atopate.activities.HomeActivity;
import es.udc.fic.muei.atopate.db.TrayectoService;
import es.udc.fic.muei.atopate.db.model.DatosOBD;
import es.udc.fic.muei.atopate.db.model.Trayecto;
import es.udc.fic.muei.atopate.entities.itemHistorialEntity;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class EstadisticasFragment extends Fragment {

    private static final String TAG = EstadisticasFragment.class.getSimpleName();
    private TrayectoService trayectoService;
    List<Trayecto> trayectos = new ArrayList();


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

        trayectoService = new TrayectoService(getContext());
        trayectos.addAll(trayectoService.getAllToday());

        configureSpinner(viewinflated);
        configureCharts(viewinflated);

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
                trayectos.removeAll(trayectos);
                switch(position) {
                    case 0:
                        trayectos.addAll(trayectoService.getAllToday());
                        break;
                    case 1:
                        trayectos.addAll(trayectoService.getAllYesterday());
                        break;
                    case 2:
                        trayectos.addAll(trayectoService.getAllLastWeek());
                        break;
                }
                configureCharts(vista);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void configureCharts(View vista) {

        GraphView speedGraph = vista.findViewById(R.id.graph2);
        GraphView fuelGraph = vista.findViewById(R.id.graph3);
        PieChartView pieChartView = vista.findViewById(R.id.chart);

        speedGraph.removeAllSeries();
        fuelGraph.removeAllSeries();

        BarGraphSeries<DataPoint> speedSeries = new BarGraphSeries<>(new DataPoint[]{});
        BarGraphSeries<DataPoint> fuelSeries = new BarGraphSeries<>(new DataPoint[]{});

        Double velocidadSuma = 0.0;
        Double velocidadMax = 0.0;
        Double fuelSuma = 0.0;
        Double fuelMax = 0.0;
        Double fuelMin = Double.MAX_VALUE;
        Double rpmSuma = 0.0;
        Double fuelAvg = 0.0;
        Double velocidadAvg = 0.0;
        Double rpmAvg = 0.0;

        int numDatos = 0;

        if (!trayectos.isEmpty()) {
            for (int i = 0; i < trayectos.size(); i++) {
                List<DatosOBD> listDatos = trayectos.get(i).datosOBD;
                if (!listDatos.isEmpty()) {
                    for (int j = 0; j < listDatos.size(); j++) {
                        DatosOBD dato = listDatos.get(j);

                        // VELOCIDAD
                        if (dato.speed > velocidadMax) {
                            velocidadMax = dato.speed;
                        }
                        velocidadSuma += dato.speed;

                        // FUEL
                        if (dato.fuelLevel > fuelMax) {
                            fuelMax = dato.fuelLevel;
                        }
                        if (dato.fuelLevel < fuelMin) {
                            fuelMin = dato.fuelLevel;
                        }
                        fuelSuma += dato.fuelLevel;

                        // RPM
                        rpmSuma += dato.rpm;

                        numDatos++;
                    }
                }
            }
            fuelAvg = fuelSuma / numDatos;
            velocidadAvg = fuelSuma / numDatos;
            rpmAvg = rpmSuma / numDatos;

        } else {
            fuelMin = 0.0;
        }


        DataPoint[] dataPointsSpeed = new DataPoint[2];
        dataPointsSpeed[0] = new DataPoint(1, velocidadMax);
        dataPointsSpeed[1] = new DataPoint(2, velocidadAvg);
        DataPoint[] dataPointsFuel = new DataPoint[3];
        dataPointsFuel[0] = new DataPoint(1, fuelMax);
        dataPointsFuel[1] = new DataPoint(2, fuelAvg);
        dataPointsFuel[2] = new DataPoint(3, fuelMin);

        speedSeries = new BarGraphSeries<>(dataPointsSpeed);
        speedSeries.setColor(Color.RED);
        speedSeries.setSpacing(25);
        speedSeries.setDrawValuesOnTop(true);
        speedSeries.setValuesOnTopSize(40);
        speedSeries.setValuesOnTopColor(Color.RED);
        fuelSeries = new BarGraphSeries<>(dataPointsFuel);
        fuelSeries.setColor(Color.BLUE);
        fuelSeries.setSpacing(25);
        fuelSeries.setDrawValuesOnTop(true);
        fuelSeries.setValuesOnTopSize(40);
        fuelSeries.setValuesOnTopColor(Color.BLUE);

        speedGraph.addSeries(speedSeries);
        speedGraph.getViewport().setMinX(0);
        speedGraph.getViewport().setMaxX(4);
        speedGraph.getViewport().setMinY(0);
        speedGraph.getViewport().setMaxY(130);
        speedGraph.setTitle("Velocidad");
        speedGraph.getGridLabelRenderer().setGridColor(Color.rgb(150,150,150));
        speedGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.rgb(150,150,150));
        speedGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.rgb(150,150,150));
        speedGraph.getLegendRenderer().setBackgroundColor(Color.rgb(200,200,200));

        speedGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    String stringValue = "";
                    Double doubleValue = new Double(value);
                    switch(doubleValue.toString()) {
                        case "1.0":
                            stringValue = "MAX";
                            break;
                        case "2.0":
                            stringValue = "AVG";
                            break;
                    }
                    return stringValue;
                }
                return String.valueOf(value);
            }
        });

        fuelGraph.addSeries(fuelSeries);
        fuelGraph.getViewport().setMinX(0);
        fuelGraph.getViewport().setMaxX(4);
        fuelGraph.getViewport().setMinY(0);
        fuelGraph.getViewport().setMaxY(130);
        fuelGraph.setTitle("Combustible");
        fuelGraph.getGridLabelRenderer().setGridColor(Color.rgb(150,150,150));
        fuelGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.rgb(150,150,150));
        fuelGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.rgb(150,150,150));
        fuelGraph.getLegendRenderer().setBackgroundColor(Color.rgb(200,200,200));

        fuelGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    String stringValue = "";
                    Double doubleValue = new Double(value);
                    switch(doubleValue.toString()) {
                        case "1.0":
                            stringValue = "MAX";
                            break;
                        case "2.0":
                            stringValue = "AVG";
                            break;
                        case "3.0":
                            stringValue = "MIN";
                            break;
                    }
                    return stringValue;
                }
                return String.valueOf(value);
            }
        });

        List<SliceValue> pieData = new ArrayList<>();
        Double notPaintedRPM = 2.75;
        pieData.add(new SliceValue(notPaintedRPM.floatValue(), Color.TRANSPARENT).setLabel(""));

        if (rpmAvg != 0.0) {
            Double paintedRPM = rpmAvg;
            Double rpmTo7 = 7 - paintedRPM;
            Double rpmLimitValue = 1.0;

            if (paintedRPM < 4) {
                pieData.add(new SliceValue(paintedRPM.floatValue(), Color.GREEN));
            } else if (paintedRPM < 8) {
                pieData.add(new SliceValue(paintedRPM.floatValue(), Color.rgb(255,165,0)));
            } else {
                pieData.add(new SliceValue(paintedRPM.floatValue(), Color.RED));
            }
            if (paintedRPM < 7) {
                pieData.add(new SliceValue(rpmTo7.floatValue(), Color.LTGRAY).setLabel(""));
            }
            if (paintedRPM < 8) {
                pieData.add(new SliceValue(rpmLimitValue.floatValue(), Color.RED).setLabel("8"));
            }

        } else {
            pieData.add(new SliceValue(7, Color.LTGRAY).setLabel(""));
            pieData.add(new SliceValue(1, Color.RED).setLabel("8"));
        }

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(14);
        pieChartData.setHasCenterCircle(true);
        pieChartData.setCenterText1("RPM").setCenterText1FontSize(16);
        pieChartView.setPieChartData(pieChartData);
        pieChartView.setChartRotationEnabled(false);
    }
}
