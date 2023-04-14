package lithium.service.user.controllers.system;

import lithium.client.changelog.Category;
import lithium.service.Response;
import lithium.service.user.client.objects.UserAccountStatusUpdate;
import lithium.service.user.data.entities.User;
import lithium.service.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/account-status")
public class SystemAccountStatusController {
  @Autowired private UserService userService;

  @PostMapping("/change")
  public User changeAccountStatus(@RequestBody UserAccountStatusUpdate statusUpdate) {
    return userService.changeUserStatus(statusUpdate);
  }
}
