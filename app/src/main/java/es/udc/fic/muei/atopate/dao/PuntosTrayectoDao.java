package es.udc.fic.muei.atopate.dao;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import es.udc.fic.muei.atopate.entities.PuntosTrayecto;
import es.udc.fic.muei.atopate.entities.Trayecto;

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
