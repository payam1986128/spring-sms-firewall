package ir.payam1986128.smsfirewall.virtualthread.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix="security.jwt")
@Getter
@Setter
public class SecuritySkippedEndpoints {
    private List<String> skippedEndpoints;
}
