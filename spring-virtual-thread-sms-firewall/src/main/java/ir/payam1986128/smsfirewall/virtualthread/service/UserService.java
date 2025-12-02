package ir.payam1986128.smsfirewall.virtualthread.service;

import ir.payam1986128.smsfirewall.core.entity.User;
import ir.payam1986128.smsfirewall.core.mapper.UserMapper;
import ir.payam1986128.smsfirewall.virtualthread.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository repository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userMapper.toUserDetailsDto(user);
    }
}
