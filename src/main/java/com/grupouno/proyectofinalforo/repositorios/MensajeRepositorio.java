package com.grupouno.proyectofinalforo.repositorios;

import com.grupouno.proyectofinalforo.entidades.Mensaje;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MensajeRepositorio extends JpaRepository<Mensaje, String> {
    
    @Query("SELECT m FROM Mensaje m WHERE m.hilo.id = :id and m.alta = true ORDER BY m.fecha ASC")
    public List<Mensaje> buscarMensajesPorIdDeHilo(@Param("id") String id);

     @Query("SELECT m FROM Mensaje m WHERE m.hilo.id = :id and m.alta = false ORDER BY m.fecha ASC")
    public List<Mensaje> buscarMensajesPorIdDeHiloAltaFalse(@Param("id") String id);
      
    @Query("SELECT m FROM Mensaje m WHERE LOWER(m.mensaje) LIKE LOWER(CONCAT ('%', :busqueda ,'%')) and m.alta = true and m.hilo.alta = true ORDER BY m.fecha ASC")
    public List<Mensaje> buscarMensajesPorContenido(@Param("busqueda") String busqueda);
    
    @Query(value = "SELECT * FROM mensaje WHERE mensaje.hilo_id = :id and mensaje.alta = true ORDER BY mensaje.fecha ASC LIMIT 1", nativeQuery = true)
    public Mensaje buscarPrimerMensajeDeHilo(@Param("id") String id);
    
    @Query("SELECT m FROM Mensaje m WHERE m.usuario.id = :id and m.alta = true ORDER BY m.fecha ASC")
    public List<Mensaje> buscarMensajesPorIdDePerfil(@Param("id") String id);
}
