package com.grupouno.proyectofinalforo.controladores;

import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/login")
public class LoginControlador {

    @GetMapping("")
    public String login(Principal principal, RedirectAttributes redirect, @RequestParam(required = false) String error, @RequestParam(required = false) String logout, Model model, @RequestParam(required = false) String nombreDeUsuario) {
        if(principal != null){
            return "redirect:/categorias";
        }
        if (error != null) {
            model.addAttribute("error", "Nombre de Usuario o Clave incorrectas");
        }
        if (logout != null) {
            redirect.addAttribute("success", "Ha salido correctamente");
        }
        if (nombreDeUsuario != null) {
            model.addAttribute("nombreDeUsuario", nombreDeUsuario);
        }
        return "login";
    }
}
