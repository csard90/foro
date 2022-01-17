package com.grupouno.proyectofinalforo.controladores;

import com.grupouno.proyectofinalforo.entidades.Hilo;
import com.grupouno.proyectofinalforo.entidades.Mensaje;
import com.grupouno.proyectofinalforo.entidades.Usuario;
import com.grupouno.proyectofinalforo.excepciones.WebException;
import com.grupouno.proyectofinalforo.servicios.HiloServicio;
import com.grupouno.proyectofinalforo.servicios.MensajeServicio;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@RequestMapping("/hilos")
public class HiloControlador {

    @Autowired
    private HiloServicio hiloServicio;

    @Autowired
    private MensajeServicio mensajeServicio;    

    // metodo para mostrar los hilos de una categoría 
    @GetMapping("")
    public String mostrarHilos(ModelMap model, @RequestParam(required = true) String id) throws WebException { // id de categoria que viaja por url
        model.addAttribute("categoria", hiloServicio.buscarCategoriaxId(id));    
        List<Hilo> hilos =  hiloServicio.buscarHilosPorIdCategoria(id);
        
        if(!hilos.isEmpty()){
        model.addAttribute("hilos", hilos); // busca hilos tambien a partir de id que viajo y la lanza a la vista
        } else {
         model.addAttribute("vacio", "Todavía no hay hilos creados en esta categoría");
        }
        return "hilos";
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/crear")
    public String crearHilo(HttpSession httpSession, ModelMap model, @RequestParam(required = false) String id, @RequestParam(required = false) String idHilo) throws WebException { // al clickear el boton de crear nuevo hilo debe viajar en la url la id de categoria                
        
        if(idHilo != null){     // CASO 1 LLEGA ID DE HILO = VA A EDITAR
            model.addAttribute("hilo", hiloServicio.findById(idHilo));  // PASA EL HILO A EDITAR
            model.addAttribute("mensaje", mensajeServicio.buscarPrimerMensajeDeHilo(idHilo));   //  PASA EL PRIMER MENSAJE DEL HILO     
        } else {    // CASO 2 LLEGA ID DE CATEGORIA = NUEVO HILO
            Usuario usuario = (Usuario) httpSession.getAttribute("usuario");            
            Hilo hilo = new Hilo();     
            hilo.getUsuario().setId(usuario.getId());   // SET DE ID DE USUARIO QUE VA A CREAR EL HILO        
            hilo.getCategoria().setId(id); // SET DE ID DE CATEGORIA A LA QUE PERTENECE EL NUEVO HILO
            model.addAttribute("hilo", hilo);
            model.addAttribute("mensaje", new Mensaje());
        }   
        return "crear-hilo_editar";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/guardar")
    public String guardarHilo(RedirectAttributes redirectAttributes, @ModelAttribute Hilo hilo, @RequestParam String mensaje) {
        try {
            if (hilo.getId().isEmpty()) {   // CASO 1= GUARDAR NUEVO HILO
                hilo = hiloServicio.guardarHilo(hilo, mensaje); // GUARDA NUEVO HILO
                mensajeServicio.guardarPrimerMensaje(mensaje, hilo); // GUARDA PRIMER NUEVO MENSAJE
                redirectAttributes.addFlashAttribute("sucess", "El hilo se creó correctamente");  // AGREGAR MENSAJE EXITO GUARDADO HILO                
            } else {
                hilo = hiloServicio.guardarHilo(hilo, mensaje); // CASO 2= GUARDA HILO CON TITULO EDITADO 
                Mensaje mensajeO = mensajeServicio.buscarPrimerMensajeDeHilo(hilo.getId()); // BUSCA PRIMER MENSAJE (DADO DE ALTA) DEL HILO
                mensajeO.setMensaje(mensaje);   // EDITA EL TEXTO DEL PRIMER MENSAJE
                mensajeServicio.guardarMensaje(mensajeO);   // GUARDA MENSAJE EN BASE DE DATOS
                redirectAttributes.addFlashAttribute("sucess", "El hilo se edito correctamente");                   
            }   
            return "redirect:/mensajes/discusion?id=" + hilo.getId();   // AMBOS CASOS EN CASO DE SER SATISFCATORIO REDIRIGEN A METODO QUE MUESTRA EL HILO NUEVO O EDITADO
        } catch (WebException ex) { // CASOS EXCEPCIONES
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("error", ex.getMessage());            
            if (hilo.getId().isEmpty()) {   // CASO 1= NUEVO HILO
                return "redirect:/hilos/crear?id=" + hilo.getCategoria().getId();   // REDIRIGE A METODO PARA CREAR NUEVO HILO PASANDO LA ID DE LA CATEGORIA
            } else {    // CASO 2 = HILO EDITADO
                return "redirect:/hilos/crear?idHilo=" + hilo.getId();  // REDIRIGE A METODO PARA EDITAR HILO PASANDO LA ID DEL HILO EN CUESTION
            }           
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/baja")
    public String dardeBajaHilo(@RequestParam(required = true) String id, RedirectAttributes redirectAtttibutes) throws WebException {

        Hilo hilo = hiloServicio.darBajaHilo(id);
        redirectAtttibutes.addFlashAttribute("sucess", "El hilo fue dado de baja.");

        return "redirect:/hilos?id=" + hilo.getCategoria().getId();
    }

    
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/alta")
    public String dardeAltaHilo(@RequestParam(required = true) String id, RedirectAttributes redirectAtttibutes) throws WebException {

        Hilo hilo = hiloServicio.darAltaHilo(id);
        redirectAtttibutes.addFlashAttribute("sucess", "El hilo fue dado de alta.");

        return "redirect:/hilos?id=" + hilo.getCategoria().getId();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/inactivos")
    public String darDeAltaHilos(@RequestParam(required = true) String id, ModelMap model) throws WebException{
        model.addAttribute("hilos", hiloServicio.buscarHilosBajaPorIdCategoria(id));    
         model.addAttribute("categoria", hiloServicio.buscarCategoriaxId(id));  
        return "hilos-baja";
    }
    
    
    
}


