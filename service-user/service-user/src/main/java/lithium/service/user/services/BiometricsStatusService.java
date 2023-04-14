package lithium.service.user.services;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.kyc.client.OnfidoProviderClient;
import lithium.service.limit.client.objects.AutoRestrictionTriggerData;
import lithium.service.limit.client.stream.AutoRestrictionTriggerStream;
import lithium.service.user.client.enums.BiometricsStatus;
import lithium.service.user.client.exceptions.Status463CantUpdateBiometricStatusException;
import lithium.service.user.client.objects.PubSubEventType;
import lithium.service.user.client.objects.UserBiometricsStatusUpdate;
import lithium.service.user.data.entities.User;
import lithium.service.user.client.exceptions.Status462IncompleteOnfidoCheckException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@AllArgsConstructor
public class BiometricsStatusService {

  private final UserService userService;
  private final PubSubUserService pubSubUserService;
  private final ChangeLogService changeLogService;
  private final AutoRestrictionTriggerStream autoRestrictionTriggerStream;
  private final LithiumServiceClientFactory services;

  public User updateUserBiometricsStatus(UserBiometricsStatusUpdate userBiometricsStatusUpdate, Principal principal)
      throws Status462IncompleteOnfidoCheckException, Status463CantUpdateBiometricStatusException {
    var user = userService.findFromGuid(userBiometricsStatusUpdate.getUserGuid());
    var author = userService.findFromGuid(userBiometricsStatusUpdate.getAuthorGuid());
    String authorFullName = author.getFirstName() + " " + author.getLastName();

    return updateUserBiometricsStatus(
        user,
        BiometricsStatus.fromValue(userBiometricsStatusUpdate.getBiometricsStatus()),
        userBiometricsStatusUpdate.getComment(),
        userBiometricsStatusUpdate.getAuthorGuid(),
        authorFullName,
        principal
    );
  }

  public User updateUserBiometricsStatus(User user, BiometricsStatus status, String comment, String authorGuid, String authorFullName, Principal principal)
      throws Status462IncompleteOnfidoCheckException, Status463CantUpdateBiometricStatusException {
    if (BiometricsStatus.REQUIRED_PHOTO.equals(status) || BiometricsStatus.REQUIRED_VIDEO.equals(status)) {
      try {
        Response<Boolean> incompleteCheckResponse = getOnfidoClient().haveIncompleteCheck(user.getGuid());
        if (incompleteCheckResponse.getData()) {
          log.warn("Can't update biometric status due user(" + user.getGuid() + ") have incomplete check");
          throw new Status462IncompleteOnfidoCheckException("User have incomplete Onfido check");
        }
      } catch (LithiumServiceClientFactoryException e) {
        log.warn("Can't update biometric status (" + user.getGuid() + ") due can't check incomplete Onfido check");
        throw new Status463CantUpdateBiometricStatusException("Can't check incomplete Onfido check due " + e.getMessage());
      }
    }

    var currentStatus = user.getBiometricsStatus();

    user.setBiometricsStatus(status);

    user = userService.save(user);

    autoRestrictionTriggerStream.trigger(AutoRestrictionTriggerData.builder().userGuid(user.guid()).build());

    log.debug("Biometrics status for userGuid:" + user.getGuid() + " was updated with:" + user.getBiometricsStatus());

    addChangeLogs(user, currentStatus, status, comment, authorGuid, authorFullName);

    sendToPubSub(user, principal);

    return user;
  }

  private void sendToPubSub(User user, Principal principal) {
    try {
      pubSubUserService.buildAndSendPubSubAccountChange(user, principal, PubSubEventType.ACCOUNT_UPDATE);
    } catch (Exception e) {
      log.warn("Can't sent pub-sub message due " + e.getMessage());
    }
  }

  private OnfidoProviderClient getOnfidoClient() throws LithiumServiceClientFactoryException {
    return services.target(OnfidoProviderClient.class, "service-kyc-provider-onfido", true);
  }

  private void addChangeLogs(
      User user,
      BiometricsStatus fromStatus,
      BiometricsStatus toStatus,
      String comment,
      String authorGuid,
      String authorFullName) {

    List<ChangeLogFieldChange> clfc = new ArrayList<>();
    ChangeLogFieldChange c = ChangeLogFieldChange.builder()
        .field("biometrics_status")
        .fromValue(fromStatus.getValue())
        .toValue(toStatus.getValue())
        .build();
    clfc.add(c);

    changeLogService.registerChangesWithDomainAndFullName(
        "user",
        "edit",
        user.getId(),
        authorGuid,
        comment,
        null,
        clfc,
        Category.ACCOUNT,
        SubCategory.KYC,
        0,
        user.domainName(),
        authorFullName);
  }
}
