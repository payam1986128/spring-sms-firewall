package ir.payam1986128.smsfirewall.webflux.service;

import ir.payam1986128.smsfirewall.core.entity.User;
import ir.payam1986128.smsfirewall.core.presentation.common.SuccessfulCreationDto;
import ir.payam1986128.smsfirewall.core.presentation.users.LoginUserRequest;
import ir.payam1986128.smsfirewall.core.presentation.users.RegisterUserRequest;
import ir.payam1986128.smsfirewall.core.presentation.users.VerificationResponse;
import ir.payam1986128.smsfirewall.webflux.repository.UserRepository;
import ir.payam1986128.smsfirewall.core.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository repository;
    private final JwtTokenProvider tokenProvider;
    private final ReactiveAuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public Mono<SuccessfulCreationDto> registerUser(RegisterUserRequest request, String code) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(code));
        user.setActive(true);
        return repository.save(user).map(u -> new SuccessfulCreationDto(user.getId().toString()));
    }

    public Mono<VerificationResponse> loginUser(Mono<LoginUserRequest> username, String code) {
        return username
                .flatMap(login -> this.authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(login.getUsername(), code))
                        .map(this.tokenProvider::createToken)
                )
                .map(VerificationResponse::new);
    }
}
