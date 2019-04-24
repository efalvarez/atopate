package es.udc.fic.muei.atopate.db.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = {
        @ForeignKey(entity = Trayecto.class,
                parentColumns = "id",
                childColumns = "trayecto_id",
                onDelete = CASCADE)},
        tableName = "datos_obd")
public class DatosOBD {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name="trayecto_id")
    public Long trayectoId;
}
