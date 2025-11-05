package ir.payam1986128.smsfirewall.webflux.service;

import ir.payam1986128.smsfirewall.core.entity.Action;
import ir.payam1986128.smsfirewall.core.entity.LimiterCondition;
import ir.payam1986128.smsfirewall.core.entity.Sms;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class DontSendActionService implements ActionService {
    @Override
    public Mono<Sms> act(LimiterCondition condition, Sms sms) {
        return Mono.fromCallable(() -> {
            sms.setEvaluatedTime(LocalDateTime.now());
            sms.setAction(Action.DONT_SEND);
            sms.setAppliedFilterId(condition.getId());
            return sms;
        });
    }

    @Override
    public Action getAction() {
        return Action.DONT_SEND;
    }
}
