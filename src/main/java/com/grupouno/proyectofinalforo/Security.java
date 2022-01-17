package com.grupouno.proyectofinalforo;

import com.grupouno.proyectofinalforo.servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Security extends WebSecurityConfigurerAdapter {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        //Seteamos el tipo de encriptador para que pueda desencriptarlo a la hora de hacer el login
        auth.userDetailsService(usuarioServicio).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/css/*", "/img/*", "/js/*").permitAll()
                .and().formLogin()
                .loginPage("/login")
                .usernameParameter("nombreDeUsuario")
                .passwordParameter("contrasena")
                .defaultSuccessUrl("/loginsuccess")
                .loginProcessingUrl("/logincheck")
                .failureUrl("/login?error=error") //Este error tiene que tenerse en cuenta en thymeleaf para mostrarlo como un pop-up
                .permitAll()
                .and().logout()
                .logoutUrl("/logout") //El logout se hace mediante thymeleaf
                .logoutSuccessUrl("/categorias") //Y el logout successful también.
                .and().csrf().disable(); //Esta parte no la entendí, tiene que ver con usar páginas no seguras.
    }
}
