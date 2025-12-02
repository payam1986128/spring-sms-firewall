package ir.payam1986128.smsfirewall.virtualthread;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.form.spring.SpringFormEncoder;
import ir.payam1986128.smsfirewall.core.smscclient.SmscApi;
import ir.payam1986128.smsfirewall.core.smscclient.SmscApiErrorDecoder;
import ir.payam1986128.smsfirewall.virtualthread.filter.JwtTokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"ir.payam1986128.smsfirewall.core", "ir.payam1986128.smsfirewall.virtualthread"})
@EnableCouchbaseRepositories("ir.payam1986128.smsfirewall.virtualthread.repository")
@EntityScan(basePackages = "ir.payam1986128.smsfirewall.core.entity")
@EnableWebSecurity
@EnableMethodSecurity
@EnableTransactionManagement
@RequiredArgsConstructor
public class VirtualThreadApplication {
    private final JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Value("${app.smsc-api.url}")
    private String smscUrl;

    public static void main(String[] args) {
        SpringApplication.run(VirtualThreadApplication.class, args);
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
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(
                        req -> req.requestMatchers(
                                        "/api/bo/users/*",
                                        "/api/bo/users",
                                        "/api/protections",
                                        "/api/protections/*"
                                ).permitAll()
                                .anyRequest().authenticated()
                ).userDetailsService(userDetailsService)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        e -> e.accessDeniedHandler(
                                        (request, response, accessDeniedException) -> response.setStatus(403)
                                )
                                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
