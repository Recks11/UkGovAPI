package dev.rexijie.ukgovapi.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvalidRequestException extends RuntimeException {
    private final int code = 403;
    private String message;

    public InvalidRequestException(String message) {
        super(message);
        this.message = message;
    }
}
