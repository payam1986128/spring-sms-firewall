package ir.payam1986128.smsfirewall.webflux.service;

import ir.payam1986128.smsfirewall.core.entity.Sms;
import ir.payam1986128.smsfirewall.core.presentation.sms.SmsFilterRequest;
import ir.payam1986128.smsfirewall.core.presentation.sms.SmsResponse;
import ir.payam1986128.smsfirewall.webflux.mapper.CommonMapper;
import ir.payam1986128.smsfirewall.webflux.mapper.SmsMapper;
import ir.payam1986128.smsfirewall.webflux.repository.SmsRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.couchbase.core.ReactiveFindByQueryOperation;
import org.springframework.data.couchbase.core.query.Query;
import org.springframework.data.couchbase.core.query.QueryCriteria;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static org.springframework.data.couchbase.core.query.N1QLExpression.x;

@AllArgsConstructor
@Service
public class SmsService {
    private final SmsRepository repository;
    private final CommonMapper commonMapper;
    private final SmsMapper smsMapper;

    public Mono<SmsResponse> getSms(SmsFilterRequest request) {
        Query query = Query.query(QueryCriteria.where(x("receivedTime")).lte(request.getDateTo()));
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
}
