// app/service/AiMlFullResponse.java
package app.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AiMlFullResponse {

    @JsonProperty("risk_score")
    private Double riskScore;

    @JsonProperty("risk_label")
    private String riskLabel;

    @JsonProperty("features_used")
    private List<String> featuresUsed;

    @JsonProperty("debug")
    private Debug debug;

    @JsonProperty("reasons")
    private List<Reason> reasons;

    @JsonProperty("validadoIA")
    private Boolean validadoIA;

    // getters/setters …

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Reason {
        public String code;
        @JsonProperty("msg") public String message;
        @JsonProperty("w")   public Double weight;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Debug {
        @JsonProperty("ocr_stats")       public OcrStats ocrStats;
        @JsonProperty("metadata_summary") public Map<String, Object> metadataSummary;

        @JsonProperty("text_summary")    public TextSummary textSummary;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class OcrStats {
            public Integer pages;
            @JsonProperty("total_chars") public Integer totalChars;
            @JsonProperty("time_ms")     public Integer timeMs;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class TextSummary {
            @JsonProperty("has_date")             public Boolean hasDate;
            @JsonProperty("length")               public Integer length;
            @JsonProperty("has_patente")          public Boolean hasPatente;
            @JsonProperty("has_vin")              public Boolean hasVin;
            @JsonProperty("has_cuit")             public Boolean hasCuit;
            @JsonProperty("has_vencimiento")      public Boolean hasVencimiento;
            @JsonProperty("has_entidad_emisora")  public Boolean hasEntidadEmisora;
            // Si no querés exponer el texto completo, podés omitir raw_text
            // @JsonProperty("raw_text")          public String rawText;
        }
    }
}
