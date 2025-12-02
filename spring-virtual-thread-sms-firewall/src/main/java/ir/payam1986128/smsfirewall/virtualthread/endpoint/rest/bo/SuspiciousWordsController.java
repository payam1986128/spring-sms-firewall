package ir.payam1986128.smsfirewall.virtualthread.endpoint.rest.bo;

import ir.payam1986128.smsfirewall.core.presentation.suspiciouswords.SuspiciousWordsFilterRequest;
import ir.payam1986128.smsfirewall.core.presentation.suspiciouswords.SuspiciousWordsRequest;
import ir.payam1986128.smsfirewall.core.presentation.suspiciouswords.SuspiciousWordsResponse;
import ir.payam1986128.smsfirewall.virtualthread.service.SuspiciousWordService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/bo/suspicious-words")
@Validated
public class SuspiciousWordsController {

    private final SuspiciousWordService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public SuspiciousWordsResponse getSuspiciousWords(SuspiciousWordsFilterRequest request) {
        return service.getSuspiciousWords(request);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void addSuspiciousWords(@Valid @RequestBody SuspiciousWordsRequest request) {
        service.addSuspiciousWords(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSuspiciousWord(@PathVariable("id") UUID id) {
        service.deleteSuspiciousWord(id);
    }
}
