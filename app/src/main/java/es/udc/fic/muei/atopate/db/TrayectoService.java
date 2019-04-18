package es.udc.fic.muei.atopate.db;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import es.udc.fic.muei.atopate.db.dao.TrayectoDao;
import es.udc.fic.muei.atopate.db.model.Trayecto;
import es.udc.fic.muei.atopate.entities.itemHistorialEntity;

public class TrayectoService {

    private TrayectoDao dao;

    public TrayectoService(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        dao = db.trayectoDao();
    }

    public List<itemHistorialEntity> getHistorial() {
        ArrayList<itemHistorialEntity> result = new ArrayList<>();

        for (Trayecto t : dao.getAll()) {
            result.add(new itemHistorialEntity(t));
        }

        return result;
    }

    public void insert(Trayecto trayecto) {
        new insertAsyncTask(dao).execute(trayecto);
    }

    private static class insertAsyncTask extends AsyncTask<Trayecto, Void, Void> {

        private TrayectoDao mAsyncTaskDao;

        insertAsyncTask(TrayectoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Trayecto... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
