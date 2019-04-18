package es.udc.fic.muei.atopate.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

import es.udc.fic.muei.atopate.db.model.Trayecto;

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
