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
import java.util.Date;

import es.udc.fic.muei.atopate.db.dao.PuntosTrayectoDao;
import es.udc.fic.muei.atopate.db.dao.TrayectoDao;
import es.udc.fic.muei.atopate.db.model.PuntosTrayecto;
import es.udc.fic.muei.atopate.db.model.Trayecto;

@Database(entities = {Trayecto.class, PuntosTrayecto.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract TrayectoDao trayectoDao();
    public abstract PuntosTrayectoDao puntosTrayectoDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "atopate").allowMainThreadQueries()
                    // Wipes and rebuilds
                    .fallbackToDestructiveMigration()
                    .addCallback(sRoomDatabaseCallback)
                    .build();
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // Example data when db created
            new PopulateDbAsync(INSTANCE).execute();
        }
    };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final TrayectoDao trayectoDao;

        PopulateDbAsync(AppDatabase db) {
            trayectoDao = db.trayectoDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            Calendar horaInicio = Calendar.getInstance();
            Calendar horaFin = Calendar.getInstance();
            horaInicio.set(2019, 4, 18, 11, 25);
            horaFin.set(2019, 4, 18, 14, 15);
            Trayecto trayecto = new Trayecto("A Coruña", "Madrid", horaInicio, horaFin, 530, "pathFoto");
            trayectoDao.insert(trayecto);

            horaInicio.set(2019, 3, 18, 11, 25);
            horaFin.set(2019, 3, 18, 14, 15);
            Trayecto trayecto2 = new Trayecto("Av. dos Mallos", "Pza Pontevedra", horaInicio, horaFin, 2, "pathFoto");
            trayectoDao.insert(trayecto2);

            horaInicio.set(2019, 2, 18, 11, 25);
            horaFin.set(2019, 2, 18, 14, 15);
            Trayecto trayecto3 = new Trayecto("Ronda de outeiro", "Pza Pontevedra", horaInicio, horaFin, 1, "pathFoto");
            trayectoDao.insert(trayecto3);
            return null;
        }
    }
}
