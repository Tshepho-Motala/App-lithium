package lithium.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lithium.metrics.builders.sportsbook.EntryPoint;
import lithium.metrics.builders.sportsbook.SportsbookBetStatus;
import lithium.metrics.builders.sportsbook.SportsbookBetType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class SportsbookMetricService {

  private final LithiumMetricsService metricsService;
  private final static String metricName = "scp_sportsbook";

  public void recordTime(String name, String description, long duration, TimeUnit timeUnit, String ... tags) {
    metricsService.recordTime(metricName + "_" + name, description, duration, timeUnit, tags);
  }

  public void counter(String domainName, EntryPoint entryPoint,
      SportsbookBetType sportsbookBetType, SportsbookBetStatus sportsbookBetStatus) {
    counter(domainName, entryPoint, sportsbookBetType, sportsbookBetStatus, null, null);
  }

  public void counter(String domainName, EntryPoint entryPoint,
      SportsbookBetType sportsbookBetType, SportsbookBetStatus sportsbookBetStatus, String extraKey, String extraValue) {

    List<Tag> tagList = new ArrayList<>();
    tagList.addAll(List.of(
        Tag.of("domainName", domainName),
        Tag.of("type", sportsbookBetType.name().toLowerCase()),
        Tag.of("status", sportsbookBetStatus.name().toLowerCase())));
    if (extraKey != null && extraValue != null) {
      tagList.add(Tag.of(extraKey, extraValue));
    }

    metricsService.counter(metricName + "_" + entryPoint.name().toLowerCase(), tagList)
        .increment();
  }
}
