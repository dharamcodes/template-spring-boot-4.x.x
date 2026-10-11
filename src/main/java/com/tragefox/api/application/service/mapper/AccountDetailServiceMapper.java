package com.tragefox.api.application.service.mapper;

import com.tragefox.api.common.mapper.BaseMapper;
import com.tragefox.api.common.mapper.MapperType;
import com.tragefox.api.security.model.entity.AccountDetailEntity;
import com.tragefox.api.security.model.request.AccountDetailRequest;
import com.tragefox.api.security.model.response.AccountDetailResponse;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface AccountDetailServiceMapper
        extends BaseMapper<AccountDetailEntity, AccountDetailRequest, AccountDetailResponse> {

    @Override
    default MapperType getType() {
        return MapperType.ACCOUNT_DETAIL_MAPPER;
    }
}
