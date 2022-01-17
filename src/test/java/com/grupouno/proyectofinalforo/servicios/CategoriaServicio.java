package com.grupouno.proyectofinalforo.servicios;

import com.grupouno.proyectofinalforo.entidades.Categoria;
import com.grupouno.proyectofinalforo.excepciones.WebException;
import com.grupouno.proyectofinalforo.repositorios.CategoriaRepositorio;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoriaServicio {

    @Autowired
    private CategoriaRepositorio categoriaRepositorio;
    
    public List<Categoria> listAll(){
        return categoriaRepositorio.findAll();
    }
    
    public Categoria findById(String id) throws WebException {
        
        Optional<Categoria> optional = categoriaRepositorio.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new WebException("La categoría que busca no se encuentra en la base de datos");
        }        
    }
    

    @Transactional
    public Categoria crearCategoria(String nombre, String descripcion) throws WebException {

        validar(nombre);

        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        categoria.setAlta(new Date());

        return categoriaRepositorio.save(categoria);
    }

    @Transactional
    public Categoria modificarCategoria(String id, String nombre, String descripcion) throws WebException {

        validar(nombre);

        Optional<Categoria> o = categoriaRepositorio.findById(id);
        if (o.isPresent()) {
            Categoria categoria = o.get();
            categoria.setNombre(nombre);
            categoria.setDescripcion(descripcion);

            return categoriaRepositorio.save(categoria);
        } else {
            throw new WebException("No se encontró la categoría solicitada.");
        }
        //que pasa si solo quiero modificar un atributo (nombre o descripcion)
    }

    @Transactional
    public Categoria bajaCat(String id) throws WebException {
        Optional<Categoria> optional = categoriaRepositorio.findById(id);
        if (optional.isPresent()) {
            Categoria categoria = optional.get();
            categoria.setBaja(new Date());

            return categoriaRepositorio.save(categoria);
        } else {
            throw new WebException("No se encontró la categoría solicitada.");
        }
    }

    @Transactional
    public Categoria altaCat(String id) throws WebException {
        Optional<Categoria> optional = categoriaRepositorio.findById(id);
        if (optional.isPresent()) {
            Categoria categoria = optional.get();
            categoria.setBaja(null);

            return categoriaRepositorio.save(categoria);
        } else {
            throw new WebException("No se encontró la categoría solicitada.");
        }
    }

    private void validar(String nombre) throws WebException {
        if (nombre == null || nombre.isEmpty()) {
            throw new WebException("El nombre de la categoría debe completarse.");
        }
        if (categoriaRepositorio.findByName(nombre) != null) {
            throw new WebException("Ya existe una categoria con ese nombre.");
        }
        //nombre sin espacios, indistinto mayus/minus
    }
}
