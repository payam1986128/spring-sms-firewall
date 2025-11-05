package ir.payam1986128.smsfirewall.webflux.endpoint.rest.bo;

import ir.payam1986128.smsfirewall.core.presentation.sms.SmsFilterRequest;
import ir.payam1986128.smsfirewall.core.presentation.sms.SmsResponse;
import ir.payam1986128.smsfirewall.webflux.service.SmsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping("/api/bo/sms")
@Validated
public class SmsController {
    private final SmsService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<SmsResponse> getSms(SmsFilterRequest request) {
        return service.getSms(request);
    }

}
