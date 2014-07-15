package it.sevenbits.codeformatter;

/**
 * Raises when there is brackets mismatch
 */

class NotEnoughBracketsException extends FormatterException {
    NotEnoughBracketsException(){}
    NotEnoughBracketsException(final String message) { super(message); }
    NotEnoughBracketsException(final String message, final Throwable cause) {
        super(message, cause);
    }
}