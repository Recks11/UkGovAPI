package dev.rexijie.ukgovapi.errors;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CustomErrorAttributes implements ErrorAttributes {
    private ErrorAttributes delegate;

    public CustomErrorAttributes(ErrorAttributes delegate) {
        this.delegate = delegate;
    }


    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        var error = getError(request);

        errorAttributes.put("status", determineHttpStatus(error));
        errorAttributes.put("path", request.path());

        if (error instanceof SponsorNotFoundException sponsorError) {
            errorAttributes.put("reason", sponsorError.getMessage());
        }

        if (error instanceof ResponseStatusException) {
            errorAttributes.put("message", "the requested resource is not found");
        }

        return errorAttributes;
    }

    @Override
    public Throwable getError(ServerRequest request) {
        return delegate.getError(request);
    }

    @Override
    public void storeErrorInformation(Throwable error, ServerWebExchange exchange) {
        delegate.storeErrorInformation(error, exchange);
    }

    public void setDelegate(ErrorAttributes delegate) {
        this.delegate = delegate;
    }

    private int determineHttpStatus(Throwable error) {
        if (error instanceof ResponseStatusException rse) return rse.getStatus().value();
        Optional<HttpStatus> responseStatusAnnotation  = getResponseStatusAnnotation(error);
        if (responseStatusAnnotation.isPresent()) return responseStatusAnnotation.get().value();
        if (error instanceof SponsorNotFoundException) return 404;
        return 500;
    }

    private Optional<HttpStatus> getResponseStatusAnnotation(Throwable throwable) {
        MergedAnnotation<ResponseStatus> responseStatusMergedAnnotation = MergedAnnotations
                .from(throwable.getClass(), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(ResponseStatus.class);
        return responseStatusMergedAnnotation.getValue("code", HttpStatus.class);
    }


}
