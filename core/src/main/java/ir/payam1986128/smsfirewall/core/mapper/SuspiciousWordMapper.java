package ir.payam1986128.smsfirewall.core.mapper;

import ir.payam1986128.smsfirewall.core.entity.SuspiciousWord;
import ir.payam1986128.smsfirewall.core.presentation.suspiciouswords.SuspiciousWordDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SuspiciousWordMapper {
    List<SuspiciousWordDto> to(List<SuspiciousWord> words);
}
