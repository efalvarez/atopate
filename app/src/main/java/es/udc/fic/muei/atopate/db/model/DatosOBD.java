package es.udc.fic.muei.atopate.db.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(indices = {
        @Index(value = {"data_id", "trayecto_id"}, unique = true),
        @Index(value = {"trayecto_id"})
},
        foreignKeys = {
                @ForeignKey(entity = Trayecto.class,
                        parentColumns = "id",
                        childColumns = "trayecto_id",
                        onDelete = CASCADE)},
        tableName = "datos_obd")
public class DatosOBD {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "data_id")
    public Long dataId;

    @NonNull
    @ColumnInfo(name = "trayecto_id")
    public Long trayectoId;

    public Double speed;

    @ColumnInfo(name = "fuel_level")
    public Double fuelLevel;

}
