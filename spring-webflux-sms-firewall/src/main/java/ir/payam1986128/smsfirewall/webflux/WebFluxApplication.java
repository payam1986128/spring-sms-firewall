package ir.payam1986128.smsfirewall.webflux;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.form.spring.SpringFormEncoder;
import ir.payam1986128.smsfirewall.core.security.JwtTokenProvider;
import ir.payam1986128.smsfirewall.core.smscclient.SmscApi;
import ir.payam1986128.smsfirewall.core.smscclient.SmscApiErrorDecoder;
import ir.payam1986128.smsfirewall.webflux.filter.JwtTokenAuthenticationFilter;
import ir.payam1986128.smsfirewall.webflux.repository.UserRepository;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.data.couchbase.repository.config.EnableReactiveCouchbaseRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@SpringBootApplication
@EnableReactiveCouchbaseRepositories("ir.payam1986128.smsfirewall.core.repository")
@EnableWebFluxSecurity
@EnableTransactionManagement
public class WebFluxApplication {

    @Value("${app.smsc-api.url}")
    private String smscUrl;

    public static void main(String[] args) {
        SpringApplication.run(WebFluxApplication.class, args);
    }

    @Bean
    public SmscApi smscApi(ObjectMapper objectMapper) {
        ObjectFactory<HttpMessageConverters> messageConverters =
                () -> new HttpMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper));
        return Feign.builder()
                .contract(new SpringMvcContract())
                .encoder(new SpringFormEncoder(new SpringEncoder(messageConverters)))
                .decoder(new ResponseEntityDecoder(new SpringDecoder(messageConverters)))
                .errorDecoder(new SmscApiErrorDecoder())
                .target(SmscApi.class, smscUrl);
    }

    @Bean
    public SecurityWebFilterChain springWebFilterChain(
            ServerHttpSecurity http,
            JwtTokenProvider tokenProvider,
            ReactiveAuthenticationManager reactiveAuthenticationManager
    ) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authenticationManager(reactiveAuthenticationManager)
                .authorizeExchange((authorize) ->
                        authorize.pathMatchers(HttpMethod.POST, "/api/bo/users/*").permitAll()
                                .pathMatchers(HttpMethod.POST, "/api/bo/users").permitAll()
                                .pathMatchers(HttpMethod.POST, "/api/protections").permitAll()
                                .pathMatchers(HttpMethod.POST, "/api/protections/*").permitAll()
                                .anyExchange().authenticated())
                .addFilterAt(new JwtTokenAuthenticationFilter(tokenProvider), SecurityWebFiltersOrder.HTTP_BASIC)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    CorsWebFilter corsWebFilter() {
        var corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "DELETE", "PUT", "POST"));
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository users) {
        return username -> users.findByUsername(username)
                .map(u -> User
                        .withUsername(u.getUsername())
                        .password(u.getPassword())
                        .authorities(AuthorityUtils.createAuthorityList(List.of("USER")))
                        .accountExpired(!u.isActive())
                        .credentialsExpired(!u.isActive())
                        .disabled(!u.isActive())
                        .accountLocked(!u.isActive())
                        .build()
                );
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(
            ReactiveUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        var authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }
}
