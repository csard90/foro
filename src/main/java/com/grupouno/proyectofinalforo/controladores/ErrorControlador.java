package com.grupouno.proyectofinalforo.controladores;

import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ErrorControlador implements ErrorController {
    
    @RequestMapping(value = "error", method = {RequestMethod.GET, RequestMethod.POST})
    public String mostrarPaginaDeError(Model model, HttpServletRequest httpServletRequest){
        String mensajeDeError="";
        int codigoDeError= (int) httpServletRequest.getAttribute("javax.servlet.error.status_code");
        switch(codigoDeError){
            case 400:
                mensajeDeError= "El recurso solicitado no existe";
                break;
            case 401:
                mensajeDeError="No se encuentra autorizado";
                break;
            case 402:
                mensajeDeError="No tiene permisos para acceder a este recurso";
                break;
            case 403:
                mensajeDeError="El recurso solicitado no se ha encontrado";
                break;
            case 500:
                mensajeDeError="El servidor no pudo realizar la petición";
                break;
            default:
                
        }
        model.addAttribute("codigo", codigoDeError);
        model.addAttribute("mensaje", mensajeDeError);
        return "error";
    }
    
}
