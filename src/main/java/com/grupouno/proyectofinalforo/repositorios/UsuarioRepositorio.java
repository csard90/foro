package com.grupouno.proyectofinalforo.repositorios;

import com.grupouno.proyectofinalforo.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, String> {
    //Buscar usuario en la base de datos por nombre
    @Query("select u from Usuario u where u.nombreDeUsuario= :nombreDeUsuario")
    Usuario findByUsername(@Param("nombreDeUsuario") String nombreDeUsuario);
    //Buscar un usuario con el email en la base de datos
    @Query("select u from Usuario u where u.email= :email")
    Usuario findByEmail(@Param("email") String email);
}
