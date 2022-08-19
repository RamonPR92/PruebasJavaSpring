package com.ramon.app.exceptions;

public class DineroInsuficienteException extends Exception{
    public DineroInsuficienteException() {
        super();
    }

    public DineroInsuficienteException(String message) {
        super(message);
    }

    public DineroInsuficienteException(String message, Throwable cause) {
        super(message, cause);
    }
}
