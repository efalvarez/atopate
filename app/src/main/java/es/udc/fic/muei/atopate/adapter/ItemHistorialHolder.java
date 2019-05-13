package es.udc.fic.muei.atopate.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import es.udc.fic.muei.atopate.R;
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

    private final GraphView graph1;
    private final GraphView graph2;
    private final PieChartView graph3;

    private final CardView root;

    private LinearLayout detallesItem;

    private itemHistorialEntity item;
    private Context context;

    public ItemHistorialHolder(Context context, View itemView) {

        super(itemView);

        this.context = context;

        this.foto = (ImageView) itemView.findViewById(R.id.imageView);
        this.tiempo = (TextView) itemView.findViewById(R.id.tiempoItem);
        this.lugar = (TextView) itemView.findViewById(R.id.lugarItem);
        this.distancia = (TextView) itemView.findViewById(R.id.distanciaItem);
        this.foto2 = (ImageView) itemView.findViewById(R.id.image);
        this.horas = (TextView) itemView.findViewById(R.id.horas);

        this.graph1 = (GraphView) itemView.findViewById(R.id.graph1);
        this.graph2 = (GraphView) itemView.findViewById(R.id.graph2);
        this.graph3 = (PieChartView) itemView.findViewById(R.id.graph3);


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
            setPic(item.getIcono(), this.foto);
            setPic(item.getIcono(), this.foto2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        configureCharts();
    }

    private void configureCharts() {
        // Charts

        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3)
        });
        graph1.addSeries(series1);

        BarGraphSeries<DataPoint> series2 = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(2, 5),
                new DataPoint(4, 3)
        });
        graph2.addSeries(series2);

        List<SliceValue> pieData = new ArrayList<>();
        pieData.add(new SliceValue(15, Color.BLUE));
        pieData.add(new SliceValue(25, Color.GRAY));
        pieData.add(new SliceValue(10, Color.RED));
        pieData.add(new SliceValue(60, Color.MAGENTA));

        PieChartData pieChartData = new PieChartData(pieData);

        pieChartData.setHasLabels(true);

        pieChartData.setHasLabels(true).setValueLabelTextSize(14);

        pieChartData.setHasCenterCircle(true);

        graph3.setPieChartData(pieChartData);
    }

    private void setPic(String imagePath, ImageView imageView) throws FileNotFoundException {
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
        imageView.setImageBitmap(bitmap);
    }

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