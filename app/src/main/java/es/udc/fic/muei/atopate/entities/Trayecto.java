package es.udc.fic.muei.atopate.entities;

import android.graphics.Bitmap;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Trayecto {
    @PrimaryKey
    @NonNull
    public Long id;

    @ColumnInfo(name = "hora_inicio")
    public Date horaInicio;

    @ColumnInfo(name = "hora_fin")
    public Date horaFin;

    public String origen;

    public String destino;

    public int distancia;

    public String foto;

    @Ignore
    public Bitmap fotoBitmap;
}
