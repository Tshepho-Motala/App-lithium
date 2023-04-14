package lithium.service.changelog.controllers.system;

import lithium.service.Response;
import lithium.service.changelog.services.ChangelogEntriesService;
import lithium.service.user.client.objects.User;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/system/changelogs")
public class SystemChangelogController {

  @Autowired ChangelogEntriesService changelogEntriesService;

  @PostMapping(value="/user/add-note")
  public Response<String> addNote(@RequestBody lithium.client.changelog.objects.ChangeLog changeLog, LithiumTokenUtil util) throws Exception {
    try {
      changeLog.setAuthorFullName(User.SYSTEM_FULL_NAME);
      changelogEntriesService.addNote(changeLog, util) ;
      return Response.<String>builder().status(Response.Status.OK).build();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Response.<String>builder().status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }
}
