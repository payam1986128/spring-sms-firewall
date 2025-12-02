package ir.payam1986128.smsfirewall.core.mapper;

import ir.payam1986128.smsfirewall.core.entity.User;
import ir.payam1986128.smsfirewall.core.presentation.users.UserDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "authorities", ignore = true)
    UserDetailsDto toUserDetailsDto(User user);
}
