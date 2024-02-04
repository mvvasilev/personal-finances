package dev.mvvasilev.common.exceptions;

public class InvalidUserIdException extends CommonFinancesException {
    public InvalidUserIdException() {
        super("UserId is invalid");
    }
}
