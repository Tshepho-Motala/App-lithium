package lithium.service.user.jobs;

import java.util.List;
import java.util.Objects;
import lithium.service.user.client.objects.UserAttributesData;
import lithium.service.user.data.entities.User;
import lithium.service.user.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class DataMigrationService {

  @Autowired
  private UserService userService;

  @Transactional(readOnly = true)
  public List<UserAttributesData> getUserAttributesForSync(Pageable page) {
    Page<User> users = userService.findAll(page);
    return users.stream()
        .map(user -> UserAttributesData.builder()
            .guid(user.guid())
            .testAccount(user.getTestAccount())
            .createdDate(user.getCreatedDate())
            .statusId(user.getStatus().getId())
            .playerTagIds(user.getPlayerTagIds())
            .build())
        .toList();
  }

  @Transactional(readOnly = true)
  public List<UserAttributesData> getUserAttributesForSyncByGuids(List<String> guids) {
    return guids.stream()
        .map(this::get)
        .filter(Objects::nonNull)
        .map(this::toUserAttributes)
        .toList();
  }

  private UserAttributesData toUserAttributes(User user) {
    return UserAttributesData.builder()
        .guid(user.guid())
        .testAccount(user.getTestAccount())
        .createdDate(user.getCreatedDate())
        .statusId(user.getStatus().getId())
        .playerTagIds(user.getPlayerTagIds())
        .build();
  }

  private User get(String guid) {
    User user = userService.findFromGuid(guid);
    if (user == null) {
      log.error(":: Migration of users attributes: cant find user by guid=[" + guid + "]");
    }
    return user;
  }
}
