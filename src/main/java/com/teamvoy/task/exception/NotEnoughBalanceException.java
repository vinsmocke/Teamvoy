package com.teamvoy.task.exception;

public class NotEnoughBalanceException extends RuntimeException {
    public NotEnoughBalanceException() {
        super();
    }

    public NotEnoughBalanceException(String message) {
        super(message);
    }

    public NotEnoughBalanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughBalanceException(Throwable cause) {
        super(cause);
    }
}
