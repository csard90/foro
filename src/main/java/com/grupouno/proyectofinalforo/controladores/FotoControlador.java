package com.grupouno.proyectofinalforo.controladores;

import com.grupouno.proyectofinalforo.entidades.Usuario;
import com.grupouno.proyectofinalforo.excepciones.WebException;
import com.grupouno.proyectofinalforo.servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/foto")
public class FotoControlador {

    @Autowired
    UsuarioServicio usuarioServicio;

    @GetMapping("/perfil/{id}")
    public ResponseEntity<byte[]> fotoPerfil(@PathVariable String id) {
        try {
            Usuario usuario = usuarioServicio.findById(id);
            if (usuario.getFotoDePerfil() == null) {
                throw new WebException("El usuario no tiene una foto asignada");
            }
            byte[] foto = usuario.getFotoDePerfil().getContenido();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(foto, headers, HttpStatus.OK);
        } catch (WebException ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
