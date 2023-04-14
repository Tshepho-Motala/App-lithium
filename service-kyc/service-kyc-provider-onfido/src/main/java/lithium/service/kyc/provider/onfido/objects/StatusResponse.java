package lithium.service.kyc.provider.onfido.objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import lithium.service.kyc.provider.onfido.entitites.CheckStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum StatusResponse {
    PENDING("pending"), SUCCESS("success"), FAIL("fail"), EMPTY(null);
    @Getter
    private String status;
    
    public static StatusResponse mapStatus(CheckStatus checkStatus) {
        switch (checkStatus) {
            case INITIATED, PROCESSING:
                return StatusResponse.PENDING;
            case COMPLETE:
                return StatusResponse.SUCCESS;
            case FAIL:
                return StatusResponse.FAIL;
        }
        return StatusResponse.EMPTY;
    }
}
