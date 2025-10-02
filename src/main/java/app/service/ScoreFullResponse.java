
package app.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScoreFullResponse {
    @JsonProperty("risk_score01")  public Double riskScore01;
    @JsonProperty("risk_score100") public Integer riskScore100;
    @JsonProperty("risk_label")    public String riskLabel;
    @JsonProperty("reasons")       public List<Reason> reasons;
    @JsonProperty("per_page")      public List<PageScore> perPage;
    @JsonProperty("model_version") public String modelVersion;
    @JsonProperty("pipeline_version") public String pipelineVersion;
    @JsonProperty("config_version") public String configVersion;
    @JsonProperty("processing_time_ms") public Integer processingTimeMs;
    @JsonProperty("validadoIA")    public Boolean validadoIA;
    @JsonProperty("debug")         public Map<String,Object> debug;
    @JsonProperty("warnings")      public List<String> warnings;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Reason {
        public String code;
        public String message;
        public Double weight; // puede venir null
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PageScore {
        public Integer page;
        public Double score;
        public List<Reason> reasons;
    }
}
