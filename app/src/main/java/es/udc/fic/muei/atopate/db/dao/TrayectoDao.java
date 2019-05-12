package es.udc.fic.muei.atopate.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Calendar;
import java.util.List;

import es.udc.fic.muei.atopate.db.model.Trayecto;

@Dao
public abstract class TrayectoDao implements BaseDao<Trayecto> {

    @Query("SELECT * FROM trayecto ORDER BY hora_fin DESC LIMIT 1")
    public abstract Trayecto getLast();

    @Query("SELECT * FROM trayecto ORDER BY hora_fin DESC")
    public abstract List<Trayecto> getAll();

    @Query("SELECT * FROM trayecto WHERE hora_inicio BETWEEN :from AND :to")
    public abstract List<Trayecto> findTrayectosBetweenDates(Calendar from, Calendar to);

    @Query("SELECT * FROM trayecto WHERE id = :trayectoId")
    public abstract Trayecto getById(Long trayectoId);

    @Query("SELECT * FROM trayecto WHERE strftime('%Y-%m-%d', hora_inicio / 1000, 'unixepoch') = DATE('now')")
    public abstract List<Trayecto> getAllToday();

    @Query("SELECT * FROM trayecto WHERE strftime('%Y-%m-%d', hora_inicio / 1000, 'unixepoch') = DATE('now', '-1 days')")
    public abstract List<Trayecto> getAllYesterday();

    @Query("SELECT * FROM trayecto WHERE strftime('%Y-%m-%d', hora_inicio / 1000, 'unixepoch') BETWEEN DATE('now', '-7 days') and DATE('now')")
    public abstract List<Trayecto> getAllLastWeek();

    @Override
    public Long upsert(Trayecto entidad) {

        long id = insert(entidad);
        if (id == -1) {
            update(entidad);
            id = entidad.id;
        }
        return id;
    }

    @Delete
    public abstract int delete(List<Trayecto> trayectos);

    @Query("DELETE FROM trayecto")
    public abstract void delete();
}
