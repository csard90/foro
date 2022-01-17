package com.grupouno.proyectofinalforo.servicios;

import com.grupouno.proyectofinalforo.entidades.Foto;
import com.grupouno.proyectofinalforo.repositorios.FotoRepositorio;
import java.io.IOException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FotoServicio {

    @Autowired
    FotoRepositorio fotoRepositorio;

    public Foto guardar(MultipartFile archivo) {
        if (archivo != null) {
            try {
                Foto foto = new Foto();
                foto.setMime(archivo.getContentType());
                foto.setNombre(archivo.getName());
                foto.setContenido(archivo.getBytes());
                return fotoRepositorio.save(foto); //Foto por defecto en el registro, oculta
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return null; //Nunca deberiamos llegar a este null
    }

    public Foto actualizar(String idFoto, MultipartFile archivo) {
        if (archivo != null) {
            try {

                Foto foto = new Foto();
                if (idFoto != null) {
                    Optional<Foto> respuesta = fotoRepositorio.findById(idFoto);
                    if(respuesta.isPresent()){
                        foto = respuesta.get();
                    }
                }
                foto.setMime(archivo.getContentType());
                foto.setNombre(archivo.getName());
                foto.setContenido(archivo.getBytes());
                return fotoRepositorio.save(foto); //Actualizar foto de perfil, con foto por defecto o nueva
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
