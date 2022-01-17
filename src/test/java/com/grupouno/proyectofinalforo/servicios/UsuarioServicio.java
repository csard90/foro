package com.grupouno.proyectofinalforo.servicios;

import com.grupouno.proyectofinalforo.entidades.Usuario;
import com.grupouno.proyectofinalforo.enumeraciones.Role;
import com.grupouno.proyectofinalforo.excepciones.WebException;
import com.grupouno.proyectofinalforo.repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    //Para guardar usuario nuevo
    //Campos Obligatorios - Nombre De Usuario, Email, Contraseña (contrasena), Confirmar Contraseña (contrasena2), Genero y Fecha de Nacimiento
    //Clase WebException creada por Martin, sirve para mandar excepciones de carga y modificación
    //Contrasena 2 sirve para asegurar la contraseña correcta, se ingresa la original y la duplicada para evitar error de usuario
    @Transactional
    public Usuario save(HttpSession sesion, Usuario usuario, String contrasena2) throws WebException {
        //Chequea si el Usuario ya está registrado
        if (usuarioRepositorio.findByUsername(usuario.getNombreDeUsuario()) != null) {
            throw new WebException("El Nombre de Usuario pertenece a otra cuenta. Reintente.");
        }
        if (usuario.getNombreDeUsuario().isEmpty() || usuario.getNombreDeUsuario() == null) {
            throw new WebException("El campo de Nombre de Usuario no puede estar vacío. Reintente.");
        }
        //Si el largo es menor  a 6 entonces lanza excepcion
        if (usuario.getNombreDeUsuario().length() < 6 && usuario.getNombreDeUsuario().length() > 12) {
            throw new WebException("El Nombre de Usuario debe tener entre 6 y 12 caracteres. Reintente.");
        }
        if (usuario.getContrasena().isEmpty() || usuario.getContrasena() == null) {
            throw new WebException("El campo de Contraseña no puede estar vacío. Reintente.");
        }
        if (usuario.getContrasena().length() < 6) {
            throw new WebException("La Contraseña debe tener al menos 6 caracteres. Reintente.");
        }
        //Si la contraseña de confirmación está vacía, es nula o no es igual a la primera contraseña, lanza excepción
        if (contrasena2 == null || contrasena2.isEmpty() || !usuario.getContrasena().equals(contrasena2)) {
            throw new WebException("Las Contraseñas ingresadas no coinciden. Reintente.");
        }

        if (usuarioRepositorio.findByEmail(usuario.getEmail()) != null) {
            throw new WebException("El E-mail ingresado pertenece a otra cuenta. Reintente.");
        }
        if (usuario.getEmail().isEmpty() || usuario.getEmail() == null) {
            throw new WebException("El campo de E-Mail no puede estar vacío. Reintente.");
        }

        usuario.setAlta(true);

        if (usuario.getFechaDeAlta() == null) {
            usuario.setFechaDeAlta(new Date());
            usuario.setFechaDeBaja(null);
        }
        //Encriptador de contraseña
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        usuario.setContrasena(encoder.encode(usuario.getContrasena()));
        //Seteamos Rol Usuario al crear - RECORDAR: el primer usuario se le setea el rol administrador desde MySQL Workbench
        usuario.setRol(Role.USER);
        return usuarioRepositorio.save(usuario);
    }

    @Transactional
    public Usuario editNonSensitive(Usuario usuario, HttpSession sesion) throws WebException {
        verifyUser(sesion, usuario);
        if (usuario.getFechaNacimiento() == null || usuario.getFechaNacimiento().after(new Date())) {
            throw new WebException("La Fecha de Nacimiento ingresada no es válida. Reintente.");
        }

        if (usuario.getGenero().isEmpty() || usuario.getGenero() == null) {
            throw new WebException("El campo de Género no puede estar vacío. Reintente.");
        }

        return usuarioRepositorio.save(usuario);
    }

    //No tengo necesidad de usar un usuario
    @Transactional
    public Usuario changePassword(Usuario usuarioActivo, HttpSession sesion, String contrasenaOriginal, String contrasenaNueva, String verificacion) throws WebException {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (contrasenaOriginal == null || contrasenaOriginal.isEmpty() || !encoder.matches(contrasenaOriginal, usuarioActivo.getContrasena())) {
            throw new WebException("La Contraseña proporcionada no es válida. Reintente.");
        }
        if (verificacion == null || verificacion.isEmpty() || contrasenaNueva == null || contrasenaNueva.isEmpty() || !contrasenaNueva.equals(verificacion)) {
            throw new WebException("Las Contraseñas ingresadas no coinciden. Reintente.");
        }
        
        usuarioActivo.setContrasena(encoder.encode(contrasenaNueva));
        sesion.setAttribute("usuario", usuarioActivo);
        return usuarioRepositorio.save(usuarioActivo);
    }

    @Transactional
    public Usuario changeUsername(Usuario usuarioActivo, HttpSession sesion, String nombreDeUsuario, String contrasenaOriginal) throws WebException {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (usuarioRepositorio.findByUsername(nombreDeUsuario) != null) {
            throw new WebException("El Nombre de Usuario ingresado pertenece a otra cuenta. Reintente");
        }
        if (contrasenaOriginal == null || contrasenaOriginal.isEmpty() || !encoder.matches(contrasenaOriginal, usuarioActivo.getContrasena())) {
            throw new WebException("La Contraseña proporcionada no es válida. Reintente.");
        }
        if (nombreDeUsuario.length() < 6 && nombreDeUsuario.length() > 12) {
            throw new WebException("El Nombre de Usuario debe tener entre 6 y 12 caracteres. Reintente.");
        }
        usuarioActivo.setNombreDeUsuario(nombreDeUsuario);
        sesion.setAttribute("usuario", usuarioActivo);
        return usuarioRepositorio.save(usuarioActivo);
    }

    @Transactional
    public Usuario changeEmail(Usuario usuarioActivo, HttpSession sesion, String contrasenaOriginal, String email) throws WebException {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (usuarioRepositorio.findByEmail(email) != null) {
            throw new WebException("El E-mail ingresado pertenece a otra cuenta. Reintente.");
        }
        if (contrasenaOriginal == null || contrasenaOriginal.isEmpty() || !encoder.matches(contrasenaOriginal, usuarioActivo.getContrasena())) {
            throw new WebException("La Contraseña proporcionada no es válida. Reintente.");
        }
        usuarioActivo.setEmail(email);
        sesion.setAttribute("usuario", usuarioActivo);
        return usuarioRepositorio.save(usuarioActivo);
    }

    @Transactional
    public Usuario setUnactive(HttpSession sesion, Usuario usuario) throws WebException {
        verifyUser(sesion, usuario);
        usuario.setFechaDeBaja(new Date());
        usuario.setFechaDeAlta(null);
        usuario.setAlta(false);
        return usuarioRepositorio.save(usuario);
    }

    @Transactional
    public Usuario setActive(HttpSession sesion, Usuario usuario) throws WebException {
        verifyUser(sesion, usuario);
        usuario.setFechaDeBaja(null);
        usuario.setFechaDeAlta(new Date());
        usuario.setAlta(true);
        return usuarioRepositorio.save(usuario);
    }

    public Usuario verifyUser(HttpSession sesion, Usuario usuario) throws WebException {
        Usuario autenticado = (Usuario) sesion.getAttribute("usuario");
        if (!autenticado.getId().equals(usuario.getId())) {
            throw new WebException("Operación no válida, reintente.");
        }
        return autenticado;
    }

    //Para testear la carga y para utilizar el panel de control de Admin. Asignar roles, dar de alta/baja usuarios, borrar usuarios, 
    public List<Usuario> listAll() {
        return usuarioRepositorio.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String nombreDeUsuario) throws UsernameNotFoundException {
        try {
            Usuario usuario = usuarioRepositorio.findByUsername(nombreDeUsuario);
            User user;
            List<GrantedAuthority> permisos = new ArrayList<>();
            if (usuario.getRol() == Role.USER) {
                //Agregar roles de usuario
                permisos.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()));
            } else {
                permisos.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()));
            }
            if(usuario.getFechaDeAlta()!=null){
                throw new WebException("No se permite loguear con cuentas dadas de baja");
            }
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuario", usuario);
            return new User(nombreDeUsuario, usuario.getContrasena(), permisos);
        } catch (Exception e) {
            throw new UsernameNotFoundException("El Usuario no existe en la base de datos. Reintente.");
        }
    }
}
