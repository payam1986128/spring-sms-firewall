package ir.payam1986128.smsfirewall.core.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CommonMapper {

    default Sort.Order to(String sort) {
        String[] split = sort.split(",");
        if (split.length == 2) {
            String dir = split[1].toLowerCase();
            if ("desc".equals(dir)) {
                return Sort.Order.desc(split[0]);
            }
        }
        return Sort.Order.asc(split[0]);
    }

    default List<Sort.Order> to(String[] sorts) {
        return Arrays.stream(sorts).map(this::to).toList();
    }
}
