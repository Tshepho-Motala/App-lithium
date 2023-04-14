package lithium.service.user.hibernate;

import static lithium.service.user.hibernate.UserUpdatesPropagator.PROPAGATED_FIELDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;
import lithium.service.user.client.stream.UserAttributesTriggerStream;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.Status;
import lithium.service.user.data.entities.User;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.persister.entity.EntityPersister;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
public class UserUpdatesPropagatorTest {

  @InjectMocks
  private UserUpdatesPropagator userUpdatesPropagator;
  @Mock
  private UserAttributesTriggerStream userAttributesTriggerStream;

  private final User userTypeObject = getUser();

  @Mock
  private EntityPersister userTypeObjectPersister;

  @BeforeEach
  public void initialize() {
    lenient().when(userTypeObjectPersister.getPropertyNames()).thenReturn(
        getUserPropertyNames()
    );
  }

  @Test
  public void propagatedFieldsShouldMatchActualUserFields() {
    List<String> userFields = Arrays.stream(User.class.getDeclaredFields())
        .map(Field::getName)
        .toList();
    Assertions.assertTrue(userFields.containsAll(PROPAGATED_FIELDS));
  }

  @Test
  public void shouldPropagateOnlyUserEntitiesChanges() {
    PostUpdateEvent notUserPostUpdateEvent = new PostUpdateEvent(new Domain(), -1, null, null, null, null, null);
    userUpdatesPropagator.onPostUpdate(notUserPostUpdateEvent);
    Mockito.verify(userAttributesTriggerStream, never()).trigger(any());
  }

  @Test
  public void shouldPropagateOnlyUserSelectedFieldChange() {
    String selectedFieldName = "testAccount";
    Assertions.assertTrue(PROPAGATED_FIELDS.contains(selectedFieldName));

    int[] changedPropertiesIndexes = {getIndexOfProperty(selectedFieldName, getUserPropertyNames())};
    userUpdatesPropagator.onPostUpdate(getUserPostUpdateEvent(changedPropertiesIndexes));
    Mockito.verify(userAttributesTriggerStream).trigger(any());
  }

  @Test
  public void shouldPropagateUserFieldChangesIfFieldNamePresentInAllowedList() {
    String selectedFieldName1 = "testAccount";
    Assertions.assertTrue(PROPAGATED_FIELDS.contains(selectedFieldName1));
    String selectedFieldName2 = "guid";
    Assertions.assertFalse(PROPAGATED_FIELDS.contains(selectedFieldName2));
    int[] changedPropertiesIndexes = {
        getIndexOfProperty(selectedFieldName1, getUserPropertyNames()),
        getIndexOfProperty(selectedFieldName2, getUserPropertyNames())
    };
    userUpdatesPropagator.onPostUpdate(getUserPostUpdateEvent(changedPropertiesIndexes));
    Mockito.verify(userAttributesTriggerStream).trigger(any());
  }

  /**
   * need it until have walkaround, see comment for UserUpdatePropagator::isUnknownChanges
   */
  @Test
  public void shouldPropagateUserIfChangesUnknown() {
    int[] noChanges = {};
    userUpdatesPropagator.onPostUpdate(getUserPostUpdateEvent(noChanges));
    Mockito.verify(userAttributesTriggerStream).trigger(any());
  }

  @Test
  public void shouldNotPropagateNotSelectedUserProperties() {
    String selectedFieldName = "guid";
    Assertions.assertFalse(PROPAGATED_FIELDS.contains(selectedFieldName));

    int[] changedPropertiesIndexes = {getIndexOfProperty(selectedFieldName, getUserPropertyNames())};
    userUpdatesPropagator.onPostUpdate(getUserPostUpdateEvent(changedPropertiesIndexes));
    Mockito.verify(userAttributesTriggerStream, never()).trigger(any());
  }

  private PostUpdateEvent getUserPostUpdateEvent(int[] dirtyProperties) {
    return new PostUpdateEvent(
        userTypeObject,
        -1,
        null,
        null,
        dirtyProperties,
        userTypeObjectPersister,
        null
    );
  }

  private String[] getUserPropertyNames() {
    List<String> names = Arrays.stream(User.class.getDeclaredFields())
        .map(Field::getName)
        .toList();
    return names.toArray(new String[0]);
  }

  private int getIndexOfProperty(String fieldName, String[] fieldNames) {
    return IntStream.range(0, fieldNames.length)
        .filter(i -> fieldNames[i].equals(fieldName))
        .findFirst().orElseThrow(IllegalArgumentException::new);
  }

  private static User getUser() {
    return User.builder()
        .firstName("Dmytro")
        .lastName("Sapyga")
        .testAccount(true)
        .status(Status.builder()
            .id(-1L)
            .name("Test Status")
            .build())
        .createdDate(new Date())
        .build();
  }
}
