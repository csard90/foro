package com.grupouno.proyectofinalforo.entidades;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Mensaje {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String mensaje;
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:MM:SS")
    private Date fecha;
    private Boolean alta;
    @ManyToOne
    private Hilo hilo;
    @ManyToOne
    private Usuario usuario;

    public Mensaje() {
        this.hilo = new Hilo();
        this.usuario = new Usuario();
    }

    public Mensaje(String id, String mensaje, Date fecha, Boolean alta, Hilo hilo, Usuario usuario) {
        this.id = id;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.alta = alta;
        this.hilo = hilo;
        this.usuario = usuario;
    }    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Boolean getAlta() {
        return alta;
    }

    public void setAlta(Boolean alta) {
        this.alta = alta;
    }    
    
    public Hilo getHilo() {
        return hilo;
    }

    public void setHilo(Hilo hilo) {
        this.hilo = hilo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "Mensaje{" + "id=" + id + ", mensaje=" + mensaje + ", fecha=" + fecha + ", alta=" + alta + ", hilo=" + hilo + ", usuario=" + usuario + '}';
    }
            
}
