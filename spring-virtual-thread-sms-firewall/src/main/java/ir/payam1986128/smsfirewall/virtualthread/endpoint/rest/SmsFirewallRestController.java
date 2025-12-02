package ir.payam1986128.smsfirewall.virtualthread.endpoint.rest;

import ir.payam1986128.smsfirewall.core.entity.Sms;
import ir.payam1986128.smsfirewall.core.presentation.common.SuccessfulCreationDto;
import ir.payam1986128.smsfirewall.virtualthread.service.FirewallService;
import ir.payam1986128.smsfirewall.virtualthread.service.TransceiverService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/protections")
@AllArgsConstructor
public class SmsFirewallRestController {
    private final TransceiverService transceiverService;
    private final FirewallService firewallService;

    @PostMapping
    public SuccessfulCreationDto receive(@RequestBody Sms sms) {
        sms = transceiverService.receive(sms);
        sms = firewallService.protect(sms);
        sms = transceiverService.receive(sms);
        return new SuccessfulCreationDto(sms.getId().toString());
    }
}
