package ir.payam1986128.smsfirewall.webflux.service;

import ir.payam1986128.smsfirewall.core.entity.Action;
import ir.payam1986128.smsfirewall.core.entity.LimiterCondition;
import ir.payam1986128.smsfirewall.core.entity.Sms;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class SendWithoutDeliveredStateService implements ActionService {
    @Override
    public Mono<Sms> act(LimiterCondition condition, Sms sms) {
        return Mono.fromCallable(() -> {
            sms.setAppliedFilterId(condition.getId());
            sms.setEvaluatedTime(LocalDateTime.now());
            sms.setAction(Action.SEND_WITHOUT_DELIVERED_STATE);
            return sms;
        });
    }

    @Override
    public Action getAction() {
        return Action.SEND_WITHOUT_DELIVERED_STATE;
    }
}
