package es.udc.fic.muei.atopate.entities;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = {
        @ForeignKey(entity = Trayecto.class,
                parentColumns = "id",
                childColumns = "trayecto_id",
                onDelete = CASCADE)},
        tableName = "puntos_trayecto")
public class PuntosTrayecto {

    public List<LatLng> coordenadas;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name="trayecto_id")
    public Long trayectoId;
}
