package es.udc.fic.muei.atopate.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import es.udc.fic.muei.atopate.db.model.PuntosTrayecto;

@Dao
public interface PuntosTrayectoDao {

    @Query("SELECT * FROM puntos_trayecto WHERE trayecto_id = :trayectoId")
    public PuntosTrayecto getByTrayecto(Long trayectoId);

    @Insert
    public Long insert(PuntosTrayecto puntosTrayecto);

    @Update
    public void update(PuntosTrayecto puntosTrayecto);

    @Delete
    public void delete(PuntosTrayecto puntosTrayecto);
}
