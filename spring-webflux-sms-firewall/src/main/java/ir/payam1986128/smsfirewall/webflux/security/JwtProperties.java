package ir.payam1986128.smsfirewall.webflux.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    private String secretKey;

    private long validityInMs;
}
