package lithium.service.access.provider.sphonic.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RequestDetails {
	private String requestId;
	private String requestDateTime;
}
