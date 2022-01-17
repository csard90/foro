package com.grupouno.proyectofinalforo.controladores;

import com.grupouno.proyectofinalforo.entidades.Categoria;
import com.grupouno.proyectofinalforo.excepciones.WebException;
import com.grupouno.proyectofinalforo.servicios.CategoriaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categorias")
public class CategoriaControlador {

    @Autowired
    private CategoriaServicio categoriaServicio;

    @GetMapping("")
    public String listarCategorias(Model model) {
        model.addAttribute("categorias", categoriaServicio.listAllAvailable());
        return "inicio";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/crear-editar")
    public String crearCategoria(RedirectAttributes redirectAttributes, Model model, @RequestParam(required = false) String id) {
        if (id != null) {
            try {
                model.addAttribute("categoria", categoriaServicio.findById(id));
                //modificar
            } catch (WebException ex) {
                ex.printStackTrace();
                redirectAttributes.addFlashAttribute("error", ex.getMessage());
                return "redirect:/categorias";
            }
        } else {
            model.addAttribute("categoria", new Categoria());
        }
        return "cargar-categorias";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/save")
    public String guardarCategoria(RedirectAttributes redirectAttributes, @ModelAttribute Categoria categoria) {
        try {
            if (categoria.getId() == null || categoria.getId().isEmpty()) {
                categoriaServicio.crearCategoria(categoria.getNombre(), categoria.getDescripcion());
            } else {
                categoriaServicio.modificarCategoria(categoria.getId(), categoria.getNombre(), categoria.getDescripcion());
            }
            //La categoría fue creada con éxito
        } catch (WebException ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/categorias";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/baja-categoria")
    public String bajaCategoria(RedirectAttributes redirect, @RequestParam(required = true) String idCategoria) {
        try {
            categoriaServicio.bajaCat(idCategoria);
        } catch (WebException ex) {
            ex.printStackTrace();
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/categorias";
    }
}
