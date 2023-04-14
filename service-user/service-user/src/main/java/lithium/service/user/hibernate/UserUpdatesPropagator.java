package lithium.service.user.hibernate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import lithium.metrics.LithiumMetricsService;
import lithium.service.user.client.stream.UserAttributesTriggerStream;
import lithium.service.user.data.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PostCommitUpdateEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.persister.entity.EntityPersister;

@Slf4j
public class UserUpdatesPropagator extends UserAttributesChangesPropagator implements PostCommitUpdateEventListener  {

  /**
   * actuality of fields names is checked in {@link lithium.service.user.hibernate.UserUpdatesPropagatorTest#propagatedFieldsShouldMatchActualUserFields()}
   */
  public static final List<String> PROPAGATED_FIELDS = List.of("testAccount", "createdDate", "status", "userCategories");

  public UserUpdatesPropagator(UserAttributesTriggerStream userAttributesTriggerStream, LithiumMetricsService metricsService) {
    super(userAttributesTriggerStream, metricsService);
  }

  @Override
  public void onPostUpdate(PostUpdateEvent event) {
    if (event.getEntity() instanceof User user && hasTrackedChanges(event)) {
      propagate(user);
    }
  }

  @Override
  public boolean requiresPostCommitHanding(
      EntityPersister persister) {
    return true;
  }

  @Override
  public void onPostUpdateCommitFailed(PostUpdateEvent postUpdateEvent) {
    log.error("onPostUpdate : Cant propagate object =  {" + postUpdateEvent.getEntity().toString() + "}");
  }

  private static boolean hasTrackedChanges(PostUpdateEvent event) {
    return getUpdatedFields(event)
        .anyMatch(PROPAGATED_FIELDS::contains);
  }

  private static Stream<String> getUpdatedFields(PostUpdateEvent event) {
    String[] fieldNames = event.getPersister().getPropertyNames();
    if (isUnknownChanges(event)) {
      return Arrays.stream(fieldNames);
    }
    return Arrays.stream(event.getDirtyProperties())
        .mapToObj(i -> fieldNames[i]);
  }

  /* TODO: we found that in case of changed lazy loaded relationships (UserCategories in our case)
    by some reason both PostUpdateEvent.state and PostUpdateEvent.oldState contains changes and therefore dirtyProperties are empty.
    It may be the different reasons for such behaviour, need deeper investigation.
    Temporary solution is to propagate such events to not miss possible important updates.*/
  private static boolean isUnknownChanges(PostUpdateEvent event) {
    return event.getDirtyProperties().length == 0;
  }
}
