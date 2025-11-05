package ir.payam1986128.smsfirewall.core.entity;

import lombok.Data;
import org.springframework.data.couchbase.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class KeywordFilter {

    @Field
    private Set<String> keywords;

    @Field
    private Set<UUID> categories;

    @Field
    private Set<String> categoryKeywords;

    @Field
    private Set<String> regexes;

    public Set<String> getAllKeywords() {
        Set<String> allKeywords = new HashSet<>();
        if (keywords != null) {
            allKeywords.addAll(keywords);
        }
        if (categoryKeywords != null) {
            allKeywords.addAll(categoryKeywords);
        }
        return allKeywords;
    }

    public void addCategoryKeywords(Set<String> anotherKeywords) {
        if (categoryKeywords == null) {
            categoryKeywords = new HashSet<>();
        }
        categoryKeywords.addAll(anotherKeywords);
    }
}
