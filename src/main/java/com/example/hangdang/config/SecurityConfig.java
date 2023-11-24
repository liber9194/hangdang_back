package com.example.hangdang.config;


import com.example.hangdang.jwt.JwtUtil;
import com.example.hangdang.security.JwtAuthenticationFilter;
import com.example.hangdang.security.JwtAuthorizationFilter;
import com.example.hangdang.security.UserDetailsServiceImpl;
//import com.example.hangdang.jwt.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        filter.setFilterProcessesUrl("/api/login");
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }


    /*
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).cors(withDefaults());

        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                        .requestMatchers(new AntPathRequestMatcher("/")).permitAll() // 메인 페이지 요청 허가
                        .requestMatchers(new AntPathRequestMatcher("/api/users/**")).permitAll() // '/api/users/'로 시작하는 요청 모두 접근 허가
                        .requestMatchers(new AntPathRequestMatcher("GET")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("POST    ")).permitAll()// GET 요청 허가
                        .requestMatchers(new AntPathRequestMatcher("PUT")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("DELETE")).permitAll()
                        .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );



        // 필터 관리
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }*/

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        //http.exceptionHandling((exceptionHandling) -> exceptionHandling.authenticationEntryPoint(jwtAuthenticationEntryPoint));
        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        // 한줄로 허용하기
                        .requestMatchers("/swagger-resources/**",
         "/webjars/**",
         "/v3/api-docs/**",
         "/swagger-ui/**",
         "/swagger-ui.html")
         .permitAll()
         .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
         .requestMatchers("/api/login", "/api/signup").permitAll() // 로그인 및 회원가입 경로 인증 제외
         //                        .requestMatchers("/api/users", "/api/users/**“).permitAll() // ‘/api/rollingpapers/’로 시작하는 요청 모두 접근 허가
         .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll() // GET 요청에 대해 허용
         //.requestMatchers(HttpMethod.PATCH, “/api/courses/{courseId}/update”).authenticated() // PATCH 요청에 대해 인증 필요
         .requestMatchers("/", "/favicon.ico").permitAll()
         .anyRequest().authenticated() // 그 외 모든 요청 인증처리
         );
         // 필터 관리
         http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
         http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
         return http.build();
         }
/*
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000", // 로컬 서버
                "http://52.79.234.77" // IP 주소
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "Access-Control-Allow-Origin"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setMaxAge(1800L);
        //configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }*/
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
         return source;
         }
}


//    @Bean
//    public SecurityFilterChain otherSecurityFilterChain(HttpSecurity http) throws Exception
//    {
//        http
//                .csrf((csrf) -> csrf.disable());
//
//        return http.build();
//
//    }