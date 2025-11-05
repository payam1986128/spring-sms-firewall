package ir.payam1986128.smsfirewall.core.entity;

import lombok.Data;
import org.springframework.data.couchbase.core.mapping.Field;

import java.util.Set;

@Data
public class Filters {

    @Field
    private KeywordFilter keyword;

    @Field
    private SenderFilter sender;

    @Field
    private Set<String> receivers;
}
