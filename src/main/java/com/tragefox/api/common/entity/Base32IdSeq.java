package com.tragefox.api.common.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("sequence_counters")
public class Base32IdSeq {

    @Id
    private String id;

    private long sequence;
}
