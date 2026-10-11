package com.tragefox.api.common.mapper;

import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BaseMapper<Entity, Request, Response> {

    @Mapping(target = "id", ignore = true)
    Entity requestToEntity(Request request);

    Response entityToResponse(Entity entity);

    Request entityToRequest(Entity entity);

    @Mapping(target = "id", ignore = true)
    void merge(Request request, @MappingTarget Entity entity);

    default MapperType getType() {
        return null;
    }
}
