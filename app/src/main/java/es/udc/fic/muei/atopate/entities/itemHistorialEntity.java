package es.udc.fic.muei.atopate.entities;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import es.udc.fic.muei.atopate.db.model.Trayecto;

import static android.support.constraint.Constraints.TAG;

/*
 *
 */
public class itemHistorialEntity {

    private Integer itemId;
    private Long trayectoId;
    private Long id;
    private String tiempo;
    private String lugarOrigen;
    private String lugarDestino;
    private String distancia;
    private String icono;
    private String horas;


    //CONSTRUCTORES
    public itemHistorialEntity(){
        super();
        this.tiempo = "Hace x horas";
        this.trayectoId = new Long(0);
        this.lugarOrigen = "x";
        this.lugarDestino = "x";
        this.distancia = "x Km";
        this.horas = "x horas";

    }

    public itemHistorialEntity(String tiempo, String lugarOrigen, String lugarDestino, String distancia,
                               String icono, String horas, Long trayectoId) {
        super();
        this.tiempo = tiempo;
        this.lugarOrigen = lugarOrigen;
        this.lugarDestino = lugarDestino;
        this.distancia = distancia;
        this.icono = icono;
        this.horas = horas;
        this.trayectoId = trayectoId;
    }

    public itemHistorialEntity(Trayecto t) {
        super();
        this.id = t.id;
        this.lugarDestino = t.destino;
        this.lugarOrigen = t.origen;
        this.distancia = t.distancia + " km";
        this.icono = t.foto;
        this.trayectoId = t.id;

        try{
            long duracion = t.horaFin.getTimeInMillis() - t.horaInicio.getTimeInMillis();
            long horas = TimeUnit.HOURS.convert(duracion, TimeUnit.MILLISECONDS);
            duracion -= TimeUnit.MILLISECONDS.convert(horas, TimeUnit.HOURS);
            long minutos = TimeUnit.MINUTES.convert(duracion, TimeUnit.MILLISECONDS);
            this.horas = (horas > 0 ? horas + "h " : "")  + minutos + "m";

            Calendar now = Calendar.getInstance();
            long timeFromNow = now.getTimeInMillis() - t.horaFin.getTimeInMillis();
            if (timeFromNow >=  86400000) { // Si el trayecto fue hace  24 horas o más, poner fecha
                SimpleDateFormat formato = new SimpleDateFormat("EEEE, dd/MM/yyyy");
                this.tiempo = formato.format(t.horaFin.getTime());
            } else {
                horas = TimeUnit.HOURS.convert(timeFromNow, TimeUnit.MILLISECONDS);
                if (horas <= 0) {
                    minutos = TimeUnit.MINUTES.convert(timeFromNow, TimeUnit.MILLISECONDS);
                    this.tiempo = "Hace " + minutos + (minutos == 1 ? " minuto" : " minutos");
                } else {
                    this.tiempo = "Hace " + horas + (horas == 1 ? " hora" : " horas");
                }
            }
        } catch(NullPointerException npe) {
            Log.d(TAG, "itemHistorialEntity: El trayecto se encuentra en curso...", npe);
            this.tiempo = "Trayecto en curso...";
        }
    }

    //GETERS & SETTERS
    public String getTiempo() {
        return tiempo;
    }

    public String getLugarDestino() {
        return lugarDestino;
    }

    public String getLugarOrigen() {
        return lugarOrigen;
    }

    public String getDistancia() {
        return distancia;
    }

    public String getIcono() {
        return icono;
    }

    public Integer getItemId(){
        return itemId;
    }

    public String getHoras() {
        return horas;
    }

    public Long getId() {
        return id;
    }

    public Long getTrayectoId() { return trayectoId;}

    public void setTrayectoId(Long trayectoId) { this.trayectoId = trayectoId;}

}
