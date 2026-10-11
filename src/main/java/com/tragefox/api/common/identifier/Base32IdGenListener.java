package com.tragefox.api.common.identifier;

import com.tragefox.api.common.identifier.annotation.Base32IdGen;
import com.tragefox.api.common.identifier.annotation.Identifier;
import com.tragefox.api.exception.ServerException;
import com.tragefox.api.exception.constant.ErrorType;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Base32IdGenListener extends AbstractMongoEventListener<Object> {

    private final MongoBase32IdGen mongoBase32IdGen;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        Object entity = event.getSource();
        if (!entity.getClass().isAnnotationPresent(Base32IdGen.class)) {
            return;
        }
        findIdField(entity.getClass()).ifPresent(field -> assignIdIfNull(field, entity));
    }

    private Optional<Field> findIdField(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Identifier.class))
                .findFirst();
    }

    private void assignIdIfNull(Field field, Object entity) {
        try {
            field.setAccessible(true);
            if (field.get(entity) == null) {
                field.set(entity, mongoBase32IdGen.generateId());
            }
        } catch (IllegalAccessException e) {
            throw new ServerException(ErrorType.INTERNAL_ERROR);
        }
    }
}
