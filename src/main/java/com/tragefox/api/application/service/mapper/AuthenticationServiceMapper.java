package com.tragefox.api.application.service.mapper;

import com.tragefox.api.common.mapper.BaseMapper;
import com.tragefox.api.common.mapper.MapperType;
import com.tragefox.api.security.model.entity.AuthenticationEntity;
import com.tragefox.api.security.model.request.AuthenticationRequest;
import com.tragefox.api.security.model.response.AuthenticationResponse;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface AuthenticationServiceMapper
        extends BaseMapper<AuthenticationEntity, AuthenticationRequest, AuthenticationResponse> {

    @Override
    default MapperType getType() {
        return MapperType.AUTHENTICATION_MAPPER;
    }
}
