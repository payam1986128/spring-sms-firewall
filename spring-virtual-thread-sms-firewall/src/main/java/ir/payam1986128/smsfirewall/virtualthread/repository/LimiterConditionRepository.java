package ir.payam1986128.smsfirewall.virtualthread.repository;

import ir.payam1986128.smsfirewall.core.entity.LimiterCondition;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Collection("limiter-conditions")
public interface LimiterConditionRepository extends CouchbaseRepository<LimiterCondition, UUID> {
    List<LimiterCondition> findAllByActiveIsTrueOrderByPriority();
}