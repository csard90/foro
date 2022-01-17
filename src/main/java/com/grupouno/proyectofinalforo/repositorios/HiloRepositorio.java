package com.grupouno.proyectofinalforo.repositorios;

import com.grupouno.proyectofinalforo.entidades.Hilo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HiloRepositorio extends JpaRepository<Hilo, String> {

    
    @Query("SELECT h FROM Hilo h WHERE h.categoria.id = :id and h.alta = true ORDER BY h.fechaActualizacion DESC") 
    public List<Hilo> buscarHilosPorIdCategoria(@Param("id") String idCategoria);

    
    @Query("SELECT h FROM Hilo h WHERE h.categoria.id = :id and h.alta = false ORDER BY h.fechaActualizacion DESC") 
    public List<Hilo> buscarHilosBajaPorIdCategoria(@Param("id") String idCategoria);
    
    @Query("SELECT h FROM Hilo h WHERE h.usuario.id = :id and h.alta = true ORDER BY h.fechaInicio DESC") 
    public List<Hilo> buscarHilosPorIdDePersona(@Param("id") String idPerfil);
}
