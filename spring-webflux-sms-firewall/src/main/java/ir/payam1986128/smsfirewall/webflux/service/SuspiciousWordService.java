package ir.payam1986128.smsfirewall.webflux.service;

import ir.payam1986128.smsfirewall.core.entity.SuspiciousWord;
import ir.payam1986128.smsfirewall.core.exception.ResourceNotFoundException;
import ir.payam1986128.smsfirewall.core.presentation.suspiciouswords.SuspiciousWordsFilterRequest;
import ir.payam1986128.smsfirewall.core.presentation.suspiciouswords.SuspiciousWordsRequest;
import ir.payam1986128.smsfirewall.core.presentation.suspiciouswords.SuspiciousWordsResponse;
import ir.payam1986128.smsfirewall.webflux.mapper.CommonMapper;
import ir.payam1986128.smsfirewall.webflux.mapper.SuspiciousWordMapper;
import ir.payam1986128.smsfirewall.webflux.repository.SuspiciousWordRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.couchbase.core.ReactiveFindByQueryOperation;
import org.springframework.data.couchbase.core.query.Query;
import org.springframework.data.couchbase.core.query.QueryCriteria;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.couchbase.core.query.N1QLExpression.x;

@AllArgsConstructor
@Service
public class SuspiciousWordService {
    private final SuspiciousWordRepository repository;
    private final CommonMapper commonMapper;
    private final SuspiciousWordMapper mapper;

    public Mono<SuspiciousWordsResponse> getSuspiciousWords(SuspiciousWordsFilterRequest request) {
        Query query = Query.query(QueryCriteria.where(x("word"))
                .like("%" + request.getFilter() + "%"));
        PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getPageSize());
        query.with(pageable);
        if (request.getSort() != null) {
            query.with(Sort.by(commonMapper.to(request.getSort())));
        }
        ReactiveFindByQueryOperation.TerminatingFindByQuery<SuspiciousWord> findByQuery = repository.getOperations().findByQuery(SuspiciousWord.class).matching(query);
        return findByQuery.all()
                .collectList()
                .map(mapper::to)
                .zipWith(findByQuery.count())
                .map(p -> new SuspiciousWordsResponse(p.getT1(), p.getT2()));
    }

    public Mono<Void> addSuspiciousWords(SuspiciousWordsRequest request) {
        List<SuspiciousWord> words = request.getWords().stream().map(w -> {
            SuspiciousWord word = new SuspiciousWord();
            word.setWord(w);
            word.setDateTime(LocalDateTime.now());
            return word;
        }).toList();
        return repository.saveAll(words).then();
    }

    public Mono<Void> deleteSuspiciousWord(UUID id) {
        return repository.deleteById(id)
                .onErrorMap(e -> new ResourceNotFoundException("Suspicious word not found"));
    }
}
