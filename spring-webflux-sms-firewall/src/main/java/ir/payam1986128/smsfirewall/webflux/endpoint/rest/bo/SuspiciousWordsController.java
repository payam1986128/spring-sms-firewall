package ir.payam1986128.smsfirewall.webflux.endpoint.rest.bo;

import ir.payam1986128.smsfirewall.core.presentation.suspiciouswords.SuspiciousWordsFilterRequest;
import ir.payam1986128.smsfirewall.core.presentation.suspiciouswords.SuspiciousWordsRequest;
import ir.payam1986128.smsfirewall.core.presentation.suspiciouswords.SuspiciousWordsResponse;
import ir.payam1986128.smsfirewall.webflux.service.SuspiciousWordService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/bo/suspicious-words")
@Validated
public class SuspiciousWordsController {

    private final SuspiciousWordService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<SuspiciousWordsResponse> getSuspiciousWords(SuspiciousWordsFilterRequest request) {
        return service.getSuspiciousWords(request);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> addSuspiciousWords(@Valid @RequestBody SuspiciousWordsRequest request) {
        return service.addSuspiciousWords(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteSuspiciousWord(@PathVariable("id") UUID id) {
        return service.deleteSuspiciousWord(id);
    }
}
