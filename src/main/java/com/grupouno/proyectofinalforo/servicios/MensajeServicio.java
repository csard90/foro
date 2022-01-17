package com.grupouno.proyectofinalforo.servicios;

import com.grupouno.proyectofinalforo.entidades.Hilo;
import com.grupouno.proyectofinalforo.entidades.Mensaje;
import com.grupouno.proyectofinalforo.entidades.Usuario;
import com.grupouno.proyectofinalforo.excepciones.WebException;
import com.grupouno.proyectofinalforo.repositorios.MensajeRepositorio;
import com.grupouno.proyectofinalforo.repositorios.UsuarioRepositorio;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class MensajeServicio {
    
    @Autowired
    private MensajeRepositorio mensajeRepositorio;
    
    @Autowired
    private HiloServicio hiloServicio;
    
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    @Transactional
    public void guardarMensaje(Mensaje mensaje) throws WebException{
        
        // COMO SOLO RECIBE MENSAJE, HILO (ID) Y USUARIO (ID) COMPLETADOS DESDE HTML UNICAMENTE SE VALIDAN ESOS 
        // CAMPOS.
        
        if (mensaje.getMensaje() == null || mensaje.getMensaje().isEmpty()) {
            throw new WebException("El mensaje ingresado no puede ser nulo o estar vacio.");
        }
        
        if (mensaje.getHilo().getId() == null || mensaje.getHilo().getId().isEmpty()) {
            throw new WebException("El mensaje que intenta guardar no está asignado a un Hilo.");
        }
        
        if (mensaje.getUsuario().getId() == null || mensaje.getHilo().getId().isEmpty()) {
            throw new WebException("El mensaje que intenta guardar no está asignado a un Usuario.");
        }
        
        
        // SI EL ID VIENE VACIO ES PORQUE ES UN OBJETO NUEVO QUE HAY QUE GUARDAR, POR ESO SE SETEA LA FECHA ACTUAL
        // Y EL ALTA = TRUE. TAMBIEN A PARTIR DE LA ID SE LE SETEA LA CATEGORIA A LA QUE PERTENECE Y EL USUARIO
        // QUE LO CREO                                        
        
        if (mensaje.getId() == null || mensaje.getId().isEmpty()) { // EL IF SIRVE PARA EL EDITAR
            mensaje.setFecha(new Date());
            mensaje.setAlta(true);
                                    
            mensaje.setHilo(hiloServicio.findById(mensaje.getHilo().getId()));            
            mensaje.getHilo().setFechaActualizacion(new Date()); 
            
            Optional<Usuario> optionalUsuario = usuarioRepositorio.findById(mensaje.getUsuario().getId());                                    
            if (optionalUsuario.isPresent()) {
                mensaje.setUsuario(optionalUsuario.get());
            } else {
                throw new WebException("El usuario que busca no se encuentra en la base de datos");
            }                          
        }                 
        
        // LA UUID SE LE COMPLETA AL HACERSE EL SAVE
        
        mensajeRepositorio.save(mensaje);
        
        // SI EL ID NO ESTA VACIO ES PORQUE YA TIENE UNA ID, POR LO TANTO ES UN OBJETO QUE YA EXISTE EN LA DB AL
        // QUE SE LE EDITO EL MENSAJE EN EL MISMO FORM, ASI QUE DIRECTAMENTE SE GUARDA.
                        
        
    }         
    
    @Transactional
    public void guardarPrimerMensaje(String mensaje, Hilo hilo) throws WebException{
        Mensaje mensajeAux = new Mensaje();
        mensajeAux.setMensaje(mensaje);
        mensajeAux.setHilo(hilo);
        mensajeAux.setUsuario(hilo.getUsuario());        
        guardarMensaje(mensajeAux);   
        System.out.println("mensajeHilo = " + mensajeAux.getHilo().getId());
        System.out.println();
    }
    
    @Transactional
    public Mensaje darBajaMensajePorId(String id) throws WebException{        
        Mensaje mensaje = buscarMensajePorId(id);
        mensaje.setAlta(false);
        return mensajeRepositorio.save(mensaje);        
    }
    
    @Transactional
    public Mensaje darAltaMensajePorId(String id) throws WebException{        
        Mensaje mensaje = buscarMensajePorId(id);
        mensaje.setAlta(true);
        return mensajeRepositorio.save(mensaje);
    }
    
 
    public List<Mensaje> buscarMensajesPorIdDeHilo(String id){
       return mensajeRepositorio.buscarMensajesPorIdDeHilo(id);
    }
    public List<Mensaje> buscarMensajesPorIdDePerfil(String id){
       return mensajeRepositorio.buscarMensajesPorIdDePerfil(id);
    }
    
    public Mensaje buscarMensajePorId(String id) throws WebException {
        
        Optional<Mensaje> optionalMensaje = mensajeRepositorio.findById(id);
        
        if (optionalMensaje.isPresent()) {
            return optionalMensaje.get();
        } else {
            throw new WebException("El mensaje que busca no se encuentra en la base de datos");
        }        
    }
    
    public Mensaje buscarPrimerMensajeDeHilo(String id){
        return mensajeRepositorio.buscarPrimerMensajeDeHilo(id);
    }
    
    public List<Mensaje> busqueda(String busqueda){
        return mensajeRepositorio.buscarMensajesPorContenido(busqueda);
    }
    
    public void darDeBajaMensajesPorIdHilo(String id){
        
        for (Mensaje mensaje :  buscarMensajesPorIdDeHilo(id)) {            
           
            mensaje.setAlta(false);
            mensajeRepositorio.save(mensaje);
        }  
    }
 
    public void darDeAltaMensajesPorIdHilo(String id){
        
        for (Mensaje mensaje :  mensajeRepositorio.buscarMensajesPorIdDeHiloAltaFalse(id)) {            
           
            mensaje.setAlta(true);
            mensajeRepositorio.save(mensaje);
        }  
    }
    
}
