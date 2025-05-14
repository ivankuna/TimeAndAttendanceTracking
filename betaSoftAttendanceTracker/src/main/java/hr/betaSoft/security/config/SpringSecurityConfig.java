package hr.betaSoft.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers("/register/**").permitAll()
                                .requestMatchers("/authorization").permitAll()
                                .requestMatchers("/pin/**").permitAll()
                                .requestMatchers("/js/**").permitAll()
                                .requestMatchers("/css/**").permitAll()
                                .requestMatchers("/icons/**").permitAll()
                                .requestMatchers("/backend/**").permitAll()
                                .requestMatchers("/backend/css/images/**").permitAll()
                                .requestMatchers("/access-denied").permitAll()
                                .requestMatchers("/users/**").hasRole("ADMIN")
                                .requestMatchers("/employees/**").hasRole("USER")
                                .requestMatchers("/absence-record/**").hasRole("USER")
                                .requestMatchers("/attendance/**").hasRole("USER")
                                .requestMatchers("/settings/**").hasRole("USER")
                                .requestMatchers("/pdf/**").hasRole("USER")
                                .requestMatchers("/html/**").hasRole("USER")
                                .requestMatchers("/attendance-html/**").hasRole("USER")
                                .requestMatchers("/absence-html/**").hasRole("USER")
                                .requestMatchers("/fund-hours-html/**").hasRole("USER")
                                .requestMatchers("/fund-hours-html-template/**").hasRole("USER")
                                .requestMatchers("/test/**").hasRole("USER")
                                .requestMatchers("/audio/**").permitAll()
                                .requestMatchers("/favicon.png").permitAll()
                                .requestMatchers("/favicon.ico").permitAll()
                                .requestMatchers("/**").hasRole("ADMIN")
                ).formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/authorization", true)
                                .permitAll()
                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .permitAll()
                ).exceptionHandling(configurer ->
                        configurer.accessDeniedPage("/login")
                );

        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
}