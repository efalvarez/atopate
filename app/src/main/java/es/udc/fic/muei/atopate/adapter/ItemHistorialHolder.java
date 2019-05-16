package es.udc.fic.muei.atopate.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import es.udc.fic.muei.atopate.R;
import es.udc.fic.muei.atopate.activities.HomeActivity;
import es.udc.fic.muei.atopate.db.model.Trayecto;
import es.udc.fic.muei.atopate.entities.itemHistorialEntity;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class ItemHistorialHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final ImageView foto;
    private final TextView tiempo;
    private final TextView lugar;
    private final TextView distancia;
    private final TextView horas;
    private final ImageView foto2;

    private final CardView root;

    private LinearLayout detallesItem;

    private itemHistorialEntity item;
    private Context context;
    private View view;
    private Activity activity;
    private List<itemHistorialEntity> items;

    public ItemHistorialHolder(Context context, View itemView, Activity activity, List<itemHistorialEntity> items) {

        super(itemView);
        this.view = itemView;
        this.items = items;

        this.context = context;
        this.activity = activity;

        this.foto = (ImageView) itemView.findViewById(R.id.imageView);
        this.tiempo = (TextView) itemView.findViewById(R.id.tiempoItem);
        this.lugar = (TextView) itemView.findViewById(R.id.lugarItem);
        this.distancia = (TextView) itemView.findViewById(R.id.distanciaItem);
        this.foto2 = (ImageView) itemView.findViewById(R.id.image);
        this.horas = (TextView) itemView.findViewById(R.id.duracionItem);

        this.detallesItem = (LinearLayout) itemView.findViewById(R.id.detallesItem);
        this.root = (CardView) itemView;

        itemView.setOnClickListener(this);
    }

    public void bindItem(itemHistorialEntity item) {

        this.item = item;
        this.tiempo.setText(item.getTiempo());
        this.lugar.setText(item.getLugarOrigen() + " - " + item.getLugarDestino());
        this.distancia.setText(item.getDistancia());
        this.horas.setText(item.getHoras());
        try {
            setPic(item.getIcono(), this.foto, 0);
            setPic(item.getIcono(), this.foto2, 1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        GraphView graph1 = view.findViewById(R.id.graph2);
        graph1.removeAllSeries();
        PieChartView pieChartView = view.findViewById(R.id.chart);
        configureCharts(pieChartView, item, graph1);
    }

    private void configureCharts(PieChartView pieChart, itemHistorialEntity item, GraphView graph) {

        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(new DataPoint[]{});
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[]{});
        Trayecto trayecto = null;
        HomeActivity homeActivity = (HomeActivity) activity;
        if (item != null && item.getTrayectoId() != null) {
            trayecto = homeActivity.trayectoService.getTrayectoById(item.getTrayectoId());
        }

        if (trayecto != null && !trayecto.datosOBD.isEmpty()) {
            DataPoint[] dataPoints = new DataPoint[trayecto.datosOBD.size()+1];
            DataPoint[] dataPointsFuel = new DataPoint[trayecto.datosOBD.size()+1];
            dataPoints[0] = new DataPoint(0, 0);
            dataPointsFuel[0] = new DataPoint(0, 0);
            for (int i = 0; i < trayecto.datosOBD.size(); i++) {
                dataPoints[i+1] = new DataPoint(i+1, trayecto.datosOBD.get(i).speed);
                dataPointsFuel[i+1] = new DataPoint(i+1, trayecto.datosOBD.get(i).fuelLevel);
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

        graph.addSeries(series1);
        graph.addSeries(series2);
        graph.getLegendRenderer().setVisible(Boolean.TRUE);
        graph.getLegendRenderer().setFixedPosition(0,0);
        graph.getLegendRenderer().setBackgroundColor(Color.rgb(200,200,200));
        graph.getGridLabelRenderer().setGridColor(Color.rgb(150,150,150));
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.rgb(150,150,150));
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.rgb(150,150,150));

        List<SliceValue> pieData = new ArrayList<>();
        Double notPaintedRPM = 2.75;
        pieData.add(new SliceValue(notPaintedRPM.floatValue(), Color.TRANSPARENT).setLabel(""));

        if (trayecto != null && !trayecto.datosOBD.isEmpty()) {
            Double avgRPM = 0.0;
            for (int i = 0; i < trayecto.datosOBD.size(); i++) {
                avgRPM += trayecto.datosOBD.get(i).rpm;
            }
            Double paintedRPM = avgRPM / trayecto.datosOBD.size();
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
        pieChartData.setCenterText1("RPM").setCenterText1FontSize(16).setCenterText1Color(Color.GRAY);
        pieChart.setPieChartData(pieChartData);
        pieChart.setChartRotationEnabled(false);
    }

    private void setPic(String imagePath, ImageView imageView, int type) throws FileNotFoundException {
        int targetW = imageView.getWidth(); // Get the dimensions of the View
        int targetH = imageView.getHeight();

        if (targetW == 0 || targetH == 0) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            targetH =  Math.round(80 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
            targetW =  Math.round(85 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth; // Get the dimensions of the bitmap
        int photoH = bmOptions.outHeight;
// Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
// Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        if (type == 0) {
            imageView.setImageBitmap(bitmap);
        } else {
            if (bitmap != null) {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setVisibility(View.GONE);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        if (this.item != null) {

            toggleVisibility();
//            if (detallesItem.getVisibility() == View.VISIBLE) {
//                detallesItem.setVisibility(View.GONE);
//            } else {
//                detallesItem.setVisibility(View.VISIBLE);
//            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void toggleVisibility() {
        if (detallesItem.getVisibility() == View.GONE || detallesItem.getVisibility() == View.INVISIBLE) {
            //btSeeMore.animate().rotation(180f).start()
            TransitionManager.beginDelayedTransition((RecyclerView)root.getParent());
            detallesItem.setVisibility(View.VISIBLE);
        }
        else {
            //btSeeMore.animate().rotation(0f).start()

            TransitionManager.beginDelayedTransition((RecyclerView)root.getParent());
            detallesItem.setVisibility(View.GONE);
        }
    }
}