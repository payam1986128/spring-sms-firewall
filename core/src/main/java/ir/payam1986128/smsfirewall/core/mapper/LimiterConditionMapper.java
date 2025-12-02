package ir.payam1986128.smsfirewall.core.mapper;

import ir.payam1986128.smsfirewall.core.entity.LimiterCondition;
import ir.payam1986128.smsfirewall.core.presentation.limiterconditions.BriefLimiterConditionDto;
import ir.payam1986128.smsfirewall.core.presentation.limiterconditions.LimiterConditionRequest;
import ir.payam1986128.smsfirewall.core.presentation.limiterconditions.LimiterConditionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LimiterConditionMapper {
    LimiterConditionResponse to(LimiterCondition condition);

    List<BriefLimiterConditionDto> to(List<LimiterCondition> conditions);

    @Mapping(target = "createdTime", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "active", expression = "java(true)")
    LimiterCondition to(LimiterConditionRequest request);
}
