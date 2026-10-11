package com.tragefox.api.common.identifier.service;

import com.tragefox.api.common.entity.Base32IdSeq;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class Base32IdGenServiceImpl implements Base32IdGenService {

    private final MongoOperations mongoOperations;

    @Override
    public long getNextSequence(String key) {
        Query query = new Query(Criteria.where("_id").is(key));
        Update update = new Update().inc("sequence", 1);
        FindAndModifyOptions options =
                FindAndModifyOptions.options().returnNew(true).upsert(true);
        Base32IdSeq counter = mongoOperations.findAndModify(query, update, options, Base32IdSeq.class);
        return Objects.requireNonNull(counter).getSequence();
    }
}
