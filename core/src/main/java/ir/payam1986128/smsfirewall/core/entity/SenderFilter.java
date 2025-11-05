package ir.payam1986128.smsfirewall.core.entity;

import lombok.Data;
import org.springframework.data.couchbase.core.mapping.Field;

import java.util.Set;

@Data
public class SenderFilter {

    @Field
    private Set<String> senders;

    @Field
    private RateFilter rate;
}
