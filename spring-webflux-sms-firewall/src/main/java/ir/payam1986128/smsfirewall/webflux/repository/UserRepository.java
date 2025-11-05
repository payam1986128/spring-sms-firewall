package ir.payam1986128.smsfirewall.webflux.repository;

import ir.payam1986128.smsfirewall.core.entity.User;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
@Collection("users")
public interface UserRepository extends ReactiveCouchbaseRepository<User, UUID> {
    Mono<User> findByUsername(String username);
}