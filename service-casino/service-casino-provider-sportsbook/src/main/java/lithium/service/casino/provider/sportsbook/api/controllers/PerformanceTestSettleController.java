package lithium.service.casino.provider.sportsbook.api.controllers;

import lithium.metrics.SportsbookMetricService;
import lithium.service.casino.provider.sportsbook.api.schema.settle.SettleMultiRequest;
import lithium.service.casino.provider.sportsbook.api.schema.settle.SettleMultiResponse;
import lithium.service.casino.provider.sportsbook.services.SettleService;
import lithium.service.casino.provider.sportsbook.shared.service.GuidConverterService;
import lithium.service.casino.provider.sportsbook.stream.PerformanceTestSettlementQueueStream;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@ConditionalOnProperty(
    name = "lithium.services.casino.provider.sportsbook.performance-testing.settle-multi.block-endpoint",
    havingValue = "false")
public class PerformanceTestSettleController {
    @Autowired @Setter
    SettleService service;

    @Autowired
    GuidConverterService guidConverterService;
    @Autowired
    PerformanceTestSettlementQueueStream testSettlementQueueStream;

    @Autowired
    SportsbookMetricService metrics;

    @PostMapping("/settle/perf-test")
    public SettleMultiResponse settleMultiPerformanceTest(
        @RequestParam(defaultValue = "en_US") String locale,
        @RequestBody SettleMultiRequest multiRequest
    ) {
        log.info("settlperftestemulti pre " + multiRequest);
        testSettlementQueueStream.triggerPerformanceTestSettlement(multiRequest);
        // Because we can; this is only a test.
        return new SettleMultiResponse();
    }
}