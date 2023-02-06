package pcrc.gotbetter.setting.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pcrc.gotbetter.setting.security.JWT.JwtProvider;
import pcrc.gotbetter.setting.security.JWT.JwtSecurityConfig;
import pcrc.gotbetter.setting.security.JWT.handler.JwtAccessDeniedHandler;
import pcrc.gotbetter.setting.security.JWT.handler.JwtAuthenticationEntryPointHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPointHandler jwtAuthenticationEntryPointHandler;

    @Value("${external.frontend}")
    private String frontend;
    @Value("${external.backend}")
    private String backend;
    @Value("${external.localFront}")
    private String localFront;
    @Value("${external.localBack}")
    private String localBack;

    @Autowired
    public SecurityConfig(JwtProvider jwtProvider, JwtAccessDeniedHandler jwtAccessDeniedHandler, JwtAuthenticationEntryPointHandler jwtAuthenticationEntryPointHandler) {
        this.jwtProvider = jwtProvider;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.jwtAuthenticationEntryPointHandler = jwtAuthenticationEntryPointHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        String[] uriPost = {"/users", "/users/verify", "/users/login", "/users/reissue"};
        String[] uriGet = {"/rules"};

        http.cors().configurationSource(corsConfigurationSource());
        http.httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST, uriPost).permitAll()
                .requestMatchers(HttpMethod.GET, uriGet).permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().disable()
                .rememberMe().disable()
                .logout().disable();
        http.exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPointHandler)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .and()
                .apply(new JwtSecurityConfig(jwtProvider));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin(frontend);
        configuration.addAllowedOrigin(backend);
        configuration.addAllowedOrigin(localFront);
        configuration.addAllowedOrigin(localBack);
        configuration.addAllowedHeader(localFront);
        configuration.addAllowedHeader(frontend);
        configuration.addAllowedOrigin("http://jxy.me");
        configuration.addAllowedHeader("http://jxy.me");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
