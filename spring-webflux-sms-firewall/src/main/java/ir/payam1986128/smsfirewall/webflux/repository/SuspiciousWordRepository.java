package ir.payam1986128.smsfirewall.webflux.repository;

import ir.payam1986128.smsfirewall.core.entity.SuspiciousWord;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
@Collection("suspicious-words")
public interface SuspiciousWordRepository extends ReactiveCouchbaseRepository<SuspiciousWord, UUID> {
    Flux<SuspiciousWord> findAllBy(Pageable pageable);
}