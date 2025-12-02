package ir.payam1986128.smsfirewall.virtualthread.service;

import ir.payam1986128.smsfirewall.core.entity.*;
import ir.payam1986128.smsfirewall.virtualthread.repository.SmsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class FirewallService {
    private final SmsRepository smsRepository;
    private final RateLimiterService rateLimiterService;
    private final LimiterConditionService limiterConditionService;

    public Sms protect(Sms sms) {
        List<LimiterCondition> activeLimiterConditions = limiterConditionService.getActiveLimiterConditions();
        for (LimiterCondition limiterCondition : activeLimiterConditions) {
            if (evaluate(limiterCondition, sms)) {
                act(limiterCondition, sms);
                return smsRepository.save(sms);
            }
        }
        act(null, sms);
        return smsRepository.save(sms);
    }

    public void act(LimiterCondition condition, Sms sms) {
        sms.setEvaluatedTime(LocalDateTime.now());
        sms.setAction(Action.SEND);
        if (condition != null) {
            sms.setAction(condition.getAction());
            sms.setAppliedFilterId(condition.getId());
        }
    }

    private Boolean evaluate(LimiterCondition condition, Sms sms) {
        if (isConditionActive(condition)) {
            return isContentBlocked(condition, sms) || isReceiverBlocked(condition, sms) || isSenderBlocked(condition, sms);
        }
        return false;
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

    private Boolean isSenderBlocked(LimiterCondition condition, Sms sms) {
        SenderFilter filter = condition.getFilters().getSender();
        if (filter == null) {
            return false;
        }
        if (filter.getRate() == null && filter.getSenders() != null) {
            return filter.getSenders().stream().anyMatch(sender -> sender.equals(sms.getSender()));
        }
        return isRateLimited(condition, sms);
    }

    private Boolean isReceiverBlocked(LimiterCondition condition, Sms sms) {
        Set<String> receivers = condition.getFilters().getReceivers();
        return receivers != null && receivers.stream().anyMatch(receiver -> receiver.equals(sms.getReceiver()));
    }

    private Boolean isRateLimited(LimiterCondition condition, Sms sms) {
        if (condition.getFilters().getSender() == null ||
                condition.getFilters().getSender().getSenders() == null ||
                condition.getFilters().getSender().getSenders().contains(sms.getSender())) {
            Long count = rateLimiterService.countSms(sms, condition);
            return count >= condition.getFilters().getSender().getRate().getThreshold();
        }
        return false;
    }

    private Boolean isContentBlocked(LimiterCondition condition, Sms sms) {
        KeywordFilter filter = condition.getFilters().getKeyword();
        if (filter != null) {
            return filter.getAllKeywords().stream().anyMatch(t -> sms.getMessage().contains(t)) ||
                    filter.getRegexes() != null &&
                            filter.getRegexes().stream().anyMatch(t -> sms.getMessage().matches(t));
        }
        return false;
    }
}
