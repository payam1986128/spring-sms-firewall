package ir.payam1986128.smsfirewall.virtualthread.service;

import ir.payam1986128.smsfirewall.core.entity.SuspiciousWord;
import ir.payam1986128.smsfirewall.core.exception.ResourceNotFoundException;
import ir.payam1986128.smsfirewall.core.mapper.CommonMapper;
import ir.payam1986128.smsfirewall.core.mapper.SuspiciousWordMapper;
import ir.payam1986128.smsfirewall.core.presentation.suspiciouswords.SuspiciousWordsFilterRequest;
import ir.payam1986128.smsfirewall.core.presentation.suspiciouswords.SuspiciousWordsRequest;
import ir.payam1986128.smsfirewall.core.presentation.suspiciouswords.SuspiciousWordsResponse;
import ir.payam1986128.smsfirewall.virtualthread.repository.SuspiciousWordRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.couchbase.core.ExecutableFindByQueryOperation;
import org.springframework.data.couchbase.core.query.Query;
import org.springframework.data.couchbase.core.query.QueryCriteria;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

    public SuspiciousWordsResponse getSuspiciousWords(SuspiciousWordsFilterRequest request) {
        Query query = Query.query(QueryCriteria.where(x("word"))
                .like("%" + request.getFilter() + "%"));
        PageRequest pageable = PageRequest.of(request.getPage() - 1, request.getPageSize());
        query.with(pageable);
        if (request.getSort() != null) {
            query.with(Sort.by(commonMapper.to(request.getSort())));
        }
        ExecutableFindByQueryOperation.TerminatingFindByQuery<SuspiciousWord> findByQuery =
                repository.getOperations().findByQuery(SuspiciousWord.class).matching(query);
        return new SuspiciousWordsResponse(mapper.to(findByQuery.all()), findByQuery.count());
    }

    public void addSuspiciousWords(SuspiciousWordsRequest request) {
        List<SuspiciousWord> words = request.getWords().stream().map(w -> {
            SuspiciousWord word = new SuspiciousWord();
            word.setWord(w);
            word.setDateTime(LocalDateTime.now());
            return word;
        }).toList();
        repository.saveAll(words);
    }

    public void deleteSuspiciousWord(UUID id) {
        try {
            repository.deleteById(id);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Suspicious word not found");
        }
    }
}
