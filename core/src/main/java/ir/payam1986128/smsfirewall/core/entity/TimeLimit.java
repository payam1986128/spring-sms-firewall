package ir.payam1986128.smsfirewall.core.entity;

import lombok.Data;
import org.springframework.data.couchbase.core.mapping.Field;

import java.time.LocalDateTime;

@Data
public class TimeLimit {
    @Field
    private LocalDateTime from;

    @Field
    private LocalDateTime to;
}
