package lithium.service.user.threshold.controllers.backoffice;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.NOT_FOUND;
import static lithium.service.Response.Status.OK;

import java.math.BigDecimal;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.user.threshold.client.dto.ThresholdDto;
import lithium.service.user.threshold.client.enums.EType;
import lithium.service.user.threshold.data.entities.Threshold;
import lithium.service.user.threshold.service.ThresholdService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/backoffice/threshold/loss-limit/{domainName}/v1")
public class BackofficeThresholdLimitsV1Controller implements IBackofficeThresholdLimits {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ThresholdService thresholdService;

    @Override
    public Response<ThresholdDto> find(String domainName, Integer granularity, Integer ageMin, Integer ageMax,String eType, LithiumTokenUtil lithiumTokenUtil) {
        EType e = EType.fromName(eType);
        Threshold threshold = thresholdService.find(domainName,e, granularity, ageMin, ageMax, lithiumTokenUtil);
        if (ObjectUtils.isEmpty(threshold)) {
            return Response.<ThresholdDto>builder().status(OK).data(thresholdService.empty()).build();
        }
        return Response.<ThresholdDto>builder().data(thresholdService.mapDto(modelMapper, threshold)).status(OK).build();
    }

    @Override
    public Response<ThresholdDto> save(String domainName, Threshold threshold, BigDecimal percentage, BigDecimal amount, String eType, Integer granularity, Integer ageMin, Integer ageMax,
                             LithiumTokenUtil lithiumTokenUtil)
            throws Status500InternalServerErrorException {
        EType eType1 = EType.fromName(eType);
        threshold = thresholdService.save(domainName, threshold, percentage, amount, eType1, granularity, ageMin, ageMax, lithiumTokenUtil);
        return Response.<ThresholdDto>builder().data(thresholdService.mapDto(modelMapper, threshold)).status(OK).build();
    }

    @Override
    public Response<ThresholdDto> disable(String domainName, Threshold threshold,String eType, LithiumTokenUtil lithiumTokenUtil) {
        EType eType1 = EType.fromName(eType);
        threshold = thresholdService.disable(domainName, threshold, eType1, lithiumTokenUtil);
        return Response.<ThresholdDto>builder().data(thresholdService.mapDto(modelMapper, threshold)).status(OK).build();
    }
}
