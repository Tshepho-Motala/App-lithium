package lithium.service.limit.client.objects;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PlayerTimeSlotLimitRequest {

    private String fromTimestampWithZone;

    private String toTimestampWithZone;

}
