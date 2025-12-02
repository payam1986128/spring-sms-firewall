package ir.payam1986128.smsfirewall.virtualthread.endpoint.rest.bo;

import ir.payam1986128.smsfirewall.core.presentation.common.SuccessfulCreationDto;
import ir.payam1986128.smsfirewall.core.presentation.users.LoginUserRequest;
import ir.payam1986128.smsfirewall.core.presentation.users.RegisterUserRequest;
import ir.payam1986128.smsfirewall.core.presentation.users.VerificationResponse;
import ir.payam1986128.smsfirewall.virtualthread.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/bo/users")
public class UsersController {
    private final AuthService authService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessfulCreationDto registerUser(
            @Valid @RequestBody RegisterUserRequest request,
            @RequestHeader("code") String code
    ) {
        return authService.registerUser(request, code);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public VerificationResponse loginUser(
            @Valid @RequestBody LoginUserRequest request,
            @RequestHeader("code") String code
    ) {
        return authService.loginUser(request, code);
    }
}
