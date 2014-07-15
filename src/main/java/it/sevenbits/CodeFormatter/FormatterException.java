package it.sevenbits.CodeFormatter;

/**
 * Raises when there is some logical problems in formatter
 */

public class FormatterException extends Exception {
    FormatterException(){}
    FormatterException(final String message) { super(message); }
    FormatterException(final String message, final  Throwable cause) {
        super(message, cause);
        }
    FormatterException(final Exception ex) {
        super(ex.getMessage(), ex.getCause());
    }
}
