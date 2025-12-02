package ir.payam1986128.smsfirewall.virtualthread.repository;

import ir.payam1986128.smsfirewall.core.entity.User;
import org.springframework.data.couchbase.repository.Collection;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Collection("users")
public interface UserRepository extends CouchbaseRepository<User, UUID> {
    Optional<User> findByUsername(String username);
}