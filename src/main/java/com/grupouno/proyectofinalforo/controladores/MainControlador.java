package com.grupouno.proyectofinalforo.controladores;

import com.grupouno.proyectofinalforo.entidades.Mensaje;
import com.grupouno.proyectofinalforo.entidades.Usuario;
import com.grupouno.proyectofinalforo.servicios.MensajeServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
public class MainControlador {

    @Autowired
    MensajeServicio mensajeServicio;

    @GetMapping("/")
    public String inicio() {
        return "redirect:/categorias";
    }

    @GetMapping("/loginsuccess")
    public String loginSuccess(RedirectAttributes redirect) {
        redirect.addFlashAttribute("success", "Ha iniciado sesión con éxito");
        return "redirect:/categorias";
    }

    @PostMapping("/search")
    public String busqueda(Model model, RedirectAttributes redirect, @RequestParam String busqueda) {
        if (busqueda == null || busqueda.isEmpty()) {
            redirect.addFlashAttribute("error", "El campo de busqueda no puede estar vacío, reintente.");
            return "redirect:/categorias";
        } else {
            List<Mensaje> mensajes = mensajeServicio.busqueda(busqueda);
            if (mensajes.isEmpty()) {
                model.addAttribute("vacio", "No hay resultados compatibles para su búsqueda");
            } else {
                model.addAttribute("lista", mensajeServicio.busqueda(busqueda));
            }
        }
        return "buscador";
    }
}
