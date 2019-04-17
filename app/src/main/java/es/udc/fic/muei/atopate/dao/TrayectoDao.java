package es.udc.fic.muei.atopate.dao;

import java.util.Date;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import es.udc.fic.muei.atopate.entities.Trayecto;

@Dao
public interface TrayectoDao {

    @Query("SELECT * FROM trayecto ORDER BY hora_fin DESC LIMIT 1")
    public Trayecto getLast();

    @Query("SELECT * FROM trayecto")
    public List<Trayecto> getAll();

    @Query("SELECT * FROM trayecto WHERE hora_inicio BETWEEN :from AND :to")
    List<Trayecto> findTrayectosBetweenDates(Date from, Date to);

    @Insert
    public Long insert(Trayecto trayecto);

    @Insert
    public void insert(List<Trayecto> trayectos);

    @Update
    public void update(Trayecto trayecto);

    @Delete
    public int delete(List<Trayecto> trayectos);

    @Delete
    public void delete(Trayecto trayecto);
}
