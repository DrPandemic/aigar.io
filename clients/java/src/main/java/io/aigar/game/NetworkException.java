package io.aigar.game;

public class NetworkException extends Exception {
    public NetworkException(int code, String message) {
        super(String.format("Received %d: %s", code, message));
    }

    public NetworkException(Throwable t) {
        super(t);
    }
}
