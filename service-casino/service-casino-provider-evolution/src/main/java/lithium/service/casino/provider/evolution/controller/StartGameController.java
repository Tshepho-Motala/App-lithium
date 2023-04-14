package lithium.service.casino.provider.evolution.controller;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.evolution.service.StartGameService;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.games.client.GamesClient;
import lithium.service.games.client.exceptions.Status429UserLoggedOutException;
import lithium.service.games.client.exceptions.Status502ProviderProcessingException;
import lithium.service.games.client.objects.Game;
import lithium.service.games.client.objects.GameUserStatus;
import lithium.service.games.client.objects.User;
import lithium.service.limit.client.exceptions.Status483PlayerCasinoNotAllowedException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.user.client.exceptions.Status411UserNotFoundException;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.util.ExceptionMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@RestController
//FIXME: Should we rather have StartGameClient (and implement it) instead of having all those unimplemented methods on GameClient
public class StartGameController implements GamesClient {

  StartGameService startGameService;
  LocaleContextProcessor localeContextProcessor;

  public StartGameController(StartGameService startGameService,
      LocaleContextProcessor localeContextProcessor) {
    this.startGameService = startGameService;
    this.localeContextProcessor = localeContextProcessor;
  }

  @Override
  public Response<GameUserStatus> toggleLocked(Long gameId, User user) {
    return null;
  }

  @Override
  public Response<GameUserStatus> unlock(String gameGuid, User user) {
    return null;
  }

  @Override
  public List<Game> listGames(String domainName) throws Exception {
    return null;
  }

  @Override
  public List<Game> listFrbGames(String domainName) throws Exception {
    return null;
  }

  @RequestMapping("/games/{domainName}/startGame")
  public Response<String> startGame(
      @PathVariable("domainName") String domainName,
      @RequestParam("token") String token,
      @RequestParam("gameId") String gameId,
      @RequestParam(value = "lang") String lang,
      @RequestParam(value = "currency") String currency,
      @RequestParam(value = "os", required = false) String os,
      @RequestParam(value = "machineGUID", required = false) String machineGUID,
      @RequestParam(value = "tutorial", required = false) Boolean tutorial,
      @RequestParam(value = "platform", required = false, defaultValue = "desktop") String platform
  ) throws
          Status429UserLoggedOutException,
          Status483PlayerCasinoNotAllowedException,
          Status500LimitInternalSystemClientException,
          Status502ProviderProcessingException,
          Status512ProviderNotConfiguredException,
          Status550ServiceDomainClientException, Status500InternalServerErrorException { // check exceptions
    localeContextProcessor.setLocaleContextHolder(lang, domainName);
    return startGame(domainName, gameId, token, false, platform, lang);
  }

  @RequestMapping("/games/{domainName}/demoGame")
  public Response<String> demoGame(
      @PathVariable("domainName") String domainName,
      @RequestParam("gameId") String gameId,
      @RequestParam(value = "lang") String lang,
      @RequestParam(value = "os") String os
  )
      throws Status512ProviderNotConfiguredException, Status550ServiceDomainClientException, Status483PlayerCasinoNotAllowedException, Status500InternalServerErrorException {
    return startGame(domainName, gameId, null, true, "desktop", lang);
  }

  @Override
  public Response<Game> addGame(String providerGuid, String providerGameId, String gameName) throws Exception {
    return null;
  }

  @Override
  public Response<Game> findById(Long gameId) throws Exception {
    return null;
  }

  @Override
  public Response<Game> editGraphic(Long gameId, String graphicFunction, MultipartFile file) throws Exception {
    return null;
  }

  @Override
  public Response<Game> edit(Game game) throws Exception {
    return null;
  }

  @Override
  public Response<Iterable<Game>> listDomainGames(String domainName) throws Exception {
    return null;
  }

  @Override
  public List<Game> listDomainGamesPerChannel(String domainName, String channel, Boolean enabled, Boolean visible) throws Exception {
    return null;
  }

  @Override
  public Response<Game> findByGuidAndDomainName(String domainName, String gameGuid) throws Exception {
    return null;
  }

  @Override
  public Response<Game> findByGuidAndDomainNameNoLabels(String domainName, String gameGuid) throws Exception {
    return null;
  }

  @Override
  public DataTableResponse<Game> listDomainGames(String domainName, Boolean enabled, String drawEcho, Long start, Long length) {
    return null;
  }

  @Override
  public DataTableResponse<Game> listDomainGamesReport(String domainName, String drawEcho, Long start, Long length) {
    return null;
  }

  @Override
  public Response<Boolean> isGameLockedForPlayer(String domainName, String gameGuid, String playerGuid) {
    return null;
  }

  private Response<String> startGame(String domainName, String gameId, String token, boolean demo, String platform, String lang)
      throws
      Status512ProviderNotConfiguredException,
      Status550ServiceDomainClientException, Status500InternalServerErrorException {
    try {
      log.info(format("Evolution %s request with domainName: %s, gameId: %s", playMode(demo), domainName, gameId));

      String responseUrl = startGameService.startGame(token, gameId, domainName, demo, platform, lang);

      log.info(format("%s response domainName: %s, gameId: %s, responseUrl: %s", playMode(demo), domainName, gameId,
          responseUrl));
      return Response.<String>builder().data(responseUrl).build();
    } catch (Status483PlayerCasinoNotAllowedException status483PlayerCasinoNotAllowedException){
      log.debug("start-game player casino not allowed exception ["
          + "DomainName " + domainName
          + ", GameId " + gameId + "] "
          + ExceptionMessageUtil.allMessages(status483PlayerCasinoNotAllowedException), status483PlayerCasinoNotAllowedException);
      throw status483PlayerCasinoNotAllowedException;
    } catch (UnsupportedEncodingException | Status411UserNotFoundException |
             LithiumServiceClientFactoryException
             | UserClientServiceFactoryException | UserNotFoundException | Status500InternalServerErrorException exception) {
      String message = format("%s URL exception [domainName: %s, gameId: %s] %s", playMode(demo), domainName, gameId,
          ExceptionMessageUtil.allMessages(exception));
      log.error(message, exception);
      throw new Status500InternalServerErrorException(message);
    }
  }

  // ?
  private Object playMode(boolean demo) {
    return demo;
  }

}
