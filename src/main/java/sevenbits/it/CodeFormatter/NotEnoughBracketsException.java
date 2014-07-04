package sevenbits.it.CodeFormatter;

/**
 * Raises when there is brackets mismatch
 */

public class NotEnoughBracketsException extends FormatterException {
    NotEnoughBracketsException(){};
    NotEnoughBracketsException(String message, Throwable cause){
        super(message, cause);
    }
}
