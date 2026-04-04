package dev.williancorrea.manhwa.reader.exception.custom;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

  private final String messageKey;
  private final Object[] messageArgs;

  public NotFoundException(String messageKey, Object[] messageArgs) {
    super(messageKey);
    this.messageKey = messageKey;
    this.messageArgs = messageArgs;
  }
}
