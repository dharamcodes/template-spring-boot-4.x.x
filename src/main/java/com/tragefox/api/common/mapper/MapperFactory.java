package com.tragefox.api.common.mapper;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MapperFactory<T> {

    private final Map<MapperType, BaseMapper<?, ?, ?>> mappers;

    @Autowired
    public MapperFactory(List<BaseMapper<?, ?, ?>> mapperList, Map<MapperType, BaseMapper<?, ?, ?>> mappers) {
        this.mappers = mappers;
        for (BaseMapper<?, ?, ?> mapper : mapperList) {
            mappers.put(mapper.getType(), mapper);
        }
    }

    @SuppressWarnings("unchecked")
    public T getMapper(MapperType mapperType) {
        return (T) mappers.get(mapperType);
    }
}
