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

@Database(entities = {Trayecto.class, PuntosTrayecto.class, DatosOBD.class}, version = 2)
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

            Calendar horaInicio = Calendar.getInstance();
            Calendar horaFin = Calendar.getInstance();
            horaInicio.set(2019, 3, 18, 11, 25);
            horaFin.set(2019, 3, 18, 14, 15);
            Trayecto trayecto = new Trayecto("A Coruña", "Madrid", horaInicio, horaFin, 530, "pathFoto", false);

            datos.trayectoId = trayectoDao.upsert(trayecto);

            datosOBDDao.upsert(datos);

//            horaInicio.set(2019, 2, 18, 11, 25);
//            horaFin.set(2019, 2, 18, 14, 15);
//            Trayecto trayecto2 = new Trayecto("Av. dos Mallos", "Pza Pontevedra", horaInicio, horaFin, 2, "pathFoto", false);
//            trayectoDao.upsert(trayecto2);
//
//            horaInicio.set(2019, 1, 18, 11, 25);
//            horaFin.set(2019, 1, 18, 14, 15);
//            Trayecto trayecto3 = new Trayecto("Ronda de outeiro", "Pza Pontevedra", horaInicio, horaFin, 1, "pathFoto", false);
//            trayectoDao.upsert(trayecto3);
            return null;
        }
    }
}
