package ir.payam1986128.smsfirewall.webflux.endpoint.rest;

import ir.payam1986128.smsfirewall.core.entity.Sms;
import ir.payam1986128.smsfirewall.core.presentation.common.SuccessfulCreationDto;
import ir.payam1986128.smsfirewall.webflux.service.FirewallService;
import ir.payam1986128.smsfirewall.webflux.service.TransceiverService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/api/protections")
@AllArgsConstructor
public class SmsFirewallRestController {
    private final TransceiverService transceiverService;
    private final FirewallService firewallService;

    @PostMapping
    public Mono<SuccessfulCreationDto> receive(@RequestBody Sms sms) {
        return transceiverService.receive(sms)
                .flatMap(firewallService::protect)
                .flatMap(transceiverService::send)
                .map(s -> new SuccessfulCreationDto(s.getId().toString()));
    }

    @PostMapping("/async")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<SuccessfulCreationDto> receiveAsync(@RequestBody Sms sms) {
        return transceiverService.receive(sms)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(s -> firewallService.protect(s)
                        .flatMap(transceiverService::send)
                        .subscribe())
                .map(s -> new SuccessfulCreationDto(s.getId().toString()));
    }
}
