package lithium.service.limit.client.objects;

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
public class Restrictions {
	private PlayerExclusionV2 playerExclusionV2;
	private PlayerCoolOff playerCoolOff;
//	private Access access;
}
