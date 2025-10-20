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
        // Solo primera letra en mayúscula, resto tal cual
        String lower = s.toLowerCase(ES);                // resto en minúscula
        return lower.substring(0,1).toUpperCase(ES)      // primera letra en mayúscula
                + lower.substring(1);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData; // no tocamos lo que viene de la BD
    }
}
