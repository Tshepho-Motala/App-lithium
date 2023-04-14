package lithium.service.reward.provider.casino.pragmatic.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.client.objects.Granularity;
import lithium.service.reward.client.dto.RewardRevision;
import lithium.service.reward.provider.casino.pragmatic.config.ProviderConfig;
import lithium.service.reward.provider.casino.pragmatic.dto.PragmaticGame;
import lithium.service.reward.provider.casino.pragmatic.dto.PragmaticRequest;
import lithium.service.reward.provider.casino.pragmatic.dto.PragmaticRewardResponse;
import lithium.service.reward.provider.casino.pragmatic.dto.VariableBetValue;
import lithium.service.reward.provider.casino.pragmatic.enums.RewardTypeFieldName;
import lithium.service.reward.provider.casino.pragmatic.services.PragmaticFrbService;
import lithium.service.reward.provider.client.dto.CancelRewardRequest;
import lithium.service.reward.provider.client.dto.CancelRewardResponse;
import lithium.service.reward.provider.client.dto.ProcessRewardRequest;
import lithium.service.reward.provider.client.dto.ProcessRewardResponse;
import lithium.service.reward.provider.client.dto.ProcessRewardStatus;
import lithium.util.Hash;
import lithium.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PragmaticFrbServiceImpl implements PragmaticFrbService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public ProcessRewardResponse awardFreeSpins(ProcessRewardRequest processRewardRequest, ProviderConfig providerConfig) {

        ProcessRewardResponse processRewardResponse = ProcessRewardResponse.builder().build();
        String errorFormattedString = "Failed to award external pragmatic freespins to user:{}, numbersOfFreeSpins:{}, coinValue:{}, reason: {}";
        long amountInCents = processRewardRequest.findRewardTypeValue(RewardTypeFieldName.ROUND_VALUE_IN_CENTS.getName(), Long.class);
        long numberOfSpins = processRewardRequest.findRewardTypeValue(RewardTypeFieldName.NUMBER_OF_ROUNDS.getName(), Long.class);
        PragmaticRewardResponse pragmaticRewardResponse;

        String playerId = generateExternalPlayerIdentifier(providerConfig, processRewardRequest.getPlayer().domainName(), Long.parseLong(processRewardRequest.getPlayer().getOriginalId()));

        try {
            final String rewardId = UUID.nameUUIDFromBytes(String.format("PLAYER_PRAGMATIC_REWARD_%s", processRewardRequest.getPlayerRewardTypeHistoryId()).getBytes()).toString();
            String baseUrl = !providerConfig.getRewardsBaseUrl().endsWith("/") ? providerConfig.getRewardsBaseUrl() + "/" : providerConfig.getRewardsBaseUrl();
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(MessageFormat.format("{0}IntegrationService/v3/http/FreeRoundsBonusAPI/v2/bonus/player/create", baseUrl))
                  .queryParam("bonusCode", rewardId)
                  .queryParam("currency", processRewardRequest.domainCurrency())
                  .queryParam("expirationDate", getRewardExpiration(processRewardRequest.getRewardRevision(), providerConfig))
                  .queryParam("playerId", playerId)
                  .queryParam("rounds", numberOfSpins)
                  .queryParam("secureLogin", providerConfig.getPragmaticUserName())
                  .queryParam("startDate", LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
            uriBuilder.queryParam("hash", calculateHash(uriBuilder.build().getQuery(), providerConfig));

            PragmaticRequest pragmaticRequest = buildRewardRequest(processRewardRequest, amountInCents);
            pragmaticRewardResponse = executeReward(uriBuilder.build().toUriString(), pragmaticRequest);
            if (pragmaticRewardResponse.getError().equals("0")) {
                log.debug("Pragmatic free spins were successfully granted to user:{}, numberOfSpins:{}, coinValue:{}, reference: {}"
                      , processRewardRequest.getPlayer().getGuid(), numberOfSpins, amountInCents, rewardId);
                processRewardResponse.setStatus(ProcessRewardStatus.SUCCESS);
                processRewardResponse.setAmountAffected(amountInCents * numberOfSpins);
                processRewardResponse.setValueUsed(0L);
                processRewardResponse.setValueGiven(numberOfSpins);
                processRewardResponse.setValueInCents(amountInCents);
                processRewardResponse.setExternalReferenceId(rewardId);
                return processRewardResponse;
            } else {
                log.error(errorFormattedString, processRewardRequest.getPlayer().getGuid(), numberOfSpins, amountInCents, pragmaticRewardResponse);
                processRewardResponse.setStatus(ProcessRewardStatus.FAILED);
                processRewardResponse.setErrorCode(pragmaticRewardResponse.getError() != null ? Integer.parseInt(pragmaticRewardResponse.getError()) : -1);
                processRewardResponse.setDescription(pragmaticRewardResponse.getDescription());
            }
        } catch (Exception e) {
            log.error(errorFormattedString, processRewardRequest.getPlayer().getGuid(), numberOfSpins, amountInCents, e);
        }

        return processRewardResponse;
    }

    @Override
    public CancelRewardResponse cancelFreeSpins(CancelRewardRequest cancelRequest, ProviderConfig providerConfig) {
        CancelRewardResponse cancelRewardResponse = CancelRewardResponse.builder().build();
        String errorFormattedString = "Failed to cancel external pragmatic free spins for user:{}, reference:{}, reason: {}";
        PragmaticRewardResponse pragmaticRewardResponse;

        try {
            String baseUrl = !providerConfig.getRewardsBaseUrl().endsWith("/") ? providerConfig.getRewardsBaseUrl() + "/" : providerConfig.getRewardsBaseUrl();
            String url = MessageFormat.format("{0}IntegrationService/v3/http/FreeRoundsBonusAPI/v2/bonus/cancel", baseUrl);
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>(3);
            params.add("secureLogin", providerConfig.getPragmaticUserName());
            params.add("bonusCode", cancelRequest.getReferenceId());

            String hashParameter = MessageFormat.format("bonusCode={0}&secureLogin={1}", cancelRequest.getReferenceId(), providerConfig.getPragmaticUserName());
            params.add("hash", calculateHash(hashParameter, providerConfig));
            pragmaticRewardResponse = executeCancel(url, params);

            if (pragmaticRewardResponse.getError().equals("0")) {
                log.debug("Pragmatic free spins with reference:{} for user:{} were cancelled successfully on Pragmatic", cancelRequest.getReferenceId(), cancelRequest.getPlayerGuid());
                cancelRewardResponse.setCode("0");
                return cancelRewardResponse;
            } else {
                log.debug(errorFormattedString, cancelRequest.getPlayerGuid(), cancelRequest.getReferenceId(), pragmaticRewardResponse);
                cancelRewardResponse.setCode(pragmaticRewardResponse.getError());
                cancelRewardResponse.setDescription(pragmaticRewardResponse.getDescription());
            }
        } catch (Exception e) {
            cancelRewardResponse.setCode("-1");
            log.error(errorFormattedString, cancelRequest.getPlayerGuid(),
                  cancelRequest.getReferenceId(), e);
        }

        return cancelRewardResponse;
    }

    private PragmaticRequest buildRewardRequest(ProcessRewardRequest processRewardRequest, long amountInCents) {
        return PragmaticRequest.builder()
              .gameList(processRewardRequest.getRewardRevisionTypeGames().stream().map(rewardRevisionTypeGame -> {
                  PragmaticGame pragmaticGame = PragmaticGame.builder().gameId(rewardRevisionTypeGame.getGameId()).build();
                  VariableBetValue variableBetValue = VariableBetValue.builder()
                        .betPerLine(BigDecimal.valueOf(amountInCents, 2))
                        .currency(processRewardRequest.domainCurrency())
                        .build();
                  pragmaticGame.getBetValues().add(variableBetValue);
                  return pragmaticGame;
              }).collect(Collectors.toList()))
              .build();
    }

    private String calculateHash(String parameter, ProviderConfig config) throws Exception {
        return Hash.builderMd5(parameter + config.getHashSecret()).build().md5();
    }

    private Long getRewardExpiration(RewardRevision rewardRevision, ProviderConfig config) {
        ZonedDateTime now = LocalDateTime.now().atZone(ZoneId.systemDefault());

        Granularity granularity = Optional.ofNullable(rewardRevision.getValidForGranularity())
                .map(Granularity::fromGranularity).orElse(null);

        if ((rewardRevision.getValidFor() != null) && (granularity != null)) {
            now = switch (granularity) {
                case GRANULARITY_HOUR -> now.plusHours(rewardRevision.getValidFor());
                case GRANULARITY_DAY -> now.plusDays(rewardRevision.getValidFor());
                case GRANULARITY_WEEK -> now.plusWeeks(rewardRevision.getValidFor());
                case GRANULARITY_MONTH -> now.plusMonths(rewardRevision.getValidFor());
                case GRANULARITY_YEAR -> now.plusYears(rewardRevision.getValidFor());
                default -> now.plusDays(config.getMaxRewardLifetimeInDays());
            };
        } else {
            now.plusDays(config.getMaxRewardLifetimeInDays());
        }

        return now.toInstant().getEpochSecond();
    }

    private PragmaticRewardResponse executeReward(String url, PragmaticRequest requestBody) throws JsonProcessingException {
        HttpHeaders headers = getCommonHeaders();
        HttpEntity<PragmaticRequest> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        return objectMapper.readValue(response.getBody(), PragmaticRewardResponse.class);
    }

    private PragmaticRewardResponse executeCancel(String url, MultiValueMap<String, String> params) throws JsonProcessingException {
        HttpHeaders headers = getRewardCancelHeaders();
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        return objectMapper.readValue(response.getBody(), PragmaticRewardResponse.class);
    }


    public String generateExternalPlayerIdentifier(ProviderConfig config, String domainName, Long playerId) {

        long id = playerId;

        if (!StringUtil.isEmpty(config.getPlayerOffset())) {
            id = id + Long.parseLong(config.getPlayerOffset());
        }

        return MessageFormat.format("{0}-{1}/{2}", config.getPlayerGuidPrefix(), domainName, String.valueOf(id));
    }

    public HttpHeaders getCommonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    public HttpHeaders getRewardCancelHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        return headers;
    }
}
