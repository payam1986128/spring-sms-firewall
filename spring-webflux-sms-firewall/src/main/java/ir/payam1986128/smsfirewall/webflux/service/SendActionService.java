package ir.payam1986128.smsfirewall.webflux.service;

import ir.payam1986128.smsfirewall.core.entity.Action;
import ir.payam1986128.smsfirewall.core.entity.Sms;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class SendActionService {
    public Mono<Sms> send(Sms sms) {
        return Mono.fromCallable(() -> {
            sms.setEvaluatedTime(LocalDateTime.now());
            sms.setAction(Action.SEND);
            return sms;
        });
    }
}
