//package com.example.chatwebproject.config;
//
//import com.example.chatwebproject.security.jwt.AuthEntryPointJwt;
//import com.example.chatwebproject.security.jwt.AuthTokenFilter;
//import com.example.chatwebproject.security.service.UserDetailServiceImpl;
//import lombok.AllArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@AllArgsConstructor
//@EnableWebSecurity
//public class SecurityConfig {
//    private final UserDetailServiceImpl userDetailService;
//    private final AuthEntryPointJwt unauthorizedHandler;
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
//        authenticationProvider.setPasswordEncoder(passwordEncoder());
//        authenticationProvider.setUserDetailsService(userDetailService);
//
//        return authenticationProvider;
//    }
//
//    @Bean
//    public AuthTokenFilter authTokenFilter(){
//        return new AuthTokenFilter();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
//        http
//                .cors().and()
//                .csrf().disable()
//                .exceptionHandling().authenticationEntryPoint(this.unauthorizedHandler).and()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//                .authorizeRequests()
//                .antMatchers("/ws/**").permitAll()
//                .antMatchers("/api/auth/**").permitAll()
////                .antMatchers("/api/test/**").hasAuthority(ERole.of(1).name())
//                .anyRequest().authenticated().and()
//                .authenticationProvider(authenticationProvider())
//                .addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
//
//}
