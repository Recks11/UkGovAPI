package dev.rexijie.ukgovapi.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SponsorNotFoundException extends RuntimeException {
    private final int code = 404;
    private final String message;

    public SponsorNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
