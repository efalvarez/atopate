package es.udc.fic.muei.atopate.db.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.Calendar;

@Entity
public class Trayecto {
    @PrimaryKey
    @NonNull
    public Long id;

    @ColumnInfo(name = "hora_inicio")
    public Calendar horaInicio;

    @ColumnInfo(name = "hora_fin")
    public Calendar horaFin;

    public String origen;

    public String destino;

    public int distancia;

    public String foto;

    @Ignore
    public Bitmap fotoBitmap;

    @Ignore
    public PuntosTrayecto puntosTrayecto;

    public Trayecto(String origen, String destino, Calendar horaInicio, Calendar horaFin, int distancia, String foto) {
        this.origen = origen;
        this.destino = destino;
        this.horaFin = horaFin;
        this.horaInicio = horaInicio;
        this.distancia = distancia;
        this.foto = foto;
    }
}
