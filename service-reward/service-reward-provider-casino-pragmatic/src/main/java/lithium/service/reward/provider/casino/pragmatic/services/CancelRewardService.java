package lithium.service.reward.provider.casino.pragmatic.services;

import lithium.modules.ModuleInfo;
import lithium.service.reward.provider.casino.pragmatic.config.ProviderConfig;
import lithium.service.reward.provider.casino.pragmatic.config.ProviderConfigService;
import lithium.service.reward.provider.client.dto.CancelRewardRequest;
import lithium.service.reward.provider.client.dto.CancelRewardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CancelRewardService {

  private final PragmaticFrbService pragmaticFrbService;
  private final ProviderConfigService providerConfigService;
  private final ModuleInfo moduleInfo;

  public CancelRewardResponse cancelReward(CancelRewardRequest request)
  throws Exception
  {
    ProviderConfig providerConfig = providerConfigService.getConfig(moduleInfo.getModuleName(), request.getDomainName());
    return pragmaticFrbService.cancelFreeSpins(request, providerConfig);
  }
}
