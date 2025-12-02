package ir.payam1986128.smsfirewall.virtualthread.repository;

import ir.payam1986128.smsfirewall.core.entity.SuspiciousWord;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Collection("suspicious-words")
public interface SuspiciousWordRepository extends CouchbaseRepository<SuspiciousWord, UUID> {
    Page<SuspiciousWord> findAllBy(Pageable pageable);
}