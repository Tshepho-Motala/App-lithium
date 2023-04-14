package lithium.service.casino.provider.evolution.exception;

public enum Status {
    OK,
    TEMPORARY_ERROR,
    INVALID_TOKEN_ID,
    INVALID_SID,
    ACCOUNT_LOCKED,
    FATAL_ERROR_CLOSE_USER_SESSION,
    UNKNOWN_ERROR,
    INVALID_PARAMETER,
    BET_DOES_NOT_EXIST
}
