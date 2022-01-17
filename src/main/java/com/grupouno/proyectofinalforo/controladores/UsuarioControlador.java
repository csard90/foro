package com.grupouno.proyectofinalforo.controladores;

import com.grupouno.proyectofinalforo.entidades.Mensaje;
import com.grupouno.proyectofinalforo.entidades.Usuario;
import com.grupouno.proyectofinalforo.excepciones.WebException;
import com.grupouno.proyectofinalforo.servicios.HiloServicio;
import com.grupouno.proyectofinalforo.servicios.MensajeServicio;
import com.grupouno.proyectofinalforo.servicios.UsuarioServicio;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuario")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private HiloServicio hiloServicio;

    @Autowired
    private MensajeServicio mensajeServicio;

    @GetMapping("/registro")
    public String crearUsuario(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            return "redirect:/categorias";
        }
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/guardar")
    public String save(RedirectAttributes redirectAttribute, MultipartFile archivo, HttpSession session, Model model, @ModelAttribute Usuario usuario, @RequestParam(required = true) String contrasena2) {
        try {
            //Si el session es distinto de null, redireccionar al inicio
            Usuario usuarioActivo = (Usuario) session.getAttribute("usuario");
            if (usuarioActivo != null) {
                redirectAttribute.addFlashAttribute("error", "Operación no válida");
                return "redirect:/categorias";
            }
            //Se crea el usuario mediante el metodo Save de usuarioServicio, necesita la segunda contraseña y el objeto usuario
            usuarioServicio.save(session, archivo, usuario, contrasena2);
            redirectAttribute.addFlashAttribute("success", "Se ha registrado con éxito.");
        } catch (WebException ex) {
            //Si no lo encuentra entonces lanza el mensaje de error apropiado, especificados en el metodo Save
            ex.printStackTrace();
            model.addAttribute("usuario", usuario);
            model.addAttribute("error", ex.getMessage());
            return "registro";
        }
        //Retorna a la pagina de registro por el momento, debería retornar al index con un mensaje de exito. "redirect:/inicio" y RedirectAttributes
        return "redirect:/categorias";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/perfil")
    public String perfil(HttpSession sesion, @RequestParam(required = false) String id, Model model) {
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");
        if (id == null) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("hilos", hiloServicio.buscarHilosPorIdDePersona(usuario.getId()));
            model.addAttribute("id", usuario.getId());
        } else {
            model.addAttribute("hilos", hiloServicio.buscarHilosPorIdDePersona(id));
            model.addAttribute("usuario", usuarioServicio.findById(id));
            model.addAttribute("id", id);
        }

        return "perfil-hilos";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/perfil-mensaje")
    public String perfilMensaje(HttpSession sesion, @RequestParam(required = false) String id, Model model) {
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");
        if (id == null) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("mensajes", mensajeServicio.buscarMensajesPorIdDePerfil(usuario.getId()));
            model.addAttribute("id", usuario.getId());
        } else {
            model.addAttribute("mensajes", mensajeServicio.buscarMensajesPorIdDePerfil(id));
            model.addAttribute("usuario", usuarioServicio.findById(id));
            model.addAttribute("id", id);
        }
        return "perfil-hilos";
    }

//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/perfil-mensaje")
//    public String perfil(HttpSession sesion, @RequestParam(required=false) String id, Model model,@RequestParam(required=false) String idPerfil) {
//        Usuario usuario = (Usuario) sesion.getAttribute("usuario");
//        if(id==null){
//          model.addAttribute("usuario", usuario);
//        } else {
//          model.addAttribute("usuario", usuarioServicio.findById(id));
//        }
//        return "perfil-hilos";
//    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/editar-usuario")
    public String editar(@RequestParam(required = true) String id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (id.equals(usuario.getId())) {
            model.addAttribute("usuario", usuario);
            return "editar-usuario";
        }
        return "redirect:/categorias";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/personalizar")
    public String editNonSensitive(RedirectAttributes success, MultipartFile archivo, HttpSession session, Model model, @ModelAttribute Usuario usuario) {
        try {
            Usuario usuarioActivo = (Usuario) session.getAttribute("usuario");
            if (!usuarioActivo.getId().equals(usuario.getId())) {
                return "redirect:/categorias";
            }
            success.addFlashAttribute("success", "Se ha editado su información con éxito.");
            usuarioServicio.editNonSensitive(usuario, archivo, session);
        } catch (WebException ex) {
            ex.printStackTrace();
            model.addAttribute("usuario", usuario);
            model.addAttribute("error", ex.getMessage());
            return "editar-usuario";
        }
        return "redirect:/categorias";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/editar-contrasena")
    public String cambiarContrasena(@RequestParam(required = true) String id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (id.equals(usuario.getId())) {
            model.addAttribute("id", id);
            return "editar-contrasena";
        }
        return "redirect:/categorias";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/cambiar-contrasena")
    public String changePassword(RedirectAttributes success, HttpSession sesion, Model model, @RequestParam(required = true) String contrasenaOriginal, @RequestParam(required = true) String contrasenaNueva, @RequestParam(required = true) String verificacion, @RequestParam(required = true) String id) {
        try {
            Usuario usuarioActivo = (Usuario) sesion.getAttribute("usuario");
            if (!usuarioActivo.getId().equals(id)) {
                return "redirect:/categorias";
            }
            success.addFlashAttribute("success", "La contraseña ha sido actualizada exitosamente");
            usuarioServicio.changePassword(usuarioActivo, sesion, contrasenaOriginal, contrasenaNueva, verificacion);
        } catch (WebException ex) {
            ex.printStackTrace();
            model.addAttribute("error", ex.getMessage());
            return "editar-contrasena";
        }
        return "redirect:/categorias";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/editar-nombre")
    public String cambiarNombre(@RequestParam(required = true) String id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (id.equals(usuario.getId())) {
            model.addAttribute("id", id);
            return "editar-nombre";
        }
        return "redirect:/categorias";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/cambiar-nombre")
    public String changeUsername(RedirectAttributes redirect, Model model, HttpSession sesion, @RequestParam(required = true) String nombreDeUsuario, @RequestParam(required = true) String contrasenaOriginal, @RequestParam(required = true) String id) {
        try {
            Usuario usuarioActivo = (Usuario) sesion.getAttribute("usuario");
            if (!usuarioActivo.getId().equals(id)) {
                redirect.addFlashAttribute("error", "La operación no se pudo realizar");
                return "redirect:/categorias";
            }
            redirect.addFlashAttribute("success", "El Nombre de Usuario ha sido actualizado exitosamente");
            usuarioServicio.changeUsername(usuarioActivo, sesion, nombreDeUsuario, contrasenaOriginal);
        } catch (WebException ex) {
            ex.printStackTrace();
            model.addAttribute("error", ex.getMessage());
            return "editar-usuario";
        }
        return "redirect:/categorias";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/editar-email")
    public String cambiarEmail(@RequestParam(required = true) String id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (id.equals(usuario.getId())) {
            model.addAttribute("id", id);
            return "editar-email";
        }
        return "redirect:/categorias";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/cambiar-email")
    public String changeEmail(RedirectAttributes success, Model model, HttpSession sesion, @RequestParam(required = true) String contrasenaOriginal, @RequestParam(required = true) String email, @RequestParam(required = true) String id) {
        try {
            Usuario usuarioActivo = (Usuario) sesion.getAttribute("usuario");
            if (!usuarioActivo.getId().equals(id)) {
                return "redirect:/";
            }
            success.addFlashAttribute("success", "El E-mail ha sido actualizado exitosamente");
            usuarioServicio.changeEmail(usuarioActivo, sesion, contrasenaOriginal, email);
        } catch (WebException ex) {
            ex.printStackTrace();
            model.addAttribute("error", ex.getMessage());
            return "editar-email";
        }
        return "redirect:/categorias";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/baja-usuario")
    public String darBaja(@RequestParam(required = true) String id, HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (id.equals(usuario.getId())) {
            return "baja-usuario";
        }
        return "redirect:/categorias";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/baja")
    public String makeUserUnavailable(@RequestParam(required = true) String contrasena, HttpSession sesion, Model model) {
        try {
            Usuario usuario = (Usuario) sesion.getAttribute("usuario");
            usuarioServicio.setUnactive(sesion, usuario, contrasena);
        } catch (WebException ex) {
            ex.printStackTrace();
            model.addAttribute("error", ex.getMessage());
            return "baja-usuario";
        }
        return "redirect:/";
    }
}
