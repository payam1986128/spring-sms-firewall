package ir.payam1986128.smsfirewall.webflux.endpoint.rest.bo;

import ir.payam1986128.smsfirewall.core.presentation.common.SuccessfulCreationDto;
import ir.payam1986128.smsfirewall.core.presentation.suspiciouscategories.SuspiciousCategoriesFilterRequest;
import ir.payam1986128.smsfirewall.core.presentation.suspiciouscategories.SuspiciousCategoriesResponse;
import ir.payam1986128.smsfirewall.core.presentation.suspiciouscategories.SuspiciousCategoryWordsRequest;
import ir.payam1986128.smsfirewall.webflux.service.SuspiciousCategoryService;
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
@RequestMapping("/api/bo/suspicious-categories")
@Validated
public class SuspiciousCategoriesController {
    private final SuspiciousCategoryService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<SuspiciousCategoriesResponse> getSuspiciousCategories(SuspiciousCategoriesFilterRequest request) {
        return service.getSuspiciousCategories(request);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SuccessfulCreationDto> addSuspiciousCategory(@Valid @RequestBody SuspiciousCategoryWordsRequest request) {
        return service.addSuspiciousCategory(request);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> editSuspiciousCategory(
            @PathVariable("id") UUID id,
            @Valid @RequestBody SuspiciousCategoryWordsRequest request) {
        return service.editSuspiciousCategory(id, request);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteSuspiciousCategory(@PathVariable("id") UUID id) {
        return service.deleteSuspiciousCategory(id);
    }
}