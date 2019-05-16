package es.udc.fic.muei.atopate.db;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import es.udc.fic.muei.atopate.db.dao.DatosOBDDao;
import es.udc.fic.muei.atopate.db.dao.PuntosTrayectoDao;
import es.udc.fic.muei.atopate.db.dao.TrayectoDao;
import es.udc.fic.muei.atopate.db.model.DatosOBD;
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

    public List<Trayecto> getAll() {
        try {
            return new getAllTrayectosAsyncTask(dao, puntosDao, datosOBDDao).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Trayecto> getAllToday() {
        try {
            return new getAllTodayAsyncTask(dao, puntosDao, datosOBDDao).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Trayecto> getAllYesterday() {
        try {
            return new getAllYesterdayAsyncTask(dao, puntosDao, datosOBDDao).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Trayecto> getAllLastWeek() {
        try {
            return new getAllLastWeekAsyncTask(dao, puntosDao, datosOBDDao).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Long insert(Trayecto trayecto) {
        try {
            return new insertAsyncTask(dao, puntosDao, datosOBDDao).execute(trayecto).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delete() {
        new deleteAsyncTask(dao).execute();
    }

    public void delete(Trayecto trayecto) {
        new deleteTrayectoAsyncTask(dao).execute(trayecto);
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

    public List<Trayecto> getAllTrayectos() {
        try {
            return new getAllTrayectosAsyncTask(dao, puntosDao, datosOBDDao).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Trayecto getTrayectoById(Long trayectoId) {

        try {
            return new getTrayectoById(dao, puntosDao, datosOBDDao).execute(trayectoId).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateTrayectoinfo(DatosOBD informacionTrayecto, Long idTrayecto) {

        informacionTrayecto.trayectoId = idTrayecto;

        new updateTrayectoInfo(datosOBDDao).execute(informacionTrayecto);
    }

    private static class deleteTrayectoAsyncTask extends AsyncTask<Trayecto, Void, Void> {

        private TrayectoDao mAsyncTaskDao;

        deleteTrayectoAsyncTask(TrayectoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Trayecto... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    public Trayecto getCurrentTrayecto() {

        try {
            return new getCurrentTrayecto(dao, puntosDao, datosOBDDao).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class insertAsyncTask extends AsyncTask<Trayecto, Void, Long> {

        private TrayectoDao mAsyncTaskDao;
        private PuntosTrayectoDao mAsyncTaskPuntosDao;
        private DatosOBDDao mAsyncTaskDatosOBDDao;

        insertAsyncTask(TrayectoDao dao, PuntosTrayectoDao puntosDao, DatosOBDDao datosOBDDao) {
            mAsyncTaskDao = dao;
            mAsyncTaskPuntosDao = puntosDao;
            mAsyncTaskDatosOBDDao = datosOBDDao;
        }

        @Override
        protected Long doInBackground(Trayecto... params) {

            Trayecto trayectoToInsert = params[0];
            Long idTrayecto;
            if (trayectoToInsert != null && trayectoToInsert.esTrayectoActual) {
                idTrayecto = mAsyncTaskDao.setCurrentTrayecto(trayectoToInsert);
            } else {
                idTrayecto = mAsyncTaskDao.upsert(params[0]);
            }

            List<DatosOBD> datosCoche = params[0].datosOBD;

            if (params[0].puntosTrayecto != null) {
                params[0].puntosTrayecto.trayectoId = idTrayecto;
                mAsyncTaskPuntosDao.upsert(params[0].puntosTrayecto);
            }

            if (!CollectionUtils.isEmpty(datosCoche)) {

                DatosOBD[] datos = datosCoche.toArray(new DatosOBD[0]);
                for (DatosOBD datoCoche : datos) {
                    datoCoche.trayectoId = idTrayecto;
                    mAsyncTaskDatosOBDDao.upsert(datoCoche);
                }

            }

            return idTrayecto;
        }
    }

    private static class setFotoAsyncTask extends AsyncTask<Trayecto, Void, Void> {

        private TrayectoDao mAsyncTaskDao;

        setFotoAsyncTask(TrayectoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Trayecto... params) {
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

    private static class getAllTrayectosAsyncTask extends AsyncTask<Void, Void, List<Trayecto>> {

        private TrayectoDao mAsyncTaskDao;
        private PuntosTrayectoDao mAsyncTaskPuntosDao;
        private DatosOBDDao mAsyncTaskDatosOBDDao;

        getAllTrayectosAsyncTask(TrayectoDao dao, PuntosTrayectoDao puntosDao, DatosOBDDao datosOBDDao) {
            mAsyncTaskDao = dao;
            mAsyncTaskPuntosDao = puntosDao;
            mAsyncTaskDatosOBDDao = datosOBDDao;
        }

        @Override
        protected List<Trayecto> doInBackground(Void... voids) {
            List<Trayecto> result = mAsyncTaskDao.getAll();

            for (Trayecto t : result) {
                t.puntosTrayecto = mAsyncTaskPuntosDao.getByTrayecto(t.id);
                t.datosOBD = mAsyncTaskDatosOBDDao.getByTrayecto(t.id);
            }

            return result;
        }
    }

    private static class getAllTodayAsyncTask extends AsyncTask<Void, Void, List<Trayecto>> {

        private TrayectoDao mAsyncTaskDao;
        private PuntosTrayectoDao mAsyncTaskPuntosTrayectoDao;
        private DatosOBDDao mAsyncTaskDatosOBDDao;

        getAllTodayAsyncTask(TrayectoDao dao, PuntosTrayectoDao puntosTrayectoDao, DatosOBDDao datosOBDDao) {
            mAsyncTaskDao = dao;
            mAsyncTaskPuntosTrayectoDao = puntosTrayectoDao;
            mAsyncTaskDatosOBDDao = datosOBDDao;
        }

        @Override
        protected List<Trayecto> doInBackground(Void... voids) {
            List<Trayecto> result = mAsyncTaskDao.getAllToday();

            for (Trayecto t : result) {
                t.puntosTrayecto = mAsyncTaskPuntosTrayectoDao.getByTrayecto(t.id);
                t.datosOBD = mAsyncTaskDatosOBDDao.getByTrayecto(t.id);
            }

            return result;
        }
    }

    private static class getAllYesterdayAsyncTask extends AsyncTask<Void, Void, List<Trayecto>> {

        private TrayectoDao mAsyncTaskDao;
        private PuntosTrayectoDao mAsyncTaskPuntosTrayectoDao;
        private DatosOBDDao mAsyncTaskDatosOBDDao;

        getAllYesterdayAsyncTask(TrayectoDao dao, PuntosTrayectoDao puntosTrayectoDao, DatosOBDDao datosOBDDao) {
            mAsyncTaskDao = dao;
            mAsyncTaskPuntosTrayectoDao = puntosTrayectoDao;
            mAsyncTaskDatosOBDDao = datosOBDDao;
        }

        @Override
        protected List<Trayecto> doInBackground(Void... voids) {
            List<Trayecto> result = mAsyncTaskDao.getAllYesterday();

            for (Trayecto t : result) {
                t.puntosTrayecto = mAsyncTaskPuntosTrayectoDao.getByTrayecto(t.id);
                t.datosOBD = mAsyncTaskDatosOBDDao.getByTrayecto(t.id);
            }

            return result;
        }
    }

    private static class getAllLastWeekAsyncTask extends AsyncTask<Void, Void, List<Trayecto>> {

        private TrayectoDao mAsyncTaskDao;
        private PuntosTrayectoDao mAsyncTaskPuntosTrayectoDao;
        private DatosOBDDao mAsyncTaskDatosOBDDao;

        getAllLastWeekAsyncTask(TrayectoDao dao, PuntosTrayectoDao puntosTrayectoDao, DatosOBDDao datosOBDDao) {
            mAsyncTaskDao = dao;
            mAsyncTaskPuntosTrayectoDao = puntosTrayectoDao;
            mAsyncTaskDatosOBDDao = datosOBDDao;
        }

        @Override
        protected List<Trayecto> doInBackground(Void... voids) {
            List<Trayecto> result = mAsyncTaskDao.getAllLastWeek();

            for (Trayecto t : result) {
                t.puntosTrayecto = mAsyncTaskPuntosTrayectoDao.getByTrayecto(t.id);
                t.datosOBD = mAsyncTaskDatosOBDDao.getByTrayecto(t.id);
                for (Trayecto trayecto : result) {
                    trayecto.puntosTrayecto = mAsyncTaskPuntosTrayectoDao.getByTrayecto(trayecto.id);
                    trayecto.datosOBD = mAsyncTaskDatosOBDDao.getByTrayecto(trayecto.id);
                }
            }
            return result;
        }
    }

    private static class getTrayectoById extends AsyncTask<Long, Void, Trayecto> {

        private TrayectoDao mAsyncTaskDao;
        private PuntosTrayectoDao mAsyncTaskPuntosDao;
        private DatosOBDDao mAsyncTaskDatosOBDDao;

        getTrayectoById(TrayectoDao dao, PuntosTrayectoDao puntosDao, DatosOBDDao datosOBDDao) {
            mAsyncTaskDao = dao;
            mAsyncTaskPuntosDao = puntosDao;
            mAsyncTaskDatosOBDDao = datosOBDDao;
        }

        @Override
        protected Trayecto doInBackground(Long... params) {
            Trayecto result = mAsyncTaskDao.getById(params[0]);

            if (result != null) {
                result.puntosTrayecto = mAsyncTaskPuntosDao.getByTrayecto(result.id);
                result.datosOBD = mAsyncTaskDatosOBDDao.getByTrayecto(result.id);
            }

            return result;
        }
    }


    private static class updateTrayectoInfo extends AsyncTask<DatosOBD, Void, Void> {

        private DatosOBDDao mAsyncTaskDatosOBDDao;

        updateTrayectoInfo(DatosOBDDao datosOBDDao) {
            mAsyncTaskDatosOBDDao = datosOBDDao;
        }

        @Override
        protected Void doInBackground(DatosOBD... params) {
            mAsyncTaskDatosOBDDao.upsert(params[0]);
            return null;
        }
    }


    private static class getCurrentTrayecto extends AsyncTask<Void, Void, Trayecto> {

        private TrayectoDao mAsyncTaskDao;
        private PuntosTrayectoDao mAsyncTaskPuntosDao;
        private DatosOBDDao mAsyncTaskDatosOBDDao;

        getCurrentTrayecto(TrayectoDao dao, PuntosTrayectoDao puntosDao, DatosOBDDao datosOBDDao) {
            mAsyncTaskDao = dao;
            mAsyncTaskPuntosDao = puntosDao;
            mAsyncTaskDatosOBDDao = datosOBDDao;
        }

        @Override
        protected Trayecto doInBackground(Void... params) {
            Trayecto result = mAsyncTaskDao.getCurrent();

            if (result != null) {
                result.puntosTrayecto = mAsyncTaskPuntosDao.getByTrayecto(result.id);
                result.datosOBD = mAsyncTaskDatosOBDDao.getByTrayecto(result.id);
            }

            return result;
        }
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
}
