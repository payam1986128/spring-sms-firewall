package ir.payam1986128.smsfirewall.virtualthread.endpoint.rest.bo;

import ir.payam1986128.smsfirewall.core.presentation.common.SuccessfulCreationDto;
import ir.payam1986128.smsfirewall.core.presentation.limiterconditions.*;
import ir.payam1986128.smsfirewall.core.presentation.sms.SmsFilterRequest;
import ir.payam1986128.smsfirewall.core.presentation.sms.SmsResponse;
import ir.payam1986128.smsfirewall.virtualthread.service.LimiterConditionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/bo/limiter-conditions")
@Validated
public class LimiterConditionsController {
    private final LimiterConditionService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public LimiterConditionsResponse getLimiterConditions(LimiterConditionsFilterRequest request) {
        return service.getLimiterConditions(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LimiterConditionResponse getLimiterCondition(@PathVariable("id") UUID id) {
        return service.getLimiterCondition(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessfulCreationDto addLimiterCondition(@Valid @RequestBody LimiterConditionRequest request) {
        return service.addLimiterCondition(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void editLimiterCondition(@PathVariable("id") UUID id, @Valid @RequestBody LimiterConditionRequest request) {
        service.editLimiterCondition(id, request);
    }

    @PutMapping("/state")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reviewLimiterCondition(@Valid @RequestBody LimiterConditionStateRequest request) {
        service.reviewLimiterCondition(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLimiterCondition(@PathVariable("id") UUID id) {
        service.deleteLimiterCondition(id);
    }

    @GetMapping(value = "/{id}/sms", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public SmsResponse getCaughtSms(@PathVariable("id") UUID id, SmsFilterRequest request) {
        return service.getCaughtSms(id, request);
    }
}
