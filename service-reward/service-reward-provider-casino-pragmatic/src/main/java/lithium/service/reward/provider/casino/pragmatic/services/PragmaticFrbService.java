package lithium.service.reward.provider.casino.pragmatic.services;

import lithium.service.reward.provider.casino.pragmatic.config.ProviderConfig;
import lithium.service.reward.provider.client.dto.CancelRewardRequest;
import lithium.service.reward.provider.client.dto.CancelRewardResponse;
import lithium.service.reward.provider.client.dto.ProcessRewardRequest;
import lithium.service.reward.provider.client.dto.ProcessRewardResponse;

public interface PragmaticFrbService {
     ProcessRewardResponse awardFreeSpins(ProcessRewardRequest awardRequest, ProviderConfig providerConfig);
     CancelRewardResponse cancelFreeSpins(CancelRewardRequest cancelRequest, ProviderConfig providerConfig);
}
