package lithium.service.kyc.client;

import lithium.service.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name="service-kyc-provider-onfido")
public interface OnfidoProviderClient {

    @GetMapping("/system/user/have-incomplete-check")
    Response<Boolean> haveIncompleteCheck(@RequestParam String guid);

}
