package app.blockchain;

import lombok.Data;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class BlockchainService {
    private final WebClient wc;
    public BlockchainService(WebClient blockchainWebClient){ this.wc = blockchainWebClient; }

    public boolean exists(String vehicleId, String eventHash) {
        Boolean result =
            wc.get()
                .uri(uri -> uri.path("/exists")
                    .queryParam("vehicleId", vehicleId)
                    .queryParam("eventHash", eventHash)
                    .build())
                .retrieve()
                .bodyToMono(ExistsResponse.class)
                .map(ExistsResponse::isExists)
                .onErrorReturn(false)
                .block();

        return Boolean.TRUE.equals(result);
    }

    public RecordResponse record(String vehicleId, String eventHash){
        return wc.post().uri("/record")
                .bodyValue(Map.of("vehicleId", vehicleId, "eventHash", eventHash))
                .retrieve()
                .bodyToMono(RecordResponse.class)
                .block();
    }

    public List<Map<String,Object>> eventsByVehicle(String vehicleId){
        return wc.get().uri("/events/{vehicleId}", vehicleId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String,Object>>>(){})
                .block();
    }

    @Data public static class ExistsResponse { private boolean exists; }
    @Data public static class RecordResponse { private boolean ok; private Object payload; }
}