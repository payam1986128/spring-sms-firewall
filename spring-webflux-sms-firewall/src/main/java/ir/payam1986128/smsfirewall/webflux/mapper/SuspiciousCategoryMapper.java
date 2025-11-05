package ir.payam1986128.smsfirewall.webflux.mapper;

import ir.payam1986128.smsfirewall.core.entity.SuspiciousCategory;
import ir.payam1986128.smsfirewall.core.presentation.suspiciouscategories.SuspiciousCategoryDto;
import ir.payam1986128.smsfirewall.core.presentation.suspiciouscategories.SuspiciousCategoryWordsRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface SuspiciousCategoryMapper {
    List<SuspiciousCategoryDto> to(List<SuspiciousCategory> categories);

    @Mapping(target = "dateTime", expression = "java(java.time.LocalDateTime.now())")
    SuspiciousCategory to(SuspiciousCategoryWordsRequest category);

    @Mapping(target = "dateTime", expression = "java(java.time.LocalDateTime.now())")
    SuspiciousCategory to(UUID id, SuspiciousCategoryWordsRequest category);
}
