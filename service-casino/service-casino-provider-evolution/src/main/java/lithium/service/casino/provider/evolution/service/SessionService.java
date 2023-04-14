package lithium.service.casino.provider.evolution.service;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.provider.evolution.model.request.CheckUserRequest;
import lithium.service.casino.provider.evolution.model.response.CheckUserResponse;


public interface SessionService {
    CheckUserResponse checkUser(CheckUserRequest checkUserRequest, String authToken) throws Status500InternalServerErrorException;
}
