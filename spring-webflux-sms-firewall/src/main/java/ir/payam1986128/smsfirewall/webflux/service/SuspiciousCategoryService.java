package ir.payam1986128.smsfirewall.webflux.service;

import ir.payam1986128.smsfirewall.core.entity.SuspiciousCategory;
import ir.payam1986128.smsfirewall.core.exception.ResourceNotFoundException;
import ir.payam1986128.smsfirewall.core.presentation.common.SuccessfulCreationDto;
import ir.payam1986128.smsfirewall.core.presentation.suspiciouscategories.SuspiciousCategoriesFilterRequest;
import ir.payam1986128.smsfirewall.core.presentation.suspiciouscategories.SuspiciousCategoriesResponse;
import ir.payam1986128.smsfirewall.core.presentation.suspiciouscategories.SuspiciousCategoryWordsRequest;
import ir.payam1986128.smsfirewall.core.mapper.CommonMapper;
import ir.payam1986128.smsfirewall.core.mapper.SuspiciousCategoryMapper;
import ir.payam1986128.smsfirewall.webflux.repository.SuspiciousCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.couchbase.core.ReactiveFindByQueryOperation;
import org.springframework.data.couchbase.core.query.Query;
import org.springframework.data.couchbase.core.query.QueryCriteria;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.data.couchbase.core.query.N1QLExpression.x;

@AllArgsConstructor
@Service
public class SuspiciousCategoryService {
    private final SuspiciousCategoryRepository repository;
    private final CommonMapper commonMapper;
    private final SuspiciousCategoryMapper mapper;
    private final LimiterConditionService limiterConditionService;

    public Mono<SuspiciousCategoriesResponse> getSuspiciousCategories(SuspiciousCategoriesFilterRequest request) {
        Query query = Query.query(QueryCriteria.where(x("name")).like("%" + request.getName() + "%"));
        PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getPageSize());
        query.with(pageable);
        if (request.getSort() != null) {
            query.with(Sort.by(commonMapper.to(request.getSort())));
        }
        if (request.getWord() != null && !request.getWord().isBlank()) {
            query.addCriteria(QueryCriteria.where(x("words")).arrayContaining(request.getWord()));
        }
        ReactiveFindByQueryOperation.TerminatingFindByQuery<SuspiciousCategory> findByQuery = repository.getOperations().findByQuery(SuspiciousCategory.class).matching(query);
        return findByQuery.all()
                .collectList()
                .map(mapper::to)
                .zipWith(findByQuery.count())
                .map(p -> new SuspiciousCategoriesResponse(p.getT1(), p.getT2()));
    }

    public Mono<SuccessfulCreationDto> addSuspiciousCategory(SuspiciousCategoryWordsRequest request) {
        return repository.save(mapper.to(request)).map(c -> new SuccessfulCreationDto(c.getId().toString()));
    }

    public Mono<Void> editSuspiciousCategory(UUID id, SuspiciousCategoryWordsRequest request) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Suspicious category not found")))
                .flatMap(c -> repository.save(mapper.to(id, request)))
                .then(limiterConditionService.editContainingLimiterConditions(id));
    }

    public Mono<Void> deleteSuspiciousCategory(UUID id) {
        return repository.deleteById(id)
                .onErrorMap(e -> new ResourceNotFoundException("Suspicious category not found"))
                .then(limiterConditionService.deleteFromContainingLimiterConditions(id));
    }
}
