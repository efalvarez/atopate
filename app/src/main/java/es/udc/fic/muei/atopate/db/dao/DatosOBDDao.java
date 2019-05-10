package es.udc.fic.muei.atopate.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import es.udc.fic.muei.atopate.db.model.DatosOBD;

@Dao
public abstract class DatosOBDDao implements BaseDao<DatosOBD> {

    @Query("SELECT * FROM datos_obd WHERE trayecto_id = :trayectoId")
    public abstract List<DatosOBD> getByTrayecto(Long trayectoId);

    @Override
    public Long upsert(DatosOBD entidad) {

        long id = insert(entidad);
        if (id == -1) {
            update(entidad);
            id = entidad.dataId;
        }
        return id;
    }

}