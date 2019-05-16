package es.udc.fic.muei.atopate.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sohrab.obd.reader.application.Preferences;
import com.sohrab.obd.reader.obdCommand.ObdCommand;
import com.sohrab.obd.reader.obdCommand.ObdConfiguration;
import com.sohrab.obd.reader.obdCommand.SpeedCommand;
import com.sohrab.obd.reader.obdCommand.fuel.FuelLevelCommand;
import com.sohrab.obd.reader.trip.TripRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import es.udc.fic.muei.atopate.BuildConfig;
import es.udc.fic.muei.atopate.R;
import es.udc.fic.muei.atopate.adapter.AjustesAdapter;
import es.udc.fic.muei.atopate.bluetooth.BluetoothConstants;
import es.udc.fic.muei.atopate.bluetooth.BluetoothReaderService;
import es.udc.fic.muei.atopate.db.TrayectoService;
import es.udc.fic.muei.atopate.db.model.DatosOBD;
import es.udc.fic.muei.atopate.db.model.PuntosTrayecto;
import es.udc.fic.muei.atopate.db.model.Trayecto;
import es.udc.fic.muei.atopate.entities.CustomToast;
import es.udc.fic.muei.atopate.fragments.AjustesFragment;
import es.udc.fic.muei.atopate.fragments.EstadisticasFragment;
import es.udc.fic.muei.atopate.fragments.HistorialFragment;
import es.udc.fic.muei.atopate.fragments.HomeFragment;
import es.udc.fic.muei.atopate.fragments.TrayectoFragment;
import es.udc.fic.muei.atopate.maps.RouteFinder;

import static android.support.constraint.Constraints.TAG;
import static es.udc.fic.muei.atopate.bluetooth.BluetoothConstants.OBD_ACTION_DATA_READ;
import static es.udc.fic.muei.atopate.bluetooth.BluetoothConstants.OBD_ACTION_MESSAGE;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static int MULTIPLE_PERMISSIONS = 1;
    public TrayectoService trayectoService;
    public Trayecto trayecto;
    private int cont = 0;

    private BottomNavigationView bottomNavigationView;
    private String pathFile;
    private Uri capturedImageURI;
    private Bitmap bitMap;


    FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    // Variables que determinan los tiempos de actualización
    public static final long TIME_REQUEST = 30000;
    private static final long TIME_FAST_REQUEST = 15000;

    public boolean isBluetoothConnectionEstablished;

    private BroadcastReceiver bthReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action == null) {
                // en caso de que no venga ningun action, no hacemos nada
                return;
            }

            if (action.equalsIgnoreCase(OBD_ACTION_MESSAGE)) {

                String toastMessage = intent.getExtras().getString(BluetoothConstants.OBD_EXTRA_DATA);
                Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_LONG).show();

            } else if (action.equalsIgnoreCase(OBD_ACTION_DATA_READ)) {

                // LECTURA TRAYECTO

                TripRecord tripRecord = TripRecord.getTripRecode(HomeActivity.this);

                if (trayecto != null) {
                    DatosOBD datosOBD = new DatosOBD();


                    datosOBD.speed = Double.valueOf(tripRecord.getSpeed());

                    Field field = null;
                    try {
                        field = tripRecord.getClass().getDeclaredField("mFuelLevel");
                        field.setAccessible(true);
                        Object value = field.get(tripRecord);

                        if (value != null) {
                            String fuelLevelValue = (String) value;
                            Log.i("CheckThis", "fuelLeve :: " + fuelLevelValue);
                            fuelLevelValue = fuelLevelValue.replaceAll("\\%", "");
                            fuelLevelValue = fuelLevelValue.replaceAll("\\,", "\\.");
                            datosOBD.fuelLevel = Double.valueOf(fuelLevelValue);
                        } else {
                            datosOBD.fuelLevel = null;
                        }

                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }


                    trayectoService.updateTrayectoinfo(datosOBD, trayecto.id);
                }


            } else if (action.equalsIgnoreCase(BluetoothConstants.OBD_ACTION_DISCONNECTED)) {

                // FIN TRAYECTO: Parte del coche (se apago, se desconecto del bluetooth, etc)

                isBluetoothConnectionEstablished = false;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Button bluetoothButton = findViewById(R.id.bluetooth);
                    bluetoothButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
                }

                Trayecto currentTrayecto = trayectoService.getCurrentTrayecto();

                if (currentTrayecto != null && !CollectionUtils.isEmpty(currentTrayecto.datosOBD) && currentTrayecto.esTrayectoActual) {
                    // en caso de que se haya desconectado antes de siquiera iniciar un viaje

                    TripRecord record = TripRecord.getTripRecode(HomeActivity.this);

                    currentTrayecto.horaFin = Calendar.getInstance();
                    currentTrayecto.esTrayectoActual = false;
                    currentTrayecto.distancia = (int) record.getmDistanceTravel();
                    trayectoService.insert(trayecto);
                    trayecto = null;
                }

                //Para las actualizaciones
                try {
                    fusedLocationClient.removeLocationUpdates(locationCallback);
                } catch(NullPointerException npe) {
                    Log.e(TAG, "BroadcastReciver: Sin actualizaciones de localización");
                }

            } else if (action.equalsIgnoreCase(BluetoothConstants.OBD_ACTION_CONNECTED)) {

                // INICIO TRAYECTO: Conexion establecida con el coche. En caso de que ya se hubiera creado
                // un trayecto que estuviera vacio, se procede a actualizarle la hora de inicio

                isBluetoothConnectionEstablished = true;

                Button bluetoothButton = findViewById(R.id.bluetooth);
                ViewCompat.setBackgroundTintList(bluetoothButton, ContextCompat.getColorStateList(getApplicationContext(), android.R.color.holo_blue_bright));


                Trayecto currentTrayecto = trayectoService.getCurrentTrayecto();

                if (currentTrayecto == null) {

                    Calendar hora = Calendar.getInstance();
                    Trayecto trayectoInsercion = new Trayecto("Bluetooth" +
                            Calendar.getInstance().getTime().toLocaleString(), "Casa", hora,
                            null, 23, null, true);

                    trayectoInsercion.datosOBD = new ArrayList<>();
                    trayectoInsercion.id = trayectoService.insert(trayectoInsercion);

                    trayecto = trayectoInsercion;

                } else {

                    currentTrayecto.horaInicio = Calendar.getInstance();
                    trayectoService.insert(currentTrayecto);
                }
                // Carga el provedor de actualizaciones
                startLocationUpdate();
            }

        }
    };

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


    public boolean setFragment(int itemId) {
        Fragment fragmentToSubstitute = HomeFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        boolean validNavigationItemSelected = false;

        switch (itemId) {
            case R.id.navigation_home:
                if (trayecto == null) {
                    trayecto = trayectoService.getLast();
                }

                fragmentToSubstitute = HomeFragment.newInstance();

                validNavigationItemSelected = true;
                break;

            case R.id.navigation_atopate:
                if (trayecto == null) {
                    trayecto = trayectoService.getLast();
                }

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

        getSupportActionBar().setLogo(R.drawable.topo100);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        compruebaPermisos();
        configureBottomNavigation();


        SharedPreferences prefs =
                getSharedPreferences("PreferenciasAtopate", Context.MODE_PRIVATE);
        String theme = prefs.getString("tema", "Claro");

        new AjustesAdapter().setTema(this, theme);

        // configuramos el receiver encargado de recuperar la informacion del coche
        configureODB2Receiver();

        //Funcionamiento del callback del actualizador del mapa
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setFragment(bottomNavigationView.getSelectedItemId());

        SharedPreferences sharedPreferences = getSharedPreferences("PreferenciasAtopate", Context.MODE_PRIVATE);
        boolean doWeComeFromBluetooth = sharedPreferences.getBoolean(BluetoothConstants.PREFERENCE_KEY_GOING_TO_BLUETOOTH, false);

        boolean isOBDDisconnected = !Preferences.get(getApplicationContext()).getIsOBDconnected();

        if (BluetoothAdapter.getDefaultAdapter().isEnabled() && doWeComeFromBluetooth &&
                isOBDDisconnected) {
            // caso de que vengamos de la aplicacion de bluetooth y reestablecemos el estado
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(BluetoothConstants.PREFERENCE_KEY_GOING_TO_BLUETOOTH, false);
            editor.apply();

            isBluetoothConnectionEstablished = false;
            new CustomToast(getApplicationContext(),getString(R.string.obd_reconnect), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        // tal como nos lo indican en la documentacion de la libreria usada, es necesario destruir
        // el receiver de la llamada al OBD2 y parar cualquier servicio relaciondo con el mismo
        unregisterReceiver(bthReceiver);
        stopService(new Intent(this, BluetoothReaderService.class));

        Preferences.get(this).setServiceRunningStatus(false);
    }


    /**
     * Se configuran aquellos datos que nos interesan recuperar del coche y procedemos a establecer
     * el receiver encargado de procesar estos datos.
     */
    private void configureODB2Receiver() {
        // establecemos los comandos que procederemos a leer del coche
        ArrayList<ObdCommand> obdComands = new ArrayList<>(Arrays.asList(
                new SpeedCommand(),
                new FuelLevelCommand()
        ));

        ObdConfiguration.setmObdCommands(this, obdComands);


        // lanzamos el intent correspondiente a la actividad
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothConstants.OBD_ACTION_MESSAGE);
        intentFilter.addAction(BluetoothConstants.OBD_ACTION_DATA_READ);
        intentFilter.addAction(BluetoothConstants.OBD_ACTION_DISCONNECTED);
        intentFilter.addAction(BluetoothConstants.OBD_ACTION_CONNECTED);

        // establecemos el listener que procesara los resultados obtenidos del coche
        registerReceiver(bthReceiver, intentFilter);

        isBluetoothConnectionEstablished = Preferences.get(getApplicationContext()).getIsOBDconnected();

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

                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        MULTIPLE_PERMISSIONS);
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

    public Bitmap getBipMap() { return bitMap; }
    public void setCurrentPhotoPath(String path) {
        pathFile = path;
    }

    // AJUSTES CLICK LISTENER
    public void onAddTrayectoClick(View view) {
        TrayectoService trayectoService = new TrayectoService(this);
        Calendar inicio = Calendar.getInstance();
        Calendar fin = Calendar.getInstance();
        inicio.add(Calendar.HOUR, -1);
        Trayecto t = new Trayecto("Lugo", "A Coruña", inicio, fin, 98, "atopate"+inicio.getTimeInMillis(), false);
        t.puntosTrayecto = new PuntosTrayecto();
        List<DatosOBD> listDatos = new ArrayList<DatosOBD>();

        for(int i=0;i<20;i++) {
            DatosOBD d = new DatosOBD();
            d.trayectoId = t.id;
            d.fuelLevel = new Double(i + cont);
            d.rpm = new Double(6);
            d.speed = new Double(i+2 + cont);
            listDatos.add(d);
        }
        cont++;

        t.datosOBD = listDatos;
        t.puntosTrayecto.coordenadas = RouteFinder.getRoute("Lugo", "A Coruña");
        trayectoService.insert(t);
        CustomToast toast = new CustomToast(this, "Trayecto de prueba", Toast.LENGTH_LONG);
        toast.show();
    }

    public void onEliminarClick(View view) {

        HomeActivity activity = this;
        new AlertDialog.Builder(this)
                .setTitle("Estás seguro?")
                .setMessage("Si aceptas, perderás todos tus trayectos.")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("MainActivity", "Sending atomic bombs to Jupiter");
                        trayectoService.delete();
                        CustomToast toast = new CustomToast(activity, "Registros eliminados", Toast.LENGTH_LONG);
                        toast.show();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("MainActivity", "Aborting mission...");
                    }
                })
                .show();


    }

    public void onExportarClick(View view) throws IOException {

        List<Trayecto> trayectos = trayectoService.getAllTrayectos();

        if (trayectos.size() > 0) {
            Type listType = new TypeToken<List<Trayecto>>() {}.getType();
            String json = (new Gson()).toJson(trayectos, listType);

            File file = File.createTempFile("atopate", ".bak");

            String path = "file:" + file.getAbsolutePath();

            try {
                FileOutputStream fop = new FileOutputStream(file);
                byte[] contentInBytes = json.getBytes();
                fop.write(contentInBytes);
                fop.flush();
                fop.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            Uri uri = FileProvider.getUriForFile(HomeActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Exportar Backup"));
            CustomToast toast = new CustomToast(this, "Trayectos exportados", Toast.LENGTH_LONG);
            toast.show();
        } else {
            CustomToast toast = new CustomToast(this, "Ningún trayecto registrado", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /**
     * Listener del boton de activacion del bluetooth. Al ser pulsado, este tendra que iniciar el evento
     * necesario para establecer la conexion con el OBDII y empezar a recuperar los valores que han
     * sido establecidos durante la fase del onCreate de esta actividad.
     *
     * @param view Vista actual
     */
    public void onActivateBluetooth(View view) {

        //start service which will execute in background for connecting and execute command until you stop

        Button bluetoothButton = findViewById(R.id.bluetooth);

        if (isBluetoothConnectionEstablished) {

            bluetoothButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));

            Intent stopBluetoothService = new Intent(this, BluetoothReaderService.class);
            stopService(stopBluetoothService);

            Trayecto currentTrayecto = trayectoService.getCurrentTrayecto();

            if (currentTrayecto != null && !CollectionUtils.isEmpty(currentTrayecto.datosOBD) && currentTrayecto.esTrayectoActual) {
                // FIN DE TRAYECTO: Por parte del usuario

                TripRecord record = TripRecord.getTripRecode(HomeActivity.this);

                currentTrayecto.distancia = (int) record.getmDistanceTravel();

                currentTrayecto.horaFin = Calendar.getInstance();
                currentTrayecto.esTrayectoActual = false;
                trayectoService.insert(currentTrayecto);
                trayecto = null;
            }

        } else {

            ViewCompat.setBackgroundTintList(bluetoothButton, ContextCompat.getColorStateList(this, android.R.color.holo_orange_light));

            Intent startBluetoothService = new Intent(this, BluetoothReaderService.class);
            startService(startBluetoothService);
        }

        // TODO Esta variable no debería cambiar, en el caso de que no encuentre dispositivos reconocibles.
        //  (y si lo hace, entonces no pintar el botón)
        isBluetoothConnectionEstablished = !isBluetoothConnectionEstablished;

    }


    // LOCATIONS UPDATES

    protected LocationRequest getLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        // Intervalos en milisegundos
        locationRequest.setInterval(TIME_REQUEST);
        locationRequest.setFastestInterval(TIME_FAST_REQUEST);
        // Exactitud del mapa
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;
    }

    public void startLocationUpdate() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location actual : locationResult.getLocations()) {
                    LatLng latLngActual = new LatLng(actual.getLatitude(), actual.getLongitude());
                    try {
                        trayecto = trayectoService.getCurrentTrayecto();
                        trayecto.puntosTrayecto.coordenadas.add(latLngActual);
                        trayectoService.insert(trayecto);
                    } catch(NullPointerException npe) {
                        Log.d(TAG, "startLocationUpdate/onLocationResult: Sin trayecto",npe);
                        break;
                    }
                }
            }
        };

        try {
            fusedLocationClient.requestLocationUpdates(getLocationRequest(),
                    locationCallback,
                    null /* Looper */);
        } catch (SecurityException se) {
            se.printStackTrace();
        }
    }
}
