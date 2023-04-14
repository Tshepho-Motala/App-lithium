package lithium.service.casino.search.controllers.system;

import lithium.exceptions.Status425DateParseException;
import lithium.service.casino.client.objects.request.CommandParams;
import lithium.service.casino.client.objects.response.CasinoBetHistoryCsvResponse;
import lithium.service.casino.search.services.casino.BetHistoryService;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/system/bethistory")
public class SystemBetHistoryController {
  @Autowired
  @Qualifier("casino.BetHistoryService")
  private BetHistoryService service;

  @PostMapping(value = "/get-casino-best-history-list")
  public CasinoBetHistoryCsvResponse getList(@RequestBody CommandParams commandParams, LithiumTokenUtil tokenUtil)
      throws Status425DateParseException {
    try {
      return service.generateCsvRecords(commandParams, tokenUtil);
    } catch (LithiumServiceClientFactoryException e) {
      log.error("Problem getting User client", e);
    }
    return null;
  }
}
