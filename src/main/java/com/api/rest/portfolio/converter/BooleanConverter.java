package com.api.rest.portfolio.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BooleanConverter implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean value) {
        if (value) {
            return "Y";
        } else {
            return "N";
        }
    }

    @Override
    public Boolean convertToEntityAttribute(String value) {
        if (value.equals("Y")) {
            return true;
        } else {
            return false;
        }
    }

}
