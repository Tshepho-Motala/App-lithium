package lithium.service.kyc.provider.onfido.controller;

import lithium.service.Response;
import lithium.service.kyc.client.OnfidoProviderClient;
import lithium.service.kyc.provider.onfido.service.OnfidoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class OnfidoSystemController implements OnfidoProviderClient {
    private final OnfidoService onfidoService;

    @GetMapping("/system/user/have-incomplete-check")
    @Override
    public Response<Boolean> haveIncompleteCheck(String guid) {
        return Response.<Boolean>builder().data(onfidoService.haveIncompleteCheck(guid)).build();
    }
}
