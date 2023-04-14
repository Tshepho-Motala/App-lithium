package lithium.service.user.hibernate;

import java.util.List;
import lithium.metrics.LithiumMetricsService;
import lithium.metrics.Tag;
import lithium.service.user.client.objects.UserAttributesData;
import lithium.service.user.client.stream.UserAttributesTriggerStream;
import lithium.service.user.data.entities.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public abstract class UserAttributesChangesPropagator {

  UserAttributesTriggerStream userAttributesTriggerStream;
  LithiumMetricsService metricsService;

  protected void propagate(User user) {
    try {
      userAttributesTriggerStream.trigger(UserAttributesData.builder()
          .guid(user.guid())
          .testAccount(user.getTestAccount())
          .createdDate(user.getCreatedDate())
          .statusId(user.getStatus().getId())
          .playerTagIds(user.getPlayerTagIds())
          .build());
    } catch (Exception ex) {
      metricsService.counter("user-popagate-error", List.of(Tag.of("guid", user.guid()))).increment();
      log.error("Add UserAttributes sign to user stream failed: " + ex.getMessage(), ex);
    }
  }
}
