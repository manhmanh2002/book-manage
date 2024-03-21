package com.thuctap.bookmanage.config;

import com.thuctap.bookmanage.service.UserService;
import com.thuctap.bookmanage.service.customuser.CustomSuccessHandler;
import com.thuctap.bookmanage.service.customuser.CustomUserDetailsService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    private final CustomSuccessHandler customSuccessHandler;
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/",
                            "/register",
                            "/registration",
                            "/forgot_password",
                            "/Forgot_Password_Form",
                            "/read_book",
                            "/confirm_account",
                            "/book-reading",
                            "/home/**",
                            "/select_book",
                            "/data/**",
                            "/user-photos/**",
                            "/selectbook_author",
                            "/selectchapter/**",
                            "/index",
                            "/hot",
                            "/reset_password",
                            "selectCategory"

                        )
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .successHandler(customSuccessHandler)
                        .permitAll()
                )
                .logout((logout) -> logout
                                .logoutSuccessUrl("/logout")
                                .logoutSuccessHandler((request, response, authentication) -> {
                                    HttpSession session = request.getSession(false);
                                    if (session != null) {
                                        session.invalidate();
                                    }
                                    response.sendRedirect("/login?logout");
                                })
                                .permitAll()
                );
        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        authenticationProvider.setPasswordEncoder(this.passwordEncoder());
        return authenticationProvider;
    }

}
