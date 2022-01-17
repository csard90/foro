package com.grupouno.proyectofinalforo.controladores;

import com.grupouno.proyectofinalforo.entidades.Hilo;
import com.grupouno.proyectofinalforo.entidades.Mensaje;
import com.grupouno.proyectofinalforo.entidades.Usuario;
import com.grupouno.proyectofinalforo.excepciones.WebException;
import com.grupouno.proyectofinalforo.servicios.HiloServicio;
import com.grupouno.proyectofinalforo.servicios.MensajeServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mensajes")
public class MensajeControlador {

    @Autowired
    private MensajeServicio mensajeServicio;

    @Autowired
    private HiloServicio hiloServicio;

    @GetMapping("/discusion")

    public String mostrarHilo(HttpSession httpSession, ModelMap model, @RequestParam(required = true) String id, @RequestParam(required = false) String idMensaje) throws WebException {        
        Hilo hilo = hiloServicio.findById(id);
        List<Mensaje> mensajes = mensajeServicio.buscarMensajesPorIdDeHilo(id);
        if (!mensajes.isEmpty()) {
            model.addAttribute("hilo", hilo);
            model.addAttribute("mensajes", mensajes);
            Mensaje mensaje = new Mensaje();
            if (idMensaje != null) {                        
                mensaje = mensajeServicio.buscarMensajePorId(idMensaje);
            } else {
                Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
                if(usuario != null){
                    mensaje.getUsuario().setId(usuario.getId()); 
                 }
                 mensaje.getHilo().setId(id);                        
            }
            model.addAttribute("mensaje", mensaje);
            return "discusion";
        } else {
            hiloServicio.darBajaHilo(id);
            return "redirect:/hilos?id=" + hilo.getCategoria().getId();
        }        
    }

    // RECIBE EL OBJETO MENSAJE DESDE EL HTML DE DISCUSION CON LOS ATRIBUTOS MENSAJES, HILO (SOLO ID)  Y USUARIO (SOLO ID) CON DATOS (EN EL CASO DE CREAR NUEVO).
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/guardar")
    public String guardarMensaje(RedirectAttributes redirectAttributes, @ModelAttribute Mensaje mensaje) {
        try {
            mensajeServicio.guardarMensaje(mensaje);
        } catch (WebException ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("error", ex.getMessage()); // EN CASO DE EXCEPCION SE ENVIA EL MSJ DE LA EXCEPCION AL METODO QUE MUESTRA EL HILO
        }
        return "redirect:/mensajes/discusion?id=" + mensaje.getHilo().getId();
    }

    // EN ALTA Y BAJA TRABAJE CON ID PORQUE SE HACE A TRAVES DE UN BOTON (CREO) Y EL BOTON VA A TENER COMO LINK
    // LA URL DE LOS METODOS DE ABAJO + EL ID VIAJANDO A TRAVES DE ELLA.
    // BAJA / ALTA MENSAJE:
    // CUANDO SE PRESIONE EL BOTON DE BAJA / ALTA MENSAJE JUNTO A LA PETICION GET, EN LA URL VA A VIAJAR LA ID DEL USUARIO
    // QUE PRESIONO EL BOTON, A PARTIR DE ESA ID DAMOS DE BAJA / ALTA EL MENSAJE.
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/baja")
    public String darBajaMensaje(@RequestParam String id) throws WebException {
        Mensaje mensaje = mensajeServicio.darBajaMensajePorId(id);
        return "redirect:/mensajes/discusion?id=" + mensaje.getHilo().getId();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/alta")
    public String darAltaMensaje(@RequestParam String id) throws WebException {
        Mensaje mensaje = mensajeServicio.darAltaMensajePorId(id);
        return "redirect:/mensajes/discusion?id=" + mensaje.getHilo().getId();
    }

}
