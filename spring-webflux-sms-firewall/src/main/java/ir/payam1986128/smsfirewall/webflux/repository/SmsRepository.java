package ir.payam1986128.smsfirewall.webflux.repository;

import ir.payam1986128.smsfirewall.core.entity.Sms;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
@Collection("sms")
public interface SmsRepository extends ReactiveCouchbaseRepository<Sms, UUID> {
    @Query("select count(meta().id) from `sms_app`.`_default`.`sms` where sender = $1 and receivedTime between $2 and $3")
    Mono<Long> count(String sender, LocalDateTime start, LocalDateTime end);
    Mono<Long> countAllByAppliedFilterId(UUID ruleId);
}