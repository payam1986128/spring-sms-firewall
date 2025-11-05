package ir.payam1986128.smsfirewall.webflux.mapper;

import ir.payam1986128.smsfirewall.core.entity.Sms;
import ir.payam1986128.smsfirewall.core.presentation.sms.BriefSmsDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SmsMapper {
    List<BriefSmsDto> to(List<Sms> sms);
}
