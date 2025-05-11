package com.caesar.exception;

public class CaesarScheduleConfigSyncException extends Exception{

    private static final long serialVersionUID = 1L;

    public CaesarScheduleConfigSyncException() {
        super();
    }


    public CaesarScheduleConfigSyncException(String message) {
        super(message);
    }


    public CaesarScheduleConfigSyncException(String message, Throwable cause) {
        super(message, cause);
    }

}
