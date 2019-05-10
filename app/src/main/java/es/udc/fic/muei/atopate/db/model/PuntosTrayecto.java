package es.udc.fic.muei.atopate.db.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(indices = {
        @Index(value = {"points_id", "trayecto_id"}, unique = true),
        @Index(value = {"trayecto_id"})
},
        foreignKeys = {
                @ForeignKey(entity = Trayecto.class,
                        parentColumns = "id",
                        childColumns = "trayecto_id",
                        onDelete = CASCADE)},
        tableName = "puntos_trayecto")
public class PuntosTrayecto {

    public List<LatLng> coordenadas;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "points_id")
    public Long pointsId;

    @NonNull
    @ColumnInfo(name = "trayecto_id")
    public Long trayectoId;
}
