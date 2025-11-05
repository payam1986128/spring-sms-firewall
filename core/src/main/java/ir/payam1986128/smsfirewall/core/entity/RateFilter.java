package ir.payam1986128.smsfirewall.core.entity;

import lombok.Data;
import org.springframework.data.couchbase.core.mapping.Field;

@Data
public class RateFilter {
    @Field
    private Integer intervalMinutes;

    @Field
    private Long threshold;
}
