package ir.payam1986128.smsfirewall.webflux.service;

import ir.payam1986128.smsfirewall.core.entity.*;
import ir.payam1986128.smsfirewall.webflux.repository.SmsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class FirewallService {
    private final SmsRepository smsRepository;
    private final RateLimiterService rateLimiterService;
    private final LimiterConditionService limiterConditionService;

    public Mono<Sms> protect(Sms sms) {
        return limiterConditionService.getActiveLimiterConditions()
                .filterWhen(condition -> evaluate(condition, sms))
                .collectList()
                .flatMap(conditions -> act(conditions.isEmpty() ? null : conditions.get(0), sms))
                .flatMap(smsRepository::save);
    }

    public Mono<Sms> act(LimiterCondition condition, Sms sms) {
        return Mono.fromCallable(() -> {
            sms.setEvaluatedTime(LocalDateTime.now());
            sms.setAction(Action.SEND);
            if (condition != null) {
                sms.setAction(condition.getAction());
                sms.setAppliedFilterId(condition.getId());
            }
            return sms;
        });
    }

    private Mono<Boolean> evaluate(LimiterCondition condition, Sms sms) {
        return Mono.just(condition)
                .filter(this::isConditionActive)
                .flatMap(c ->
                        Mono.zip(isContentBlocked(c, sms), isReceiverBlocked(c, sms), isSenderBlocked(c, sms))
                                .map(tuple -> tuple.getT1() || tuple.getT2() || tuple.getT3())
                );
    }

    private boolean isConditionActive(LimiterCondition condition) {
        List<TimeLimit> timeLimits = condition.getTimeLimits();
        return timeLimits == null || timeLimits.isEmpty() ||
                timeLimits.stream()
                        .anyMatch(timeLimit ->
                                (timeLimit.getFrom() == null || LocalDateTime.now().isAfter(timeLimit.getFrom())) &&
                                        LocalDateTime.now().isBefore(timeLimit.getTo())
                        );
    }

    private Mono<Boolean> isSenderBlocked(LimiterCondition condition, Sms sms) {
        SenderFilter filter = condition.getFilters().getSender();
        if (filter == null) {
            return Mono.just(false);
        }
        if (filter.getRate() == null && filter.getSenders() != null) {
            return Mono.just(filter.getSenders().stream().anyMatch(sender -> sender.equals(sms.getSender())));
        }
        return isRateLimited(condition, sms);
    }

    private Mono<Boolean> isReceiverBlocked(LimiterCondition condition, Sms sms) {
        Set<String> receivers = condition.getFilters().getReceivers();
        return Mono.just(receivers != null && receivers.stream().anyMatch(receiver -> receiver.equals(sms.getReceiver())));
    }

    private Mono<Boolean> isRateLimited(LimiterCondition condition, Sms sms) {
        return Mono.just(condition)
                .filter(c -> c.getFilters().getSender() == null ||
                        c.getFilters().getSender().getSenders() == null ||
                        c.getFilters().getSender().getSenders().contains(sms.getSender())
                ).flatMap(c -> rateLimiterService.countSms(sms, c))
                .map(count -> count >= condition.getFilters().getSender().getRate().getThreshold());
    }

    private Mono<Boolean> isContentBlocked(LimiterCondition condition, Sms sms) {
        KeywordFilter filter = condition.getFilters().getKeyword();
        if (filter != null) {
            return Mono.just(filter.getAllKeywords().stream().anyMatch(t -> sms.getMessage().contains(t)) ||
                    filter.getRegexes() != null &&
                            filter.getRegexes().stream().anyMatch(t -> sms.getMessage().matches(t)));
        }
        return Mono.just(false);
    }
}
