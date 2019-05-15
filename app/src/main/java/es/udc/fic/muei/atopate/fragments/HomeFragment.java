package es.udc.fic.muei.atopate.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.udc.fic.muei.atopate.R;
import es.udc.fic.muei.atopate.activities.HomeActivity;
import es.udc.fic.muei.atopate.db.model.Trayecto;
import es.udc.fic.muei.atopate.entities.CustomToast;
import es.udc.fic.muei.atopate.entities.itemHistorialEntity;
import es.udc.fic.muei.atopate.maps.MapsConfigurer;
import es.udc.fic.muei.atopate.maps.RouteFinder;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements OnMapReadyCallback {


    private MapView mapaVista;

    static final int REQUEST_TAKE_PHOTO = 1;

    private ImageView image;

    private String pictureFilePath;
    private static final String TAG = HomeActivity.class.getSimpleName();
    private int contador = 0;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private float screen_width = new Float(0);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screen_width = metrics.widthPixels;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        contador = 0;

        View viewinflated = inflater.inflate(R.layout.fragment_home, container, false);

        image = viewinflated.findViewById(R.id.imageView);

        HomeActivity activity = (HomeActivity) getActivity();

        final View viewHome = viewinflated.findViewById(R.id.constraintHome);

        final TextView estadisticasButton = viewinflated.findViewById(R.id.botonEstadisticas);
        ConstraintLayout estadisticas = viewinflated.findViewById(R.id.inicioEstadisticas);
        estadisticasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (estadisticas.getVisibility() == View.GONE || estadisticas.getVisibility() == View.INVISIBLE) {
                    //btSeeMore.animate().rotation(180f).start()
                    TransitionManager.beginDelayedTransition((ConstraintLayout)viewHome);
                    estadisticas.setVisibility(View.VISIBLE);
                }
                else {
                    //btSeeMore.animate().rotation(0f).start()

                    TransitionManager.beginDelayedTransition((ConstraintLayout)viewHome);
                    estadisticas.setVisibility(View.GONE);
                }
            }
        });

        final TextView fotoButton = viewinflated.findViewById(R.id.botonFoto);
        ConstraintLayout foto = viewinflated.findViewById(R.id.inicioFoto);
        fotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (foto.getVisibility() == View.GONE || foto.getVisibility() == View.INVISIBLE) {
                    //btSeeMore.animate().rotation(180f).start()
                    TransitionManager.beginDelayedTransition((ConstraintLayout)viewHome);
                    foto.setVisibility(View.VISIBLE);
                }
                else {
                    //btSeeMore.animate().rotation(0f).start()

                    TransitionManager.beginDelayedTransition((ConstraintLayout)viewHome);
                    foto.setVisibility(View.GONE);
                }
            }
        });

        final Button captureButton = viewinflated.findViewById(R.id.photo);
        captureButton.setOnClickListener(capture);
        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) && captureButton != null) {
            captureButton.setEnabled(false);
        }

        if (activity.getBipMap() != null && image != null) {

            image.setImageBitmap(activity.getBipMap());
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (contador == 0 || contador%2 == 0) {
                        captureButton.setEnabled(false);
                    } else {
                        captureButton.setEnabled(true);
                    }

                    float scale = screen_width / view.getWidth();
                    if (view.getScaleX() == 1) {
                        view.setScaleY(scale);
                        view.setScaleX(scale);
                    } else {
                        view.setScaleY(1);
                        view.setScaleX(1);
                    }
                    contador++;
                }
            });
        }


        configureCharts(viewinflated);
        configureMaps(viewinflated, savedInstanceState);

        Trayecto trayecto = activity.trayecto;
        if (trayecto != null) {
            itemHistorialEntity item = new itemHistorialEntity(trayecto);

            TextView fecha = viewinflated.findViewById(R.id.fecha);
            fecha.setText(item.getTiempo());

            TextView origenDestino = viewinflated.findViewById(R.id.origenDestino);
            origenDestino.setText(trayecto.origen + " - " + trayecto.destino);

            TextView tiempo = viewinflated.findViewById(R.id.tiempo);
            tiempo.setText(item.getHoras());

            TextView distancia = viewinflated.findViewById(R.id.distancia);
            distancia.setText(item.getDistancia() + " -  37.5 litros" );

            if (trayecto.foto != null) {
                try {
                    setPic(trayecto.foto, image);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        // getInstallationIdentifier();

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

    // serie de metodos reimplementados para que el mapa se actualice de acorde al estado de la tarea

    @Override
    public void onResume() {
        mapaVista.onResume();
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapaVista.onStart();
    }

    @Override
    public void onStop() {
        mapaVista.onStop();
        super.onStop();
    }

    @Override
    public void onPause() {
        mapaVista.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapaVista.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapaVista.onLowMemory();
        super.onLowMemory();
    }

    private void configureMaps(View vista, Bundle savedInstanceState) {
        mapaVista = vista.findViewById(R.id.mapView);
        MapsConfigurer.initializeMap(getActivity(), mapaVista, savedInstanceState, this);
    }

    private void configureCharts(View vista) {

        GraphView graph1 = vista.findViewById(R.id.graph1);

        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3)
        });

        graph1.addSeries(series1);

        GraphView graph2 = vista.findViewById(R.id.graph2);
        HomeActivity activity = (HomeActivity) getActivity();

        BarGraphSeries<DataPoint> series2 = new BarGraphSeries<>();

        if (activity.trayecto != null && !activity.trayecto.datosOBD.isEmpty()) {
            DataPoint[] dataPoints = new DataPoint[activity.trayecto.datosOBD.size()+1];
            dataPoints[0] = new DataPoint(0, 0);
            for (int i = 0; i < activity.trayecto.datosOBD.size(); i++) {
                dataPoints[i+1] = new DataPoint(i+1, activity.trayecto.datosOBD.get(i).speed);
            }
            series2 = new BarGraphSeries<>(dataPoints);
        } else {
            series2 = new BarGraphSeries<>(new DataPoint[]{
                    new DataPoint(0,0),
            });
        }

        graph2.addSeries(series2);

        PieChartView pieChartView = vista.findViewById(R.id.chart);
        List<SliceValue> pieData = new ArrayList<>();
        pieData.add(new SliceValue(15, Color.BLUE));
        pieData.add(new SliceValue(25, Color.GREEN));
        pieData.add(new SliceValue(10, Color.RED));
        pieData.add(new SliceValue(60, Color.YELLOW));

        PieChartData pieChartData = new PieChartData(pieData);

        pieChartData.setHasLabels(true);

        pieChartData.setHasLabels(true).setValueLabelTextSize(14);

        pieChartData.setHasCenterCircle(true);


        pieChartView.setPieChartData(pieChartData);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
// Ensure that there's a camera activity to handle the intent
        HomeActivity activity = (HomeActivity) getActivity();
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try { // Create the File where the photo should go
                photoFile = createImageFile();
            } catch (IOException ex) {// Error occurred while creating the File
                CustomToast toast = new CustomToast(getContext(), "No es posible tomar fotos", Toast.LENGTH_LONG);
                toast.show();
                Log.e(TAG, "EXCEPCION: " + ex.toString());
                return;
            }
            if (photoFile != null) {
                //Uri fileUri = Uri.fromFile(photoFile);
                Uri fileUri = FileProvider.getUriForFile(getContext(),
                        getContext().getApplicationContext().getPackageName() + ".provider", photoFile);
                activity.setCapturedImageURI(fileUri);
                activity.setCurrentPhotoPath(photoFile.getAbsolutePath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        activity.getCapturedImageURI());
                HomeFragment homeFragment = this;
                homeFragment.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
// Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new
                Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // File outputDir = getContext().getCacheDir();
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        Log.d(TAG, "valor de imageFileName: " + imageFileName);
        Log.d(TAG, "valor de storageDir: " + storageDir);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        // Save a file: path for use with ACTION_VIEW intents
        pictureFilePath = image.getAbsolutePath();
        HomeActivity activity = (HomeActivity) getActivity();
        //activity.setCurrentPhotoPath("file:" + pictureFilePath);
        activity.setCurrentPhotoPath(pictureFilePath);
        Log.d(TAG, "valor de picture: " + pictureFilePath);

        return image;
    }

    private View.OnClickListener capture = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                if (((HomeActivity) getActivity()).tienePermiso(Manifest.permission.CAMERA)) {
                    dispatchTakePictureIntent();
                }
                else {
                    CustomToast toast = new CustomToast(getActivity(), "No consediste permisos de uso de la cámara", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    };

    private void addToGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        HomeActivity activity = (HomeActivity) getActivity();
        File f = new File(activity.getCurrentPhotoPath());
        Uri picUri = Uri.fromFile(f);
        galleryIntent.setData(picUri);
        this.getActivity().sendBroadcast(galleryIntent);
    }

    private void setPic(String imagePath, ImageView imageView) throws FileNotFoundException {
        int targetW = imageView.getWidth(); // Get the dimensions of the View
        int targetH = imageView.getHeight();

        if (targetW == 0 || targetH == 0) {
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            targetH =  Math.round(80 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
            targetW =  Math.round(85 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        }

        HomeActivity activity = (HomeActivity) getActivity();

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
        activity.setBitMap(bitmap);
        imageView.setImageBitmap(bitmap);
    }

    static final int REQUEST_PICTURE_CAPTURE = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK) {
            HomeActivity activity = (HomeActivity) getActivity();
            addToGallery();
            try {
                activity.trayecto.foto = activity.getCurrentPhotoPath();
                activity.trayectoService.setFoto(activity.trayecto);
                setPic(activity.getCurrentPhotoPath(), image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap mMap) {

        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        HomeActivity activity = (HomeActivity) getActivity();
        if (activity.trayecto != null && activity.trayecto.puntosTrayecto != null) {
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            int height =  Math.round(150 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

            RouteFinder.drawRoute(activity.trayecto.puntosTrayecto.coordenadas, mMap, getResources().getDisplayMetrics().widthPixels, height);
        }

    }
}
