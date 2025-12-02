package ir.payam1986128.smsfirewall.virtualthread.repository;

import ir.payam1986128.smsfirewall.core.entity.Sms;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
@Collection("sms")
public interface SmsRepository extends CouchbaseRepository<Sms, UUID> {
    @Query("select count(meta().id) from `sms_app`.`_default`.`sms` where sender = $1 and receivedTime between $2 and $3")
    Long count(String sender, LocalDateTime start, LocalDateTime end);
    Long countAllByAppliedFilterId(UUID ruleId);
}