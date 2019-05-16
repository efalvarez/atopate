package es.udc.fic.muei.atopate.bluetooth;

public class BluetoothConstants {

    public static final String deviceObdName = "OBD";
    public static final String deviceVlink = "V-LINK";

    public static final String CODE_OBD_UNABLE_TO_RETRIEVE_DATA = "UNABLETOCONNECT";
    public static final String CODE_OBD_CAR_DISCONNECTED = "NODATA";

    public static final String PREFERENCE_KEY_GOING_TO_BLUETOOTH = "TO_BLUETOOTH_AND_FURTHER_BEYOND";

    public static final String OBD_ACTION_MESSAGE = "OBD_MESSAGE";
    public static final String OBD_ACTION_DISCONNECTED = "OBD_ACTION_DISCONNECTED";
    public static final String OBD_ACTION_CONNECTED = "OBD_ACTION_CONNECTED";
    public static final String OBD_ACTION_DATA_READ = "OBD_DATA_READ";
    public static final String OBD_EXTRA_DATA = "obd_extra_data";

    public static final int OBD_MAXIMUS_ITERATIONS = 3;


    public static final String SERVICE_NAME = "BluetoothReaderService";

    public static final int EXTRA_SHORT_DELAY = 500;
    public static final int SHORT_DELAY = 2000;
    public static final int LONG_DELAY = 15000;

    public static final int COMMAND_EXECUTION_DELAY = 1000 * 10;
}
