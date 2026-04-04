package dev.williancorrea.manhwa.reader.exception.custom;

import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {

    private final String messageKey;
    private final Object[] messageArgs;

    public ConflictException(String messageKey, Object[] messageArgs) {
        super(messageKey);
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
    }
}
