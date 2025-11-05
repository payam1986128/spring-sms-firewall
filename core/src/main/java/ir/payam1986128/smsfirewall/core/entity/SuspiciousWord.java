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
import java.util.UUID;

@Data
@Document
@Collection("suspicious-words")
@TypeAlias("word")
public class SuspiciousWord {
    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    private UUID id;

    @Field
    private SuspiciousCategory category;

    @Field
    private String word;

    @Field
    private LocalDateTime dateTime;
}
