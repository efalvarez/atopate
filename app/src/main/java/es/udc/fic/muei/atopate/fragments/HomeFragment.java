package es.udc.fic.muei.atopate.fragments;

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
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.udc.fic.muei.atopate.R;
import es.udc.fic.muei.atopate.activities.HomeActivity;
import es.udc.fic.muei.atopate.maps.MapsConfigurer;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {


    MapView mapaVista;

    static final int REQUEST_TAKE_PHOTO = 1;

    private ImageView image;

    private String pictureFilePath;
    private String deviceIdentifier;
    private static final String TAG = HomeActivity.class.getSimpleName();

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
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

        View viewinflated = inflater.inflate(R.layout.fragment_home, container, false);

        image = viewinflated.findViewById(R.id.imageView);

        Button captureButton = viewinflated.findViewById(R.id.photo);
        captureButton.setOnClickListener(capture);
        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) && captureButton != null) {
            captureButton.setEnabled(false);
        }

        configureCharts(viewinflated);
        configureMaps(viewinflated, savedInstanceState);

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
        MapsConfigurer.initializeMap(getActivity(), mapaVista, savedInstanceState);
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
        BarGraphSeries<DataPoint> series2 = new BarGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3)
        });

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
                Toast.makeText(this.getContext(),
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "EXCEPCION: " + ex.toString());
                return;
            }
            if (photoFile != null) {
                //Uri fileUri = Uri.fromFile(photoFile);
                Uri fileUri = FileProvider.getUriForFile(getContext(),
                        getContext().getApplicationContext().getPackageName() + ".provider", photoFile);
                activity.setCapturedImageURI(fileUri);
                //activity.setCurrentPhotoPath(pictureFilePath);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        activity.getCapturedImageURI());
                activity.setResult(RESULT_OK);
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
        activity.setCurrentPhotoPath("file://"+ pictureFilePath);
        Log.d(TAG, "valor de picture: " + pictureFilePath);

        return image;
    }

    private View.OnClickListener capture = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                dispatchTakePictureIntent();
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

    private void setPic(String imagePath, ImageView imageView) {
        int targetW = imageView.getWidth(); // Get the dimensions of the View
        int targetH = imageView.getHeight();

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

    static final int REQUEST_PICTURE_CAPTURE = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK) {
            HomeActivity activity = (HomeActivity) getActivity();
            addToGallery();
            setPic(activity.getCurrentPhotoPath(), image);
        }
    }
}
