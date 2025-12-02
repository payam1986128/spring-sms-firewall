package ir.payam1986128.smsfirewall.virtualthread.service;

import ir.payam1986128.smsfirewall.core.entity.KeywordFilter;
import ir.payam1986128.smsfirewall.core.entity.LimiterCondition;
import ir.payam1986128.smsfirewall.core.entity.Sms;
import ir.payam1986128.smsfirewall.core.entity.SuspiciousCategory;
import ir.payam1986128.smsfirewall.core.exception.ResourceNotFoundException;
import ir.payam1986128.smsfirewall.core.mapper.CommonMapper;
import ir.payam1986128.smsfirewall.core.mapper.LimiterConditionMapper;
import ir.payam1986128.smsfirewall.core.mapper.SmsMapper;
import ir.payam1986128.smsfirewall.core.presentation.common.SuccessfulCreationDto;
import ir.payam1986128.smsfirewall.core.presentation.limiterconditions.*;
import ir.payam1986128.smsfirewall.core.presentation.sms.SmsFilterRequest;
import ir.payam1986128.smsfirewall.core.presentation.sms.SmsResponse;
import ir.payam1986128.smsfirewall.core.service.PhoneNumberService;
import ir.payam1986128.smsfirewall.virtualthread.repository.LimiterConditionRepository;
import ir.payam1986128.smsfirewall.virtualthread.repository.SmsRepository;
import ir.payam1986128.smsfirewall.virtualthread.repository.SuspiciousCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.couchbase.core.ExecutableFindByQueryOperation;
import org.springframework.data.couchbase.core.query.Query;
import org.springframework.data.couchbase.core.query.QueryCriteria;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.data.couchbase.core.query.N1QLExpression.x;

@AllArgsConstructor
@Service
public class LimiterConditionService {
    private final AtomicReference<List<LimiterCondition>> activeLimiterConditions = new AtomicReference<>();

    private final LimiterConditionRepository repository;
    private final SuspiciousCategoryRepository categoryRepository;
    private final SmsRepository smsRepository;
    private final CommonMapper commonMapper;
    private final LimiterConditionMapper mapper;
    private final SmsMapper smsMapper;
    private final PhoneNumberService phoneNumberService;

    public List<LimiterCondition> getActiveLimiterConditions() {
        List<LimiterCondition> cached = activeLimiterConditions.get();
        if (cached != null) {
            return cached;
        }
        List<LimiterCondition> limiterConditions = repository.findAllByActiveIsTrueOrderByPriority();
        activeLimiterConditions.set(limiterConditions);
        return limiterConditions;
    }

    public LimiterConditionsResponse getLimiterConditions(LimiterConditionsFilterRequest request) {
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
        ExecutableFindByQueryOperation.TerminatingFindByQuery<LimiterCondition> findByQuery =
                repository.getOperations().findByQuery(LimiterCondition.class).matching(query);
        return new LimiterConditionsResponse(mapper.to(findByQuery.all()), findByQuery.count(), LocalDateTime.now());
    }

    public LimiterConditionResponse getLimiterCondition(UUID id) {
        Optional<LimiterCondition> limiterConditionOptional = repository.findById(id);
        if (limiterConditionOptional.isEmpty()) {
            throw new ResourceNotFoundException("LimiterCondition not found");
        }
        LimiterConditionResponse response = mapper.to(limiterConditionOptional.get());
        response.setCaughtSms(smsRepository.countAllByAppliedFilterId(id));
        return response;
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

    public SuccessfulCreationDto addLimiterCondition(LimiterConditionRequest request) {
        LimiterCondition limiterCondition = updateLimiterConditionCategories(mapper.to(normalizePhoneNumbers(request)));
        LimiterCondition saved = repository.save(limiterCondition);
        activeLimiterConditions.set(null);
        return new SuccessfulCreationDto(saved.getId().toString());
    }

    public void editContainingLimiterConditions(UUID categoryId) {
        Query query = Query.query(
                QueryCriteria.where(x("filters.keyword.categories")).arrayContaining(categoryId)
        );
        ExecutableFindByQueryOperation.TerminatingFindByQuery<LimiterCondition> findByQuery =
                repository.getOperations().findByQuery(LimiterCondition.class).matching(query);
        List<LimiterCondition> limiterConditions = findByQuery.all();
        limiterConditions.forEach(this::updateLimiterConditionCategories);
        repository.saveAll(limiterConditions);
        activeLimiterConditions.set(null);
    }

    private LimiterCondition updateLimiterConditionCategories(LimiterCondition condition) {
        KeywordFilter keywordFilter = condition.getFilters().getKeyword();
        List<SuspiciousCategory> categories = new ArrayList<>();
        if (keywordFilter != null && keywordFilter.getCategories() != null) {
            categories = categoryRepository.findAllById(keywordFilter.getCategories());
            if (keywordFilter.getCategoryKeywords() != null) {
                keywordFilter.getCategoryKeywords().clear();
            }
        }
        categories.forEach(category -> {
                    if (condition.getFilters().getKeyword() != null) {
                        condition.getFilters().getKeyword().addCategoryKeywords(new HashSet<>(category.getWords()));
                    }
                });
        return condition;
    }

    public void deleteFromContainingLimiterConditions(UUID categoryId) {
        Query query = Query.query(
                QueryCriteria.where(x("filters.keyword.categories")).arrayContaining(categoryId)
        );
        ExecutableFindByQueryOperation.TerminatingFindByQuery<LimiterCondition> findByQuery =
                repository.getOperations().findByQuery(LimiterCondition.class).matching(query);

        findByQuery.all().stream()
                .peek(c -> c.getFilters().getKeyword().getCategories().remove(categoryId))
                .map(this::updateLimiterConditionCategories)
                .forEach(repository::save);

        activeLimiterConditions.set(null);
    }

    public void editLimiterCondition(UUID id, LimiterConditionRequest request) {
        LimiterCondition limiterCondition = mapper.to(normalizePhoneNumbers(request));
        limiterCondition.setId(id);

        repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Limiter condition not found"));
        updateLimiterConditionCategories(limiterCondition);
        repository.save(limiterCondition);
        activeLimiterConditions.set(null);
    }

    public void reviewLimiterCondition(LimiterConditionStateRequest request) {
        repository.findAllById(request.getIds()).stream()
                .peek(c -> c.setActive(request.getState()))
                .forEach(repository::save);
        activeLimiterConditions.set(null);
    }

    public void deleteLimiterCondition(UUID id) {
        try {
            repository.deleteById(id);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Limiter condition not found");
        }
        activeLimiterConditions.set(null);
    }

    public SmsResponse getCaughtSms(UUID id, SmsFilterRequest request) {
        Query query = getCaughtSmsQuery(id, request);
        PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getPageSize());
        query.with(pageable);
        if (request.getSort() != null) {
            query.with(Sort.by(commonMapper.to(request.getSort())));
        }
        ExecutableFindByQueryOperation.TerminatingFindByQuery<Sms> findByQuery = repository.getOperations().findByQuery(Sms.class).matching(query);

        return new SmsResponse(smsMapper.to(findByQuery.all()), findByQuery.count(), request.getDateTo());
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
