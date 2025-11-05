package ir.payam1986128.smsfirewall.webflux.service;

import ir.payam1986128.smsfirewall.core.entity.LimiterCondition;
import ir.payam1986128.smsfirewall.core.entity.Sms;
import ir.payam1986128.smsfirewall.core.entity.Action;
import reactor.core.publisher.Mono;

public interface ActionService {
    Mono<Sms> act(LimiterCondition condition, Sms sms);
    Action getAction();
}
