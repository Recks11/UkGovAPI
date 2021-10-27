package dev.rexijie.ukgovapi.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends RuntimeException {
    private final int code = 400;
    private final String message;

    public InvalidRequestException(String message) {
        super(message);
        this.message = message;
    }

    public int getCode() {
        return code;
    }
}
