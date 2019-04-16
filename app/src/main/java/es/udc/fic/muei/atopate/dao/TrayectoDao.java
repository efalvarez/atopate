package es.udc.fic.muei.atopate.dao;

import java.util.Date;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import es.udc.fic.muei.atopate.entities.Trayecto;

@Dao
public interface TrayectoDao {
    @Query("SELECT * FROM trayecto WHERE hora_inicio BETWEEN :from AND :to")
    List<Trayecto> findTrayectosBetweenDates(Date from, Date to);
}
