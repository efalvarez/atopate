package es.udc.fic.muei.atopate.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.Calendar;

import es.udc.fic.muei.atopate.db.dao.DatosOBDDao;
import es.udc.fic.muei.atopate.db.dao.PuntosTrayectoDao;
import es.udc.fic.muei.atopate.db.dao.TrayectoDao;
import es.udc.fic.muei.atopate.db.model.DatosOBD;
import es.udc.fic.muei.atopate.db.model.PuntosTrayecto;
import es.udc.fic.muei.atopate.db.model.Trayecto;

@Database(entities = {Trayecto.class, PuntosTrayecto.class, DatosOBD.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsync(INSTANCE).execute();
        }
    };

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "atopate")
                    // Wipes and rebuilds
                    .fallbackToDestructiveMigration()
                    .addCallback(sRoomDatabaseCallback)
                    .build();
        }
        return INSTANCE;
    }

    public abstract TrayectoDao trayectoDao();

    public abstract PuntosTrayectoDao puntosTrayectoDao();

    public abstract DatosOBDDao datosOBDDao();

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final TrayectoDao trayectoDao;

        private final DatosOBDDao datosOBDDao;

        PopulateDbAsync(AppDatabase db) {
            trayectoDao = db.trayectoDao();
            datosOBDDao = db.datosOBDDao();

        }

        @Override
        protected Void doInBackground(Void... params) {

            DatosOBD datos = new DatosOBD();
            datos.speed = 20D;
            datos.fuelLevel = 20D;
            datos.rpm = 3D;

            Calendar horaInicio = Calendar.getInstance();
            Calendar horaFin = Calendar.getInstance();
            horaInicio.set(2019, 4, 10, 11, 25);
            horaFin.set(2019, 4, 10, 14, 15);
            Trayecto trayecto = new Trayecto("A Coruña", "Madrid", horaInicio, horaFin, 530, "pathFoto", false);
            horaInicio.set(2019, 3, 18, 11, 25);
            horaFin.set(2019, 3, 18, 14, 15);

            datos.trayectoId = trayectoDao.upsert(trayecto);

            datosOBDDao.upsert(datos);
            return null;
        }
    }
}
