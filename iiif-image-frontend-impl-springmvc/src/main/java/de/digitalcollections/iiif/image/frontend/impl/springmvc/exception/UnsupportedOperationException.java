package de.digitalcollections.iiif.image.frontend.impl.springmvc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class UnsupportedOperationException extends Exception {
    public UnsupportedOperationException(String message) {
        super(message);
    }
}