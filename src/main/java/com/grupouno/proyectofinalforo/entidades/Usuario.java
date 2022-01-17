package com.grupouno.proyectofinalforo.entidades;

import com.grupouno.proyectofinalforo.enumeraciones.Role;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Usuario {

    //@DateTimeFormat(iso=ISO.DATE) para darle forma a las fechas
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String email;
    private String nombreDeUsuario;
    private String contrasena;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaNacimiento;
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:MM:SS")
    private Date fechaDeAlta;
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:MM:SS")
    private Date fechaDeBaja;
    private String genero; //F-M-O
    private String nacionalidad; //Desplegable
    private Boolean alta;
    @Enumerated(EnumType.STRING)
    private Role rol;
    private String descripcion;
    @OneToOne
    private Foto fotoDePerfil; //Consultar

    public Usuario() {
    }

    public Usuario(String id, String email, String nombreDeUsuario, String contrasena, Date fechaNacimiento, Date fechaDeAlta, Date fechaDeBaja, String genero, String nacionalidad, Boolean alta, Role rol, String descripcion, Foto fotoDePerfil) {
        this.id = id;
        this.email = email;
        this.nombreDeUsuario = nombreDeUsuario;
        this.contrasena = contrasena;
        this.fechaNacimiento = fechaNacimiento;
        this.fechaDeAlta = fechaDeAlta;
        this.fechaDeBaja = fechaDeBaja;
        this.genero = genero;
        this.nacionalidad = nacionalidad;
        this.alta = alta;
        this.rol = rol;
        this.descripcion = descripcion;
        this.fotoDePerfil = fotoDePerfil;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombreDeUsuario() {
        return nombreDeUsuario;
    }

    public void setNombreDeUsuario(String nombreDeUsuario) {
        this.nombreDeUsuario = nombreDeUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Date getFechaDeAlta() {
        return fechaDeAlta;
    }

    public void setFechaDeAlta(Date fechaDeAlta) {
        this.fechaDeAlta = fechaDeAlta;
    }

    public Date getFechaDeBaja() {
        return fechaDeBaja;
    }

    public void setFechaDeBaja(Date fechaDeBaja) {
        this.fechaDeBaja = fechaDeBaja;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public Boolean getAlta() {
        return alta;
    }

    public void setAlta(Boolean alta) {
        this.alta = alta;
    }

    public Role getRol() {
        return rol;
    }

    public void setRol(Role rol) {
        this.rol = rol;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Foto getFotoDePerfil() {
        return fotoDePerfil;
    }

    public void setFotoDePerfil(Foto fotoDePerfil) {
        this.fotoDePerfil = fotoDePerfil;
    }

}
