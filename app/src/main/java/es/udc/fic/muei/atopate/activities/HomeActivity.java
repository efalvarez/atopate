package es.udc.fic.muei.atopate.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.sohrab.obd.reader.application.Preferences;
import com.sohrab.obd.reader.constants.DefineObdReader;
import com.sohrab.obd.reader.obdCommand.ObdCommand;
import com.sohrab.obd.reader.obdCommand.ObdConfiguration;
import com.sohrab.obd.reader.obdCommand.SpeedCommand;
import com.sohrab.obd.reader.obdCommand.engine.OilTempCommand;
import com.sohrab.obd.reader.obdCommand.fuel.FuelLevelCommand;
import com.sohrab.obd.reader.service.ObdReaderService;
import com.sohrab.obd.reader.trip.TripRecord;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import es.udc.fic.muei.atopate.R;
import es.udc.fic.muei.atopate.adapter.AjustesAdapter;
import es.udc.fic.muei.atopate.db.TrayectoService;
import es.udc.fic.muei.atopate.db.model.PuntosTrayecto;
import es.udc.fic.muei.atopate.db.model.Trayecto;
import es.udc.fic.muei.atopate.entities.CustomToast;
import es.udc.fic.muei.atopate.fragments.AjustesFragment;
import es.udc.fic.muei.atopate.fragments.EstadisticasFragment;
import es.udc.fic.muei.atopate.fragments.HistorialFragment;
import es.udc.fic.muei.atopate.fragments.HomeFragment;
import es.udc.fic.muei.atopate.fragments.TrayectoFragment;
import es.udc.fic.muei.atopate.maps.RouteFinder;

import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_CONNECTION_STATUS_MSG;
import static com.sohrab.obd.reader.constants.DefineObdReader.ACTION_READ_OBD_REAL_TIME_DATA;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static int MULTIPLE_PERMISSIONS = 1;
    public TrayectoService trayectoService;
    public Trayecto trayecto;

    private boolean bluetoothRecordIsActivated = false;

    private BottomNavigationView bottomNavigationView;
    private String pathFile;
    private Uri capturedImageURI;
    private Bitmap bitMap;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            boolean isAlreadyChecked = checkIfItemIsAlreadyChecked(item, bottomNavigationView);

            if (isAlreadyChecked) {
                return true;
            }

            return setFragment(item.getItemId());

        }
    };

    private BroadcastReceiver mObdReaderReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals(ACTION_CONNECTION_STATUS_MSG)) {

                String connectionStatusMsg = intent.getStringExtra(DefineObdReader.INTENT_EXTRA_DATA);


            } else if (action.equals(ACTION_READ_OBD_REAL_TIME_DATA)) {

                TripRecord tripRecord = TripRecord.getTripRecode(HomeActivity.this);

                Log.d("CheckThis", "Speedo: " + tripRecord.getSpeed());
                Log.d("CheckThis", "RPM:" + tripRecord.getEngineRpm());
            }

        }
    };

    public boolean setFragment(int itemId) {
        Fragment fragmentToSubstitute = HomeFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        boolean validNavigationItemSelected = false;

        switch (itemId) {
            case R.id.navigation_home:
                trayecto = trayectoService.getLast();

                fragmentToSubstitute = HomeFragment.newInstance();

                validNavigationItemSelected = true;
                break;

            case R.id.navigation_atopate:
                trayecto = trayectoService.getLast();

                fragmentToSubstitute = TrayectoFragment.newInstance();

                validNavigationItemSelected = true;
                break;

            case R.id.navigation_resumen:

                fragmentToSubstitute = EstadisticasFragment.newInstance();

                validNavigationItemSelected = true;
                break;

            case R.id.navigation_historico:

                fragmentToSubstitute = HistorialFragment.getInstance();

                validNavigationItemSelected = true;
                break;

            case R.id.navigation_ajustes:

                fragmentToSubstitute = AjustesFragment.newInstance();

                validNavigationItemSelected = true;
                break;
        }

        transaction.replace(R.id.general_fragment_container, fragmentToSubstitute);
        transaction.commit();

        return validNavigationItemSelected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trayectoService = new TrayectoService(this);
        setContentView(R.layout.activity_home);
        try {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (java.lang.NullPointerException e) {
            Log.d(TAG, "onCreate: ", e);
        }

        getSupportActionBar().setIcon(R.drawable.topo45);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        compruebaPermisos();
        configureBottomNavigation();


        SharedPreferences prefs =
                getSharedPreferences("PreferenciasAtopate", Context.MODE_PRIVATE);
        String theme = prefs.getString("tema", "Claro");

        new AjustesAdapter().setTema(this, theme);


        List<ObdCommand> obdComands = Arrays.asList(
                new SpeedCommand(),
                new OilTempCommand(),
                new FuelLevelCommand()
        );

        ObdConfiguration.setmObdCommands(this, null);
//
//         set gas price per litre so that gas cost can calculated. Default is 7 $/l
        float gasPrice = 7; // per litre, you should initialize according to your requirement.
        Preferences.get(this).setGasPrice(gasPrice);
//        /**
//         * Register receiver with some action related to OBD connection status
//         */
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_READ_OBD_REAL_TIME_DATA);
        intentFilter.addAction(ACTION_CONNECTION_STATUS_MSG);
        registerReceiver(mObdReaderReceiver, intentFilter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        setFragment(bottomNavigationView.getSelectedItemId());
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        unregisterReceiver(mObdReaderReceiver);
//        stop service
        stopService(new Intent(this, ObdReaderService.class));
//         This will stop background thread if any running immediately.
        Preferences.get(this).setServiceRunningStatus(false);
    }

    private boolean checkIfItemIsAlreadyChecked(MenuItem checkedItem, BottomNavigationView navigationView) {

        Menu menu = navigationView.getMenu();

        // recorremos los items del menu en busca del que ha sido pulsado para ver si ya estaba pulsado
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);

            boolean sameId = checkedItem.getItemId() == menuItem.getItemId();

            if (sameId && menuItem.isChecked()) {
                return true;
            }
        }

        return false;

    }

    private void configureBottomNavigation() {
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    private void compruebaPermisos() {
        if (!tienePermiso(Manifest.permission.CAMERA)
                && !tienePermiso(Manifest.permission.ACCESS_FINE_LOCATION)
                && !tienePermiso(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && !tienePermiso(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, Manifest.permission.CAMERA)) {
                CustomToast toast = new CustomToast(this, "Se requiere aceptes los permisos para continuar", Toast.LENGTH_LONG);
                toast.show();
            } else {
                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        MULTIPLE_PERMISSIONS);
            }
        }
    }

    public boolean tienePermiso(String service) {
        return ContextCompat.checkSelfPermission(HomeActivity.this, service) == PackageManager.PERMISSION_GRANTED;
    }

    // HOME FRAGMENT CLICK LISTENERS
    public void onAtopateClick(View view) {
        bottomNavigationView.setSelectedItemId(R.id.navigation_atopate);
    }

    public void onCompartirClick(View view) {
        if (trayecto != null && trayecto.puntosTrayecto != null) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            LatLng ubicacion = trayecto.puntosTrayecto.coordenadas.get(trayecto.puntosTrayecto.coordenadas.size() - 1);
            String enlaceGoogleMaps = "http://maps.google.com/maps?q=" + ubicacion.latitude + "," + ubicacion.longitude;
            sendIntent.putExtra(Intent.EXTRA_TEXT, "ATÓPATE - Ubicación del aparcamiento: " + enlaceGoogleMaps);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Compartir ubicación"));
        } else {
            CustomToast toast = new CustomToast(this, "Ubicación del aparcamiento no disponible", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void setBitMap(Bitmap bitmap) {
        bitMap = bitmap;
    }

    public Uri getCapturedImageURI() {
        return capturedImageURI;
    }

    public void setCapturedImageURI(Uri fileUri) {
        capturedImageURI = fileUri;
    }

    public String getCurrentPhotoPath() {
        return pathFile;
    }

    public void setCurrentPhotoPath(String path) {
        pathFile = path;
    }

    public Bitmap getBipMap() {
        return bitMap;
    }

    // AJUSTES CLICK LISTENER
    public void onAddTrayectoClick(View view) {
        TrayectoService trayectoService = new TrayectoService(this);
        Calendar inicio = Calendar.getInstance();
        Calendar fin = Calendar.getInstance();
        inicio.add(Calendar.HOUR, -1);
        Trayecto t = new Trayecto("Lugo", "A Coruña", inicio, fin, 98, "pathfoto");
        t.puntosTrayecto = new PuntosTrayecto();
        t.puntosTrayecto.coordenadas = RouteFinder.getRoute("Lugo", "A Coruña");
        trayectoService.insert(t);

        CustomToast toast = new CustomToast(this, "Trayecto de prueba añadido", Toast.LENGTH_LONG);
        toast.show();

    }

    public void onActivateBluetooth(View view) {

        //start service which will execute in background for connecting and execute command until you stop

        if (bluetoothRecordIsActivated) {
            stopService(new Intent(this, ObdReaderService.class));
        } else {
            startService(new Intent(this, ObdReaderService.class));
        }

        bluetoothRecordIsActivated = !bluetoothRecordIsActivated;
    }

}
