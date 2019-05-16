package es.udc.fic.muei.atopate.bluetooth;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.sohrab.obd.reader.application.Preferences;
import com.sohrab.obd.reader.enums.ObdProtocols;
import com.sohrab.obd.reader.obdCommand.ObdCommand;
import com.sohrab.obd.reader.obdCommand.ObdConfiguration;
import com.sohrab.obd.reader.obdCommand.control.TroubleCodesCommand;
import com.sohrab.obd.reader.obdCommand.protocol.EchoOffCommand;
import com.sohrab.obd.reader.obdCommand.protocol.LineFeedOffCommand;
import com.sohrab.obd.reader.obdCommand.protocol.ObdResetCommand;
import com.sohrab.obd.reader.obdCommand.protocol.SelectProtocolCommand;
import com.sohrab.obd.reader.obdCommand.protocol.SpacesOffCommand;
import com.sohrab.obd.reader.obdCommand.protocol.TimeoutCommand;
import com.sohrab.obd.reader.trip.TripRecord;
import com.sohrab.obd.reader.utils.L;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import es.udc.fic.muei.atopate.R;

public class BluetoothReaderService extends IntentService {

    private static final String TAG = BluetoothReaderService.class.getSimpleName();

    private static Intent dataReadNotify = new Intent(BluetoothConstants.OBD_ACTION_DATA_READ);

    private IBinder mBinder = new LocalBinder();

    private BluetoothAdapter bthAdapter;
    private BluetoothSocket bthSocket;

    private boolean isFaultCodeRead = true;
    private boolean deviceIsConnected = false;
    private boolean deviceConnectionIsRunning = false;

    public BluetoothReaderService() {
        super(BluetoothConstants.SERVICE_NAME);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        L.i("onHandleIntent" + "Thread is :: " + Thread.currentThread().getId());

        if (intent == null) {
            return;
        }


        if (checkBluetoothActivated()) {
            // en caso de que hayamos pasado todas as comprobacions previas, pasamos a gestionar
            // el proceso

            handleCommand();

        } else {
            // en caso de que non teñamos o bluetooth activado abrimos a aplicacion por defecto
            // e que a establezca el a man

            Intent intentOpenBluetoothSettings = new Intent();
            intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            intentOpenBluetoothSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentOpenBluetoothSettings);

            establishGoingToOtherActivity();

            // e paramos o proceso
            stopSelf();
        }

        Preferences.get(getApplicationContext()).setServiceRunningStatus(false);
        Preferences.get(getApplicationContext()).setIsOBDconnected(false);
        TripRecord.getTripRecode(this).clear();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Preferences.get(getApplicationContext()).setServiceRunningStatus(true);
        Preferences.get(getApplicationContext()).setIsOBDconnected(false);

        L.i(TAG, "Bluetooth service created ::");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Preferences.get(getApplicationContext()).setServiceRunningStatus(false);
        Preferences.get(getApplicationContext()).setIsOBDconnected(false);
        TripRecord.getTripRecode(this).clear();


        L.i(TAG, "Bluetooth service destroyed ::");
    }

    @Override
    public boolean stopService(Intent name) {
        // establecemos el flag que permittira saber si el servicio esta corriendo o no a false
        L.i(TAG, "Bluetooth service stopped ::");
        Preferences.get(getApplicationContext()).setServiceRunningStatus(false);
        return super.stopService(name);
    }

    /**
     * Gestiona el comando que ha sido recibido
     */
    private void handleCommand() {

        if (!deviceIsConnected) {
            connectToDevice();
        }

        if (deviceIsConnected) {
            executeCommand();
        }

        if (Preferences.get(getApplicationContext()).getServiceRunningStatus()) {
            L.i("The service is currently running, trying to reconnect ::");

            handleCommand();
        }

    }


    private void executeCommand() {

        L.i("The thread which is executing the task is :: " + Thread.currentThread().getId());
        TripRecord tripRecord = TripRecord.getTripRecode(this);
        ArrayList<ObdCommand> commands = (ArrayList<ObdCommand>) ObdConfiguration.getmObdCommands().clone();

        int count = 0;
        while (bthSocket != null && bthSocket.isConnected() &&
                commands.size() > count && deviceIsConnected &&
                Preferences.get(getApplicationContext()).getServiceRunningStatus()) {

            ObdCommand command = commands.get(count);
            try {

                L.i("Command executed :: " + command.getName());
                command.run(bthSocket.getInputStream(), bthSocket.getOutputStream());
                L.i("The result of the command is :: " + command.getFormattedResult() + " :: name is :: " + command.getName());
                tripRecord.updateTrip(command.getName(), command);


                if (isFaultCodeRead) {

                    try {
                        TroubleCodesCommand troubleCodesCommand = new TroubleCodesCommand();
                        troubleCodesCommand.run(bthSocket.getInputStream(), bthSocket.getOutputStream());
                        tripRecord.updateTrip(troubleCodesCommand.getName(), troubleCodesCommand);
                        isFaultCodeRead = false;

                    } catch (Exception e) {
                        L.i(TAG, "There was an error trying to execute the command :: " + command.getName());
                    }

                }

                sendBroadcast(dataReadNotify);

            } catch (Exception e) {

                L.i("Exception executing command :: " + command.getName() + " :: " + e.getMessage());

                boolean condicionOriginalComrpobacionConexion = !TextUtils.isEmpty(e.getMessage())
                        && (e.getMessage().equals("Broken pipe") || e.getMessage().equals("Connection reset by peer"));

                boolean unableToRetrieveData = e.getMessage().contains(BluetoothConstants.CODE_OBD_UNABLE_TO_RETRIEVE_DATA);

                boolean carDisconnected = e.getMessage().contains(BluetoothConstants.CODE_OBD_CAR_DISCONNECTED);

                if (condicionOriginalComrpobacionConexion || unableToRetrieveData) {
                    L.i("command Exception  :: " + e.getMessage());

                    sendMessageToBroadcast(BluetoothConstants.OBD_ACTION_MESSAGE, getString(R.string.obd_running_vehicle_mandatory));
                    establishDiconnectionStatus();

                    deviceIsConnected = false;
                    return;

                } else if (carDisconnected) {
                    establishDiconnectionStatus();
                }
            }

            count++;
            if (count == commands.size()) {
                count = 0;


                // Establece un delay entre recuperacion de los datos
                try {
                    Thread.sleep(BluetoothConstants.COMMAND_EXECUTION_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        // exit loop means connection lost, so set connection status false
        deviceIsConnected = false;

    }

    /**
     * Revisa los dispositivos con los que nos hemos emparejado para ver si alguno de ellos es el
     * OBD2 e intenta establecer conexion con el mismo.
     */
    private void connectToDevice() {
        while (!deviceIsConnected && Preferences.get(getApplicationContext()).getServiceRunningStatus()) {

            if (bthAdapter != null) {
                boolean deviceFound = false;

                // revisamos todos los dispositivos emparejados
                Set<BluetoothDevice> bluetoothDevices = bthAdapter.getBondedDevices();

                if (bluetoothDevices.isEmpty()) {
                    // TODO Revisar si esto está bien implementado.
                    sendMessageToBroadcast(BluetoothConstants.OBD_ACTION_MESSAGE, getString(R.string.bluetooth_without_available_devices));
                    stopService(new Intent(this, BluetoothReaderService.class));

                    Preferences.get(this).setServiceRunningStatus(false);
                    break;
                }
                for (BluetoothDevice device : bluetoothDevices) {

                    String name = device.getName();

                    // si el dispositivo del OBD2 intentamos conectarnos
                    if (name != null && (name.toUpperCase().contains(BluetoothConstants.deviceObdName) || name.toUpperCase().contains(BluetoothConstants.deviceVlink))) {

                        try {
                            // intentamos establecer la conexion con el dispositivo
                            connectToOBDDevice(device);

                        } catch (Exception e) {
                            L.i("It was not possible to connect to OBD2 device :: " + e.getMessage());

                        }

                        deviceFound = true;
                        break;
                    }

                }

                if (!deviceFound && (bthSocket == null || bthSocket.isConnected())) {
                    // no caso de que non se encontrase algun dispositivo, mandamos un mensaje
                    sendMessageToBroadcast(BluetoothConstants.OBD_ACTION_MESSAGE, getString(R.string.obd_not_found));
                }
            }
        }

    }


    /**
     * Intentamos establecer la conexion con el dispositivo OBD2 con todo el proceso que este conlleva.
     *
     * @param bthDevice Dispositivo OBD2 con el que conectarnos
     */
    private void connectToOBDDevice(BluetoothDevice bthDevice) {

        try {
            // intentamos establecer conexion con el dispositivo
            bthSocket = (BluetoothSocket) bthDevice.getClass().getMethod("createInsecureRfcommSocket", new Class[]{int.class}).invoke(bthDevice, 1);

        } catch (Exception e) {

            L.i("We were not able to connect to the OBD device. Closing socket...");
            closeSocket();
        }

        if (bthSocket != null) {
            // caso de que fueramos capaces de conectarnos

            try {
                // paramos el servicio de discovery para
                bthAdapter.cancelDiscovery();

                // establecemos una espera para dar tiempo a que se desconecte
                Thread.sleep(BluetoothConstants.EXTRA_SHORT_DELAY);

                bthSocket.connect();

                L.i("Connected to the OBD ::");

            } catch (Exception e) {

                L.i("There was a problem trying to connect to OBD :: " + e.getMessage());
                closeSocket();
            }

            boolean isSockedConnected = bthSocket.isConnected();

            if (isSockedConnected) {

                try {
                    Thread.sleep(BluetoothConstants.SHORT_DELAY);

                    L.i("Executing reset command in new Thread :: " + Thread.currentThread().getId());

                    Thread newThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                // establecemos la siguiente serie de comandos para intentar
                                // conectarnos al dispositivo. Ponemos un sleep despues de cada comandp
                                // para darle tiempo al dispositivo de procesarlo

                                deviceConnectionIsRunning = false;
                                new ObdResetCommand().run(bthSocket.getInputStream(), bthSocket.getOutputStream());
                                Thread.sleep(1000);
                                new EchoOffCommand().run(bthSocket.getInputStream(), bthSocket.getOutputStream());
                                Thread.sleep(200);
                                new LineFeedOffCommand().run(bthSocket.getInputStream(), bthSocket.getOutputStream());
                                Thread.sleep(200);
                                new SpacesOffCommand().run(bthSocket.getInputStream(), bthSocket.getOutputStream());
                                Thread.sleep(200);
                                new SpacesOffCommand().run(bthSocket.getInputStream(), bthSocket.getOutputStream());
                                Thread.sleep(200);
                                new TimeoutCommand(125).run(bthSocket.getInputStream(), bthSocket.getOutputStream());
                                Thread.sleep(200);
                                new SelectProtocolCommand(ObdProtocols.AUTO).run(bthSocket.getInputStream(), bthSocket.getOutputStream());
                                Thread.sleep(200);
                                new EchoOffCommand().run(bthSocket.getInputStream(), bthSocket.getOutputStream());
                                Thread.sleep(200);
                                deviceConnectionIsRunning = true;

                            } catch (Exception e) {
                                deviceConnectionIsRunning = false;
                                L.i("There was a problem establishing connection with the OBD :: " + e.getMessage());
                            }

                        }
                    });

                    newThread.start();
                    newThread.join(BluetoothConstants.LONG_DELAY);
                    L.i("Thread wake to check reset command status  i.e  :: " + Thread.currentThread().getId() + ",  Connection is running :: " + deviceConnectionIsRunning);
                    isSockedConnected = deviceConnectionIsRunning;

                } catch (Exception e) {
                    L.i("There was a problem trying to reset the connection :: " + e.getMessage());
                    isSockedConnected = false;
                }

            }

            if (bthSocket != null && bthSocket.isConnected() && isSockedConnected) {

                // si nos damos conectado, actualizamos el estado de la tarea
                establishConnectionStatus();

            } else {

                // en caso de que no podamos conectarnos mandamos el mensaje correspondiente
                sendMessageToBroadcast(BluetoothConstants.OBD_ACTION_MESSAGE, getString(R.string.obd_not_available));
                establishDiconnectionStatus();

            }
        }
    }

    private void sendMessageToBroadcast(String action, String message) {
        Intent intent = new Intent(action);
        intent.putExtra(BluetoothConstants.OBD_EXTRA_DATA, message);
        sendBroadcast(intent);
    }

    /**
     * Comprueba que el bluetooth esta conectado
     *
     * @return
     */
    private boolean checkBluetoothActivated() {
        bthAdapter = BluetoothAdapter.getDefaultAdapter();

        return bthAdapter != null && bthAdapter.isEnabled();
    }

    /**
     * Metodo utilizado para establecer a nivel de aplicacion el estado de la conexion
     */
    private void establishConnectionStatus() {

        Preferences.get(getApplicationContext()).setIsOBDconnected(true);
        deviceIsConnected = true;

        L.i("The socket was connected :: ");
        sendBroadcast(new Intent(BluetoothConstants.OBD_ACTION_CONNECTED));
    }

    private void establishDiconnectionStatus() {

        Preferences.get(getApplicationContext()).setIsOBDconnected(false);
        Preferences.get(getApplicationContext()).setServiceRunningStatus(false);
        deviceIsConnected = false;
        closeSocket();

        L.i("The socket was disconnected :: ");

        sendBroadcast(new Intent(BluetoothConstants.OBD_ACTION_DISCONNECTED));
        stopSelf();
    }

    /**
     * close Bluetooth Socket
     */
    private void closeSocket() {
        L.i("Closing socket ::");
        if (bthSocket != null) {

            try {
                bthSocket.close();

            } catch (IOException e) {
                L.i("We were not able to close the socket ::");

            }
        }
    }


    private void establishGoingToOtherActivity() {

        SharedPreferences preferences = getSharedPreferences("PreferenciasAtopate", Context.MODE_PRIVATE);

        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putBoolean(BluetoothConstants.PREFERENCE_KEY_GOING_TO_BLUETOOTH, true);
        preferencesEditor.apply();
    }


    /**
     * create Binder instance used to return in onBind method
     */
    public class LocalBinder extends Binder {
        public BluetoothReaderService getService() {
            return BluetoothReaderService.this;
        }
    }


}


