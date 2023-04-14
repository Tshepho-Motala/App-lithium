package lithium.service.casino.provider.sgs.controllers;

import lithium.service.casino.client.CasinoProviderMock;
import lithium.service.casino.client.objects.MockResponse;
import lithium.service.casino.client.objects.ProviderMockPayload;
import lithium.service.casino.provider.sgs.mock.MockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


@RestController
@RequestMapping(path = "/casino/mock")
public class MockController implements CasinoProviderMock {

	//FIXME: Add endgame transactions where needed

	@Autowired private MockService mockService;

	@RequestMapping(path="/bet", method=RequestMethod.POST)
	public @ResponseBody MockResponse bet(@RequestBody ProviderMockPayload mockPayload) {
		MockResponse betResponse = mockService.processBet(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				mockPayload.isRoundEnd(),
				true);

		//TODO: Can be added to response list but would need to modify the signature
		mockService.processEndgame(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId());

		return betResponse;
	}

	@RequestMapping(path="/win", method=RequestMethod.POST)
	public @ResponseBody MockResponse win(@RequestBody ProviderMockPayload mockPayload) {
		MockResponse winResponse = mockService.processWin(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				mockPayload.isRoundEnd(),
				true);

		mockService.processEndgame(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId());

		return winResponse;
	}

	@RequestMapping(path="/negativeBet", method=RequestMethod.POST)
	public @ResponseBody MockResponse negativeBet(@RequestBody ProviderMockPayload mockPayload) {
		MockResponse negativeBetResponse =  mockService.processNegativeBet(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				mockPayload.isRoundEnd(),
				true);

		mockService.processEndgame(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId());

		return negativeBetResponse;
	}

	@RequestMapping(path="/betAndWin", method= RequestMethod.POST)
	public @ResponseBody ArrayList<MockResponse> betAndWin(@RequestBody ProviderMockPayload mockPayload) {
		// For betsoft provider there is no single call in the API for this
		// we will instead do the individual calls with a single round id and send the responses back in a list
		ArrayList<MockResponse> response = new ArrayList<>();

		response.add(mockService.processBet(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				false,
				true));

		response.add(mockService.processWin(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsSecondaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				mockPayload.isRoundEnd(),
				false));

		response.add(mockService.processEndgame(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId()));

		return response;
	}

	@RequestMapping(path="/refund", method=RequestMethod.POST)
	public @ResponseBody MockResponse refund(@RequestBody ProviderMockPayload mockPayload) {
		return mockService.processRefund(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				mockPayload.isRoundEnd(),
				false);
	}

	@RequestMapping(path="/freespin", method=RequestMethod.POST)
	public @ResponseBody MockResponse freespin(@RequestBody ProviderMockPayload mockPayload) {
		return mockService.processFreespin(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				mockPayload.isRoundEnd());

	}

	@RequestMapping(path="/freespinFeature", method=RequestMethod.POST)
	public @ResponseBody MockResponse freespinFeature(@RequestBody ProviderMockPayload mockPayload) {
		// No feature freespin identification that makes it different from normal freespin
		return 	mockService.processFreespin(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				mockPayload.isRoundEnd());

	}

	@RequestMapping(path="/winAndNegativeBet", method=RequestMethod.POST)
	public @ResponseBody ArrayList<MockResponse> winAndNegativeBet(@RequestBody ProviderMockPayload mockPayload) {
		ArrayList<MockResponse> response = new ArrayList<>();

		response.add(mockService.processNegativeBet(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				false,
				true));

		response.add(mockService.processWin(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsSecondaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				mockPayload.isRoundEnd(),
				false));

		response.add(mockService.processEndgame(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId()));

		return response;
	}

	@RequestMapping(path="/betAndRefund", method=RequestMethod.POST)
	public @ResponseBody ArrayList<MockResponse> betAndRefund(@RequestBody ProviderMockPayload mockPayload) {
		ArrayList<MockResponse> response = new ArrayList<>();

		response.add(mockService.processBet(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				mockPayload.isRoundEnd(),
				true));

		response.add(mockService.processRefund(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				mockPayload.isRoundEnd(),
				false));

		response.add(mockService.processEndgame(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId()));

		return response;
	}

	@RequestMapping(path="/winAndRefund", method=RequestMethod.POST)
	public @ResponseBody ArrayList<MockResponse> winAndRefund(@RequestBody ProviderMockPayload mockPayload) {
		ArrayList<MockResponse> response = new ArrayList<>();

		response.add(mockService.processWin(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				mockPayload.isRoundEnd(),
				true));

		response.add(mockService.processRefund(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				mockPayload.isRoundEnd(),
				false));

		response.add(mockService.processEndgame(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId()));

		return response;
	}

	@RequestMapping(path="/negativeBetAndRefund", method=RequestMethod.POST)
	public @ResponseBody ArrayList<MockResponse> negativeBetAndRefund(@RequestBody ProviderMockPayload mockPayload) {
		ArrayList<MockResponse> response = new ArrayList<>();

		response.add(mockService.processNegativeBet(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				mockPayload.isRoundEnd(),
				true));

		response.add(mockService.processRefund(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				mockPayload.isRoundEnd(),
				false));

		return response;
	}

	@RequestMapping(path="/winAndNegativeBetAndRefund", method=RequestMethod.POST)
	public @ResponseBody ArrayList<MockResponse> winAndNegativeBetAndRefund(@RequestBody ProviderMockPayload mockPayload) {
		ArrayList<MockResponse> response = new ArrayList<>();

		response.add(mockService.processNegativeBet(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				false,
				true));

		response.add(mockService.processWin(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsSecondaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				mockPayload.isRoundEnd(),
				false));

		response.add(mockService.processRefund(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				mockPayload.isRoundEnd(),
				false));

		return response;
	}

	@RequestMapping(path="/freespinAndRefund", method=RequestMethod.POST)
	public @ResponseBody ArrayList<MockResponse> freespinAndRefund(@RequestBody ProviderMockPayload mockPayload) {
		ArrayList<MockResponse> response = new ArrayList<>();

		response.add(mockService.processFreespin(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				mockPayload.isRoundEnd()));

		response.add(mockService.processRefund(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				mockPayload.isRoundEnd(),
				false));

		return response;

	}

	@RequestMapping(path="/freespinFeatureAndRefund", method=RequestMethod.POST)
	public @ResponseBody ArrayList<MockResponse> freespinFeatureAndRefund(@RequestBody ProviderMockPayload mockPayload) {
		ArrayList<MockResponse> response = new ArrayList<>();

		response.add(mockService.processFreespin(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				mockPayload.isRoundEnd()));

		response.add(mockService.processRefund(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				mockPayload.isRoundEnd(),
				false));

		return response;
	}

	@RequestMapping(path="/betAndWinAndRefund", method=RequestMethod.POST)
	public @ResponseBody ArrayList<MockResponse> betAndWinAndRefund(@RequestBody ProviderMockPayload mockPayload) {
		ArrayList<MockResponse> response = new ArrayList<>();

		response.add(mockService.processBet(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				false,
				true));

		response.add(mockService.processWin(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsSecondaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				mockPayload.isRoundEnd(),
				false));

		response.add(mockService.processRefund(
				mockPayload.getUserGuid(),
				mockPayload.getTransactionId(),
				mockPayload.getAmountCentsPrimaryAction(),
				mockPayload.getAuthToken(),
				mockPayload.getProviderGameId(),
				mockPayload.getCurrency(),
				mockPayload.getRoundId(),
				mockPayload.isRoundEnd(),
				false));

		return response;
	}

	@RequestMapping(path="/balance", method=RequestMethod.POST)
	public @ResponseBody MockResponse balance(@RequestBody ProviderMockPayload mockPayload) {
		return mockService.processBalance(mockPayload.getUserGuid(), mockPayload.getAuthToken());
	}

	@RequestMapping(path="/validateUser", method=RequestMethod.POST)
	public @ResponseBody MockResponse validateUser(@RequestBody ProviderMockPayload mockPayload) {
		return mockService.processAuthenticateUser(mockPayload.getUserGuid(), mockPayload.getAuthToken());
	}

}
