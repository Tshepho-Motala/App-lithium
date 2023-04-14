package lithium.service.casino.provider.evolution.controller;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.provider.evolution.model.request.CheckUserRequest;
import lithium.service.casino.provider.evolution.model.response.CheckUserResponse;
import lithium.service.casino.provider.evolution.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/check")
@RestController
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    public CheckUserResponse checkUser(@RequestBody CheckUserRequest checkUserRequest, @RequestParam String authToken) throws Status500InternalServerErrorException {
        return sessionService.checkUser(checkUserRequest, authToken);
    }

}
