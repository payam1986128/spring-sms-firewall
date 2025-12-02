package ir.payam1986128.smsfirewall.virtualthread.service;

import ir.payam1986128.smsfirewall.core.entity.User;
import ir.payam1986128.smsfirewall.core.exception.ResourceNotFoundException;
import ir.payam1986128.smsfirewall.core.mapper.UserMapper;
import ir.payam1986128.smsfirewall.core.presentation.common.SuccessfulCreationDto;
import ir.payam1986128.smsfirewall.core.presentation.users.LoginUserRequest;
import ir.payam1986128.smsfirewall.core.presentation.users.RegisterUserRequest;
import ir.payam1986128.smsfirewall.core.presentation.users.UserDetailsDto;
import ir.payam1986128.smsfirewall.core.presentation.users.VerificationResponse;
import ir.payam1986128.smsfirewall.virtualthread.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private UserDetailsDto getUserDetails(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toUserDetailsDto(user);
    }

    public SuccessfulCreationDto registerUser(RegisterUserRequest request, String code) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(code));
        user.setActive(true);
        User saved = userRepository.save(user);
        return new SuccessfulCreationDto(saved.getId().toString());
    }

    public VerificationResponse loginUser(LoginUserRequest request, String code) {
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), code));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsDto user = getUserDetails(request.getUsername());

        String accessToken = jwtTokenService.generateAccessToken(user);
        return new VerificationResponse(accessToken);
    }
}
