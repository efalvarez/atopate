package es.udc.fic.muei.atopate.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import es.udc.fic.muei.atopate.db.model.PuntosTrayecto;

@Dao
public abstract class PuntosTrayectoDao implements BaseDao<PuntosTrayecto> {

    @Query("SELECT * FROM puntos_trayecto WHERE trayecto_id = :trayectoId")
    public abstract PuntosTrayecto getByTrayecto(Long trayectoId);

    @Override
    public Long upsert(PuntosTrayecto entidad) {

        long id = insert(entidad);
        if (id == -1) {
            update(entidad);
            id = entidad.pointsId;
        }
        return id;
    }
}
