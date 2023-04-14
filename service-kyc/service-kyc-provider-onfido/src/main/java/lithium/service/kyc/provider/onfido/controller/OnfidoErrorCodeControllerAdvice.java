package lithium.service.kyc.provider.onfido.controller;

import lithium.exceptions.CustomHttpErrorCodeResponse;
import lithium.exceptions.ErrorCodeException;
import lithium.service.kyc.provider.onfido.exceptions.CodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
@Slf4j
public class OnfidoErrorCodeControllerAdvice {

    @ExceptionHandler(CodeException.class)
    public CustomHttpErrorCodeResponse handleErrorCodeException(CodeException e, HttpServletResponse response) {
        response.setStatus(e.getCode());
        return CustomHttpErrorCodeResponse.from(new ErrorCodeException(e.getCode(), e.getMessage(), e.getClass().getCanonicalName()));
    }
}
