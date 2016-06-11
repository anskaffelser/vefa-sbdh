package no.difi.vefa.sbdh.lang;

public class SbdhException extends Exception {
    public SbdhException(String message, Throwable cause) {
        super(message, cause);
    }

    public SbdhException(String message) {
        super(message);
    }
}
