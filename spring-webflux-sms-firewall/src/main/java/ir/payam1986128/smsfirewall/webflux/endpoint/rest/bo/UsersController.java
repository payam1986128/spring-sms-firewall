package ir.payam1986128.smsfirewall.webflux.endpoint.rest.bo;

import ir.payam1986128.smsfirewall.core.presentation.common.SuccessfulCreationDto;
import ir.payam1986128.smsfirewall.core.presentation.users.LoginUserRequest;
import ir.payam1986128.smsfirewall.core.presentation.users.RegisterUserRequest;
import ir.payam1986128.smsfirewall.core.presentation.users.VerificationResponse;
import ir.payam1986128.smsfirewall.webflux.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping("/api/bo/users")
public class UsersController {
    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SuccessfulCreationDto> registerUser(
            @Valid @RequestBody RegisterUserRequest request,
            @RequestHeader("code") String code
    ) {
        return service.registerUser(request, code);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public Mono<VerificationResponse> loginUser(
            @Valid @RequestBody Mono<LoginUserRequest> request,
            @RequestHeader("code") String code
    ) {
        return service.loginUser(request, code);
    }
}
