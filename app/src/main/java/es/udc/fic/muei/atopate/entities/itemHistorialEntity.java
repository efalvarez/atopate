package es.udc.fic.muei.atopate.entities;

import android.graphics.drawable.Drawable;

/*
 *
 */
public class itemHistorialEntity {

    private Integer itemId;
    private String tiempo;
    private String lugarOrigen;
    private String lugarDestino;
    private String distancia;
    private Drawable icono;
    private String horas;
    private String fechas;


    //CONSTRUCTORES
    public itemHistorialEntity(){
        super();
        this.tiempo = "Hace x horas";
        this.lugarOrigen = "x";
        this.lugarDestino = "x";
        this.distancia = "x Km";
        this.horas = "x horas";
        this.fechas = "x fecha - y fecha";

    }

    public itemHistorialEntity(String tiempo, String lugarOrigen, String lugarDestino, String distancia,
                               Drawable icono, String horas, String fechas) {
        super();
        this.tiempo = tiempo;
        this.lugarOrigen = lugarOrigen;
        this.lugarDestino = lugarDestino;
        this.distancia = distancia;
        this.icono = icono;
        this.horas = horas;
        this.fechas = fechas;
    }


    //GETERS & SETTERS
    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
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

    public void setDistancia(String distancia) {
        this.distancia = distancia;
    }

    public Drawable getIcono() {
        return icono;
    }

    public void setIcono(Drawable icono) {
        this.icono = icono;
    }

    public Integer getItemId(){
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public void setLugarOrigen(String lugarOrigen) {
        this.lugarOrigen = lugarOrigen;
    }

    public void setLugarDestino(String lugarDestino) {
        this.lugarDestino = lugarDestino;
    }

    public String getHoras() {
        return horas;
    }

    public void setHoras(String horas) {
        this.horas = horas;
    }

    public String getFechas() {
        return fechas;
    }

    public void setFechas(String fechas) {
        this.fechas = fechas;
    }

}
