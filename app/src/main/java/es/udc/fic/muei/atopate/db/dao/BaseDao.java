package es.udc.fic.muei.atopate.db.dao;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import java.util.List;

public interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Long insert(T entidad);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<T> entidad);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void update(T entidad);

    @Delete
    int delete(List<T> entidad);

    @Delete
    void delete(T entidad);

    @Transaction
    Long upsert(T entidad);


}
