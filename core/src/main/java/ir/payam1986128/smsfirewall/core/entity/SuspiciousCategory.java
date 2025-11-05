package ir.payam1986128.smsfirewall.core.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;
import org.springframework.data.couchbase.repository.Collection;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Document
@Collection("suspicious-categories")
@TypeAlias("category")
public class SuspiciousCategory {
    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    private UUID id;

    @Field
    private String name;

    @Field
    private LocalDateTime dateTime;

    @Field
    private Set<String> words;
}
