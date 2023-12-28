package dev.mvvasilev.common.data;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public abstract class AbstractEnumConverter<T extends Enum<T> & PersistableEnum<E>, E> implements AttributeConverter<T, E> {
    private final Class<T> clazz;

    public AbstractEnumConverter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public E convertToDatabaseColumn(T attribute) {
        return attribute != null ? attribute.value() : null;
    }

    @Override
    public T convertToEntityAttribute(E dbData) {
        if (dbData == null) {
            return null;
        }

        T[] enums = clazz.getEnumConstants();

        for (T e : enums) {
            if (e.value().equals(dbData)) {
                return e;
            }
        }

        throw new UnsupportedOperationException(String.format("Can't find value '%s' for enum '%s'", dbData, clazz.getCanonicalName()));
    }
}