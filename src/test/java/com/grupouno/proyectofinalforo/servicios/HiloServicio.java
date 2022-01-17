package com.grupouno.proyectofinalforo.servicios;

import com.grupouno.proyectofinalforo.entidades.Categoria;
import com.grupouno.proyectofinalforo.entidades.Hilo;
import com.grupouno.proyectofinalforo.entidades.Mensaje;
import com.grupouno.proyectofinalforo.entidades.Usuario;
import com.grupouno.proyectofinalforo.excepciones.WebException;
import com.grupouno.proyectofinalforo.repositorios.CategoriaRepositorio;
import com.grupouno.proyectofinalforo.repositorios.HiloRepositorio;
import com.grupouno.proyectofinalforo.repositorios.UsuarioRepositorio;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HiloServicio {

    @Autowired
    private HiloRepositorio hiloRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private CategoriaRepositorio categoriaRepositorio;

    @Autowired
    private MensajeServicio mensajeServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Transactional
    public Hilo guardarHilo(Hilo hilo, String mensaje) throws WebException {

        if (mensaje == null || mensaje.isEmpty()) {
            throw new WebException("El hilo debe tener un primer mensaje para ser creado");
        }

        if (hilo.getTitulo() == null || hilo.getTitulo().isEmpty()) {
            throw new WebException("El título del hilo no puede estar vacío o nulo");
        }

        if (hilo.getUsuario().getId() == null || hilo.getUsuario().getId().isEmpty()) {
            throw new WebException("El usuario del hilo debe tener un id");
        }

        if (hilo.getCategoria().getId() == null || hilo.getCategoria().getId().isEmpty()) {
            throw new WebException("La categoría del hilo debe tener un id");
        }

        hilo.setFechaInicio(new Date());
        hilo.setFechaActualizacion(new Date());
        hilo.setAlta(true);

        // seteamos los datos del usuario completo a partir del id que nos pasan del html
       
        Optional<Usuario> optionalUsuario = usuarioRepositorio.findById(hilo.getUsuario().getId());

        if (optionalUsuario.isPresent()) {
            hilo.setUsuario(optionalUsuario.get());
        } else {
            throw new WebException("El usuario no se encuentra en la base de datos");
        }

        // seteamos los datos de la categoria completa a partir del id que nos pasan del html 
        
        hilo.setCategoria(buscarCategoriaxId(hilo.getCategoria().getId()));

        return hiloRepositorio.save(hilo);          
    }

    public Hilo findById(String id) throws WebException {

        Optional<Hilo> optionalHilo = hiloRepositorio.findById(id);

        if (optionalHilo.isPresent()) {
            Hilo hilo = optionalHilo.get();
            return hilo;

        } else {
            throw new WebException("El hilo que busca no se encuentra en la base de datos");
        }
    }

    public List<Hilo>buscarHilosPorIdCategoria(String idCategoria){        
        return hiloRepositorio.buscarHilosPorIdCategoria(idCategoria);
    }
    
    public List<Hilo> listAll() {
        return hiloRepositorio.findAll();
    }

    //Este metodo tiene que estar en SERVICIOS CATEGORIA
    public Categoria buscarCategoriaxId(String idCategoria) throws WebException{
        
        Optional<Categoria> optionalCategoria = categoriaRepositorio.findById(idCategoria);
        
        if (optionalCategoria.isPresent()){
           
            return optionalCategoria.get();
            
        } else {
            throw new WebException("La categoría no se encuentra en la base de datos");
        }
          
    }
    
    public void aa(String id) throws WebException{
        Hilo hilo = findById(id);
        hilo.setAlta(false);
       
         hiloRepositorio.save(hilo);
    }
    
    public void jaja(){
        
        
    }
}
