package ir.payam1986128.smsfirewall.smscmock;

import ir.payam1986128.smsfirewall.core.presentation.sms.SmsRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SmscApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmscApplication.class, args);
    }

    @PostMapping("/submissions")
    public void submit(@RequestBody SmsRequest smsRequest) {
        var response = new Object();
    }
}
