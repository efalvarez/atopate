package es.udc.fic.muei.atopate.entities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import es.udc.fic.muei.atopate.db.model.Trayecto;

/*
 *
 */
public class itemHistorialEntity {

    private Integer itemId;
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
        this.lugarOrigen = "x";
        this.lugarDestino = "x";
        this.distancia = "x Km";
        this.horas = "x horas";

    }

    public itemHistorialEntity(Trayecto t) {
        super();
        this.id = t.id;
        this.lugarDestino = t.destino;
        this.lugarOrigen = t.origen;
        this.distancia = t.distancia + " km";
        this.icono = t.foto;

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
                this.tiempo = "Hace " + minutos + ( minutos == 1 ? " minuto" : " minutos");
            } else {
                this.tiempo = "Hace " + horas + ( horas == 1 ? " hora" : " horas");
            }
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
}
