package ir.payam1986128.smsfirewall.virtualthread.service;

import ir.payam1986128.smsfirewall.core.entity.SuspiciousCategory;
import ir.payam1986128.smsfirewall.core.exception.ResourceNotFoundException;
import ir.payam1986128.smsfirewall.core.mapper.CommonMapper;
import ir.payam1986128.smsfirewall.core.mapper.SuspiciousCategoryMapper;
import ir.payam1986128.smsfirewall.core.presentation.common.SuccessfulCreationDto;
import ir.payam1986128.smsfirewall.core.presentation.suspiciouscategories.SuspiciousCategoriesFilterRequest;
import ir.payam1986128.smsfirewall.core.presentation.suspiciouscategories.SuspiciousCategoriesResponse;
import ir.payam1986128.smsfirewall.core.presentation.suspiciouscategories.SuspiciousCategoryWordsRequest;
import ir.payam1986128.smsfirewall.virtualthread.repository.SuspiciousCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.couchbase.core.ExecutableFindByQueryOperation;
import org.springframework.data.couchbase.core.query.Query;
import org.springframework.data.couchbase.core.query.QueryCriteria;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.data.couchbase.core.query.N1QLExpression.x;

@AllArgsConstructor
@Service
public class SuspiciousCategoryService {
    private final SuspiciousCategoryRepository repository;
    private final CommonMapper commonMapper;
    private final SuspiciousCategoryMapper mapper;
    private final LimiterConditionService limiterConditionService;

    public SuspiciousCategoriesResponse getSuspiciousCategories(SuspiciousCategoriesFilterRequest request) {
        Query query = Query.query(QueryCriteria.where(x("name")).like("%" + request.getName() + "%"));
        PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getPageSize());
        query.with(pageable);
        if (request.getSort() != null) {
            query.with(Sort.by(commonMapper.to(request.getSort())));
        }
        if (request.getWord() != null && !request.getWord().isBlank()) {
            query.addCriteria(QueryCriteria.where(x("words")).arrayContaining(request.getWord()));
        }
        ExecutableFindByQueryOperation.TerminatingFindByQuery<SuspiciousCategory> findByQuery =
                repository.getOperations().findByQuery(SuspiciousCategory.class).matching(query);
        return new SuspiciousCategoriesResponse(mapper.to(findByQuery.all()), findByQuery.count());
    }

    public SuccessfulCreationDto addSuspiciousCategory(SuspiciousCategoryWordsRequest request) {
        SuspiciousCategory saved = repository.save(mapper.to(request));
        return new SuccessfulCreationDto(saved.getId().toString());
    }

    public void editSuspiciousCategory(UUID id, SuspiciousCategoryWordsRequest request) {
        Optional<SuspiciousCategory> suspiciousCategory = repository.findById(id);
        if (suspiciousCategory.isEmpty()) {
            throw new ResourceNotFoundException("Suspicious category not found");
        }
        repository.save(mapper.to(id, request));
        limiterConditionService.editContainingLimiterConditions(id);
    }

    public void deleteSuspiciousCategory(UUID id) {
        try {
            repository.deleteById(id);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Suspicious category not found");
        }
        limiterConditionService.deleteFromContainingLimiterConditions(id);
    }
}
