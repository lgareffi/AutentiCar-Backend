package app.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AiResponse {
    @JsonProperty("risk_score")
    private Double riskScore;

    @JsonProperty("risk_label")
    private String riskLabel;

    @JsonProperty("validadoIA")
    private Boolean validadoIA;

    public AiResponse() {}

    public Double getRiskScore() { return riskScore; }
    public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }

    public String getRiskLabel() { return riskLabel; }
    public void setRiskLabel(String riskLabel) { this.riskLabel = riskLabel; }

    public Boolean getValidadoIA() { return validadoIA; }
    public void setValidadoIA(Boolean validadoIA) { this.validadoIA = validadoIA; }
}
