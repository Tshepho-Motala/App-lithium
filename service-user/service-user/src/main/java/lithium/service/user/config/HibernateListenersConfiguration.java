package lithium.service.user.config;

import javax.persistence.EntityManagerFactory;
import lithium.metrics.LithiumMetricsService;
import lithium.service.user.client.stream.UserAttributesTriggerStream;
import lithium.service.user.hibernate.NewUsersPropagator;
import lithium.service.user.hibernate.UserUpdatesPropagator;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateListenersConfiguration {

  @Autowired
  private EntityManagerFactory entityManagerFactory;

  @Bean
  public EventListenerRegistry getEventListenerRegistry() {
    SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
    return sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
  }

  @Bean
  public NewUsersPropagator synchronizationInsertEventListener(UserAttributesTriggerStream stream, LithiumMetricsService metricsService) {
    var listener = new NewUsersPropagator(stream, metricsService);
    getEventListenerRegistry().getEventListenerGroup(EventType.POST_COMMIT_INSERT).appendListener(listener);
    return listener;
  }

  @Bean
  public UserUpdatesPropagator synchronizationUpdateEventListener(UserAttributesTriggerStream stream, LithiumMetricsService metricsService) {
    var listener = new UserUpdatesPropagator(stream, metricsService);
    getEventListenerRegistry().getEventListenerGroup(EventType.POST_COMMIT_UPDATE).appendListener(listener);
    return listener;
  }

}
