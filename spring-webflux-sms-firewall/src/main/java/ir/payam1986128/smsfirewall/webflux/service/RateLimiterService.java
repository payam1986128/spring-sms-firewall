package ir.payam1986128.smsfirewall.webflux.service;

import ir.payam1986128.smsfirewall.core.entity.LimiterCondition;
import ir.payam1986128.smsfirewall.core.entity.Sms;
import ir.payam1986128.smsfirewall.webflux.repository.SmsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RateLimiterService {

    private final SmsRepository smsRepository;

    public Mono<Long> countSms(Sms sms, LimiterCondition condition) {
        Integer intervalMinutes = condition.getFilters().getSender().getRate().getIntervalMinutes();
        LocalDateTime start = sms.getReceivedTime().minusMinutes(intervalMinutes);
        return smsRepository.count(sms.getSender(), start, sms.getReceivedTime());
    }
}
