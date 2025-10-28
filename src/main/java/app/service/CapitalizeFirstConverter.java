package app.service;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Locale;

@Converter(autoApply = false)
public class CapitalizeFirstConverter implements AttributeConverter<String, String> {

    private static final Locale ES = new Locale("es", "AR");

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        String s = attribute.trim();
        if (s.isEmpty()) return s;
        String lower = s.toLowerCase(ES);
        return lower.substring(0,1).toUpperCase(ES)
                + lower.substring(1);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData;
    }
}
