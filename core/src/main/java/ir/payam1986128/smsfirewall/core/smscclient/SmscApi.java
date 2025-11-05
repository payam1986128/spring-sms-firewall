package ir.payam1986128.smsfirewall.core.smscclient;

import ir.payam1986128.smsfirewall.core.presentation.sms.SmsRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface SmscApi {
    @PostMapping("/submissions")
    void submitSms(@RequestBody SmsRequest request);
}
