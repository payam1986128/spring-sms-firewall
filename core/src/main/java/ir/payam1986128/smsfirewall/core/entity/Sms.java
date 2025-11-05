package ir.payam1986128.smsfirewall.core.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.annotation.Version;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;
import org.springframework.data.couchbase.repository.Collection;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Document
@Collection("sms")
@TypeAlias("sms")
public class Sms {
    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    private UUID id;

    @Field
    private String sender;

    @Field
    private String receiver;

    @Field
    private String message;

    @Field
    private LocalDateTime receivedTime;

    @Field
    private LocalDateTime evaluatedTime;

    @Field
    private LocalDateTime sendTime;

    @Field
    private UUID appliedFilterId;

    @Field
    private Action action;

    @Version
    private long version;
}
