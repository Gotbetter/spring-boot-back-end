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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pcrc.gotbetter.user.login_method.jwt.config.JwtProvider;
import pcrc.gotbetter.user.login_method.jwt.config.JwtSecurityConfig;
import pcrc.gotbetter.user.login_method.jwt.config.handler.JwtAccessDeniedHandler;
import pcrc.gotbetter.user.login_method.jwt.config.handler.JwtAuthenticationEntryPointHandler;

@Configuration
@EnableWebSecurity
@EnableWebMvc
public class SecurityConfig implements WebMvcConfigurer {

    private final JwtProvider jwtProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPointHandler jwtAuthenticationEntryPointHandler;

    @Value("${external.frontend}")
    private String frontend;
    @Value("${external.backend}")
    private String backend;
    @Value("${external.backend.prod}")
    private String prodBackend;
    @Value("${external.localFront}")
    private String localFront;
    @Value("${external.localBack}")
    private String localBack;
    @Value("${external.localBack.prod}")
    private String prodLocalBack;

    @Autowired
    public SecurityConfig(JwtProvider jwtProvider, JwtAccessDeniedHandler jwtAccessDeniedHandler, JwtAuthenticationEntryPointHandler jwtAuthenticationEntryPointHandler) {
        this.jwtProvider = jwtProvider;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.jwtAuthenticationEntryPointHandler = jwtAuthenticationEntryPointHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        String[] uriPost = {"/users", "/users/verify", "/users/login", "/users/reissue", "/oauth"};
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
        configuration.addAllowedOrigin(prodBackend);
        configuration.addAllowedOrigin(prodLocalBack);
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

//    @Override
//    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {   // 기본 컨버터를 유지관리
//        converters.removeIf(v->v.getSupportedMediaTypes().contains(MediaType.APPLICATION_JSON));  // 기존 json용 컨버터 제거
//        converters.add(new MappingJackson2HttpMessageConverter());  // 새로 json 컨버터 추가. 필요시 커스텀 컨버터 bean 사용
//    }
}
