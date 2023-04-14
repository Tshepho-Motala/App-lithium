package lithium.service.user.hibernate;

import lithium.metrics.LithiumMetricsService;
import lithium.service.user.client.stream.UserAttributesTriggerStream;
import lithium.service.user.data.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PostCommitInsertEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.persister.entity.EntityPersister;

@Slf4j
public class NewUsersPropagator extends UserAttributesChangesPropagator implements PostCommitInsertEventListener {

  public NewUsersPropagator(UserAttributesTriggerStream userAttributesTriggerStream, LithiumMetricsService metricsService) {
    super(userAttributesTriggerStream, metricsService);
  }

  @Override
  public boolean requiresPostCommitHanding(EntityPersister entityPersister) {
    return true;
  }

  @Override
  public void onPostInsert(PostInsertEvent event) {
    final Object entity = event.getEntity();
    if (entity instanceof User user) {
      propagate(user);
    }
  }

  @Override
  public void onPostInsertCommitFailed(PostInsertEvent postInsertEvent) {
    log.error("onPostInsert : Cant propagate object =  {" + postInsertEvent.getEntity().toString() + "}");
  }
}
