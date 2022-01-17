package com.grupouno.proyectofinalforo.repositorios;

import com.grupouno.proyectofinalforo.entidades.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepositorio extends JpaRepository<Categoria, String>{

//    ToDo
//           Buscar categoria por nombre
    @Query("SELECT c FROM Categoria c WHERE c.nombre = :nombre")
    public Categoria findByName(@Param("nombre") String nombre);
    
//           Buscar categoria dada de baja
    @Query("SELECT c FROM Categoria c WHERE c.baja = null ORDER BY c.nombre")
    public List<Categoria> listAllAvailable();
}
