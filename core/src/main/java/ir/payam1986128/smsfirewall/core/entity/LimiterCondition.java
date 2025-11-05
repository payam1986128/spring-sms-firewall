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
import java.util.List;
import java.util.UUID;

@Data
@Document
@Collection("limiter-conditions")
@TypeAlias("condition")
public class LimiterCondition {
    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    private UUID id;

    @Field
    private String name;

    @Field
    private Integer priority;

    @Field
    private boolean active;

    @Field
    private LocalDateTime createdTime;

    @Field
    private List<TimeLimit> timeLimits;

    @Field
    private Filters filters;

    @Field
    private Action action;
}
