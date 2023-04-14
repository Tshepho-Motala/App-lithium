package lithium.service.casino.provider.evolution.exception.handler;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.provider.evolution.exception.PropertyNotConfiguredException;
import lithium.service.casino.provider.evolution.exception.Status;
import lithium.service.casino.provider.evolution.exception.Status10002InvalidParameterException;
import lithium.service.casino.provider.evolution.exception.Status10003InvalidSIDException;
import lithium.service.casino.provider.evolution.exception.Status10003InvalidTokenIdException;
import lithium.service.casino.provider.evolution.exception.Status3004ClosedUserSession;
import lithium.service.casino.provider.evolution.model.response.StandardResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class EvolutionControllerAdvice {

    @ExceptionHandler(Status10003InvalidTokenIdException.class)
    public ResponseEntity<StandardResponse> handleInvalidToken(Status10003InvalidTokenIdException exception){
        return ResponseEntity.ok(
                StandardResponse
                        .builder()
                        .uuid(null)
                        .bonus(null)
                        .retransmission(null)
                        .balance(null)
                        .status(Status.INVALID_TOKEN_ID.name())
                        .build()
        );
    }

    @ExceptionHandler({Status10003InvalidSIDException.class, Status10002InvalidParameterException.class})
    public ResponseEntity<StandardResponse> handleInvalidSID(){
        return ResponseEntity.ok(
                StandardResponse
                        .builder()
                        .uuid(null)
                        .bonus(null)
                        .retransmission(null)
                        .balance(null)
                        .status(Status.INVALID_SID.name())
                        .build()
        );
    }

    @ExceptionHandler(Status500InternalServerErrorException.class)
    public ResponseEntity<StandardResponse> handleInternalServerError(){
        return ResponseEntity.ok(
                StandardResponse
                        .builder()
                        .uuid(null)
                        .bonus(null)
                        .retransmission(null)
                        .balance(null)
                        .status(Status.UNKNOWN_ERROR.name())
                        .build()
        );
    }

    @ExceptionHandler(Status3004ClosedUserSession.class)
    public StandardResponse handleCurrencyMismatchExceptions(Status3004ClosedUserSession e) {
        logException(e);
        return StandardResponse.builder()
                .status(Status.FATAL_ERROR_CLOSE_USER_SESSION.toString())
                .build();
    }

    @ExceptionHandler(PropertyNotConfiguredException.class)
    public StandardResponse handleCurrencyMismatchExceptions(PropertyNotConfiguredException e) {
        logException(e);
        return StandardResponse.builder()
                .status(Status.UNKNOWN_ERROR.toString())
                .build();
    }

    private static void logException(Exception e) {
        log.error("Exception is occurred, exception=" + e.getClass().getSimpleName(), e);
    }

}
