package ir.payam1986128.smsfirewall.webflux.service;

import ir.payam1986128.smsfirewall.core.entity.KeywordFilter;
import ir.payam1986128.smsfirewall.core.entity.LimiterCondition;
import ir.payam1986128.smsfirewall.core.entity.Sms;
import ir.payam1986128.smsfirewall.core.entity.SuspiciousCategory;
import ir.payam1986128.smsfirewall.core.exception.ResourceNotFoundException;
import ir.payam1986128.smsfirewall.core.presentation.common.SuccessfulCreationDto;
import ir.payam1986128.smsfirewall.core.presentation.limiterconditions.*;
import ir.payam1986128.smsfirewall.core.presentation.sms.SmsFilterRequest;
import ir.payam1986128.smsfirewall.core.presentation.sms.SmsResponse;
import ir.payam1986128.smsfirewall.webflux.mapper.CommonMapper;
import ir.payam1986128.smsfirewall.webflux.mapper.LimiterConditionMapper;
import ir.payam1986128.smsfirewall.webflux.mapper.SmsMapper;
import ir.payam1986128.smsfirewall.webflux.repository.LimiterConditionRepository;
import ir.payam1986128.smsfirewall.webflux.repository.SmsRepository;
import ir.payam1986128.smsfirewall.webflux.repository.SuspiciousCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.couchbase.core.ReactiveFindByQueryOperation;
import org.springframework.data.couchbase.core.query.Query;
import org.springframework.data.couchbase.core.query.QueryCriteria;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.springframework.data.couchbase.core.query.N1QLExpression.x;

@AllArgsConstructor
@Service
public class LimiterConditionService {
    private final AtomicReference<List<LimiterCondition>> activeLimiterConditions = new AtomicReference<>();

    private final LimiterConditionRepository repository;
    private final LimiterConditionRepository limiterConditionRepository;
    private final SuspiciousCategoryRepository categoryRepository;
    private final SmsRepository smsRepository;
    private final CommonMapper commonMapper;
    private final LimiterConditionMapper mapper;
    private final SmsMapper smsMapper;
    private final PhoneNumberService phoneNumberService;

    public Flux<LimiterCondition> getActiveLimiterConditions() {
        List<LimiterCondition> cached = activeLimiterConditions.get();
        if (cached != null) {
            return Flux.fromIterable(cached);
        }
        return limiterConditionRepository.findAllByActiveIsTrueOrderByPriority()
                .collectList()
                .doOnNext(activeLimiterConditions::set)
                .flatMapMany(Flux::fromIterable);
    }

    public Mono<LimiterConditionsResponse> getLimiterConditions(LimiterConditionsFilterRequest request) {
        Query query = Query.query(QueryCriteria.where(x("name"))
                .like("%" + request.getFilter() + "%"));
        PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getPageSize());
        query.with(pageable);
        if (request.getSort() != null) {
            query.with(Sort.by(commonMapper.to(request.getSort())));
        }
        if (request.getState() != null) {
            query.addCriteria(QueryCriteria.where(x("active")).eq(request.getState()));
        }
        ReactiveFindByQueryOperation.TerminatingFindByQuery<LimiterCondition> findByQuery =
                repository.getOperations().findByQuery(LimiterCondition.class).matching(query);
        return findByQuery.all()
                .collectList()
                .map(mapper::to)
                .zipWith(findByQuery.count())
                .map(p -> new LimiterConditionsResponse(p.getT1(), p.getT2(), LocalDateTime.now()));
    }

    public Mono<LimiterConditionResponse> getLimiterCondition(UUID id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Limiter condition not found")))
                .map(mapper::to)
                .zipWith(smsRepository.countAllByAppliedFilterId(id))
                .map(p -> {
                    LimiterConditionResponse response = p.getT1();
                    response.setCaughtSms(p.getT2());
                    return response;
                });
    }

    private LimiterConditionRequest normalizePhoneNumbers(LimiterConditionRequest request) {
        if (request.getFilters().getSender() != null) {
            request.getFilters().getSender().normalizeSenders(phoneNumberService::normalize);
        }
        if (request.getFilters().getReceivers() != null) {
            request.getFilters().normalizeReceivers(phoneNumberService::normalize);
        }
        return request;
    }

    public Mono<SuccessfulCreationDto> addLimiterCondition(LimiterConditionRequest request) {
        return Mono.just(mapper.to(normalizePhoneNumbers(request)))
                .flatMap(this::updateLimiterConditionCategories)
                .flatMap(repository::save)
                .map(r -> new SuccessfulCreationDto(r.getId().toString()))
                .doOnNext(s -> activeLimiterConditions.set(null));
    }

    public Mono<Void> editContainingLimiterConditions(UUID categoryId) {
        Query query = Query.query(
                QueryCriteria.where(x("filters.message.categoryFilters")).arrayContaining(categoryId)
        );
        ReactiveFindByQueryOperation.TerminatingFindByQuery<LimiterCondition> findByQuery =
                repository.getOperations().findByQuery(LimiterCondition.class).matching(query);
        return findByQuery.all()
                .flatMap(this::updateLimiterConditionCategories)
                .flatMap(repository::save)
                .doOnNext(s -> activeLimiterConditions.set(null))
                .then();
    }

    private Mono<LimiterCondition> updateLimiterConditionCategories(LimiterCondition condition) {
        KeywordFilter keywordFilter = condition.getFilters().getKeyword();
        Mono<List<SuspiciousCategory>> categoriesMono = Flux.<SuspiciousCategory>empty().collectList();
        if (keywordFilter != null && keywordFilter.getCategories() != null) {
            categoriesMono = categoryRepository.findAllById(keywordFilter.getCategories()).collectList();
            if (keywordFilter.getCategoryKeywords() != null) {
                keywordFilter.getCategoryKeywords().clear();
            }
        }
        return categoriesMono
                .map(categories -> {
                    if (condition.getFilters().getKeyword() != null) {
                        Set<String> words = categories.stream().map(SuspiciousCategory::getWords).flatMap(Set::stream).collect(Collectors.toSet());
                        condition.getFilters().getKeyword().addCategoryKeywords(words);
                    }
                    return condition;
                });
    }

    public Mono<Void> deleteFromContainingLimiterConditions(UUID categoryId) {
        Query query = Query.query(
                QueryCriteria.where(x("filters.message.categoryFilters")).arrayContaining(categoryId)
        );
        ReactiveFindByQueryOperation.TerminatingFindByQuery<LimiterCondition> findByQuery =
                repository.getOperations().findByQuery(LimiterCondition.class).matching(query);
        return findByQuery.all()
                .map(c -> {
                    c.getFilters().getKeyword().getCategories().remove(categoryId);
                    return c;
                })
                .flatMap(this::updateLimiterConditionCategories)
                .flatMap(repository::save)
                .doOnNext(s -> activeLimiterConditions.set(null))
                .then();
    }

    public Mono<Void> editLimiterCondition(UUID id, LimiterConditionRequest request) {
        LimiterCondition identificationCondition = mapper.to(normalizePhoneNumbers(request));
        identificationCondition.setId(id);

        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("LimiterCondition not found")))
                .flatMap(c -> updateLimiterConditionCategories(identificationCondition))
                .flatMap(repository::save)
                .doOnNext(s -> activeLimiterConditions.set(null))
                .then();
    }

    public Mono<Void> reviewLimiterCondition(LimiterConditionStateRequest request) {
        return repository.findAllById(request.getIds())
                .map(c -> {
                    c.setActive(request.getState());
                    return c;
                })
                .flatMap(repository::save)
                .doOnNext(s -> activeLimiterConditions.set(null))
                .then();
    }

    public Mono<Void> deleteLimiterCondition(UUID id) {
        return repository.deleteById(id)
                .doOnSuccess(s -> activeLimiterConditions.set(null))
                .onErrorMap(e -> new ResourceNotFoundException("LimiterCondition not found"));
    }

    public Mono<SmsResponse> getCaughtSms(UUID id, SmsFilterRequest request) {
        Query query = getCaughtSmsQuery(id, request);
        PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getPageSize());
        query.with(pageable);
        if (request.getSort() != null) {
            query.with(Sort.by(commonMapper.to(request.getSort())));
        }
        ReactiveFindByQueryOperation.TerminatingFindByQuery<Sms> findByQuery =
                repository.getOperations().findByQuery(Sms.class).matching(query);
        return findByQuery.all()
                .collectList()
                .map(smsMapper::to)
                .zipWith(findByQuery.count())
                .map(p -> new SmsResponse(p.getT1(), p.getT2(), request.getDateTo()));
    }

    private Query getCaughtSmsQuery(UUID id, SmsFilterRequest request) {
        Query query = Query.query(QueryCriteria.where(x("appliedFilterId")).eq(id));
        if (request.getDateTo() != null) {
            query.addCriteria(QueryCriteria.where(x("receivedTime")).lte(request.getDateTo()));
        }
        if (request.getDateFrom() != null) {
            query.addCriteria(QueryCriteria.where(x("receivedTime")).gte(request.getDateFrom()));
        }
        if (request.getAction() != null) {
            query.addCriteria(QueryCriteria.where(x("action")).eq(request.getAction()));
        }
        if (request.getSender() != null) {
            query.addCriteria(QueryCriteria.where(x("sender")).eq(request.getSender()));
        }
        if (request.getReceiver() != null) {
            query.addCriteria(QueryCriteria.where(x("receiver")).eq(request.getReceiver()));
        }
        return query;
    }
}
