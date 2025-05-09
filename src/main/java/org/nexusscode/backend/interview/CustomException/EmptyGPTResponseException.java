package org.nexusscode.backend.interview.CustomException;

public class EmptyGPTResponseException extends RuntimeException {
    public EmptyGPTResponseException(String message) {
        super(message);
    }
}
