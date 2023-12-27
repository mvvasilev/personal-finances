package dev.mvvasilev.common.exceptions;

public class CommonFinancesException extends RuntimeException {

    public CommonFinancesException(String message) {
        super(message);
    }

    public CommonFinancesException(String messageTemplate, Object... replacements) {
        super(String.format(messageTemplate, replacements));
    }
}
