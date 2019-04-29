package es.udc.fic.muei.atopate.db;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import es.udc.fic.muei.atopate.db.dao.DatosOBDDao;
import es.udc.fic.muei.atopate.db.dao.PuntosTrayectoDao;
import es.udc.fic.muei.atopate.db.dao.TrayectoDao;
import es.udc.fic.muei.atopate.db.model.Trayecto;
import es.udc.fic.muei.atopate.entities.itemHistorialEntity;

public class TrayectoService {

    private TrayectoDao dao;
    private PuntosTrayectoDao puntosDao;
    private DatosOBDDao datosOBDDao;

    public TrayectoService(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        dao = db.trayectoDao();
        puntosDao = db.puntosTrayectoDao();
        datosOBDDao = db.datosOBDDao();
    }

    public List<itemHistorialEntity> getHistorial() {
        try {
            return new getAsyncTask(dao, datosOBDDao).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insert(Trayecto trayecto) {
        new insertAsyncTask(dao, puntosDao, datosOBDDao).execute(trayecto);
    }

    public void delete() {
        new deleteAsyncTask(dao).execute();
    }
    public void setFoto(Trayecto trayecto) {
        new setFotoAsyncTask(dao).execute(trayecto);
    }

    public Trayecto getLast() {
        try {
            return new getLastAsyncTask(dao, puntosDao, datosOBDDao).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class deleteAsyncTask extends AsyncTask<Void, Void, Void> {

        private TrayectoDao mAsyncTaskDao;

        deleteAsyncTask(TrayectoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.delete();
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<Trayecto, Void, Void> {

        private TrayectoDao mAsyncTaskDao;
        private PuntosTrayectoDao mAsyncTaskPuntosDao;
        private DatosOBDDao mAsyncTaskDatosOBDDao;

        insertAsyncTask(TrayectoDao dao, PuntosTrayectoDao puntosDao, DatosOBDDao datosOBDDao ) {
            mAsyncTaskDao = dao;
            mAsyncTaskPuntosDao = puntosDao;
            mAsyncTaskDatosOBDDao = datosOBDDao;
        }

        @Override
        protected Void doInBackground(final Trayecto... params) {
            Long idTrayecto = mAsyncTaskDao.insert(params[0]);
            if (params[0].puntosTrayecto != null) {
                params[0].puntosTrayecto.trayectoId = idTrayecto;
                mAsyncTaskPuntosDao.insert(params[0].puntosTrayecto);
            }
            if (params[0].datosOBD != null) {
                params[0].datosOBD.trayectoId = idTrayecto;
                mAsyncTaskDatosOBDDao.insert(params[0].datosOBD);
            }
            return null;
        }
    }

    private static class setFotoAsyncTask extends AsyncTask<Trayecto, Void, Void> {

        private TrayectoDao mAsyncTaskDao;

        setFotoAsyncTask(TrayectoDao dao ) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Trayecto... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }

    private static class getAsyncTask extends AsyncTask<Void, Void, List<itemHistorialEntity>> {

        private TrayectoDao mAsyncTaskDao;
        private DatosOBDDao mAsyncTaskDatosOBDDao;

        getAsyncTask(TrayectoDao dao, DatosOBDDao datosOBDDao) {
            mAsyncTaskDao = dao;
            mAsyncTaskDatosOBDDao = datosOBDDao;
        }

        @Override
        protected List<itemHistorialEntity> doInBackground(Void... voids) {
            ArrayList<itemHistorialEntity> result = new ArrayList<>();

            for (Trayecto t : mAsyncTaskDao.getAll()) {
                result.add(new itemHistorialEntity(t));
            }

            return result;
        }
    }

    private static class getLastAsyncTask extends AsyncTask<Void, Void, Trayecto> {

        private TrayectoDao mAsyncTaskDao;
        private PuntosTrayectoDao mAsyncTaskPuntosDao;
        private DatosOBDDao mAsyncTaskDatosOBDDao;

        getLastAsyncTask(TrayectoDao dao, PuntosTrayectoDao puntosDao, DatosOBDDao datosOBDDao) {
            mAsyncTaskDao = dao;
            mAsyncTaskPuntosDao = puntosDao;
            mAsyncTaskDatosOBDDao = datosOBDDao;
        }

        @Override
        protected Trayecto doInBackground(Void... voids) {
            Trayecto result = mAsyncTaskDao.getLast();

            if (result != null) {
                result.puntosTrayecto = mAsyncTaskPuntosDao.getByTrayecto(result.id);
                result.datosOBD = mAsyncTaskDatosOBDDao.getByTrayecto(result.id);
            }

            return result;
        }
    }
}
