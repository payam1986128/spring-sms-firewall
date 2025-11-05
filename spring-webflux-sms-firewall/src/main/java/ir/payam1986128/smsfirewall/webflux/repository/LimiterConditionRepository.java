package ir.payam1986128.smsfirewall.webflux.repository;

import ir.payam1986128.smsfirewall.core.entity.LimiterCondition;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
@Collection("limiter-conditions")
public interface LimiterConditionRepository extends ReactiveCouchbaseRepository<LimiterCondition, UUID> {
    Flux<LimiterCondition> findAllByActiveIsTrueOrderByPriority();
}