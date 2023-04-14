package lithium.service.games.provider.google.rge.client.objects.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PredictRequest {
    private List<PredictInstance> instances;
}
