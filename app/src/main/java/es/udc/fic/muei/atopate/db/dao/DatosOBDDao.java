package es.udc.fic.muei.atopate.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import es.udc.fic.muei.atopate.db.model.DatosOBD;

@Dao
public interface DatosOBDDao {

    @Query("SELECT * FROM datos_obd WHERE trayecto_id = :trayectoId")
    public DatosOBD getByTrayecto(Long trayectoId);

    @Insert
    public Long insert(DatosOBD datosOBD);

    @Update
    public void update(DatosOBD datosOBD);

    @Delete
    public void delete(DatosOBD datosOBD);
}
