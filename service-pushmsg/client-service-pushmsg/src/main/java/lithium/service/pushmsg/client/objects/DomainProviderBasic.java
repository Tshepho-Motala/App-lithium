package lithium.service.pushmsg.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DomainProviderBasic {
	private String domainName;
	private String description;
	private Long providerId;
}