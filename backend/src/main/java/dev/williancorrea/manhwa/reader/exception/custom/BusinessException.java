package dev.williancorrea.manhwa.reader.exception.custom;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

  private final String messageKey;
  private final Object[] messageArgs;

  public BusinessException(String messageKey, Object[] messageArgs) {
    super(messageKey);
    this.messageKey = messageKey;
    this.messageArgs = messageArgs;
  }

  public BusinessException(String messageKey, Object[] messageArgs, Throwable cause) {
    super(messageKey, cause);
    this.messageKey = messageKey;
    this.messageArgs = messageArgs;
  }
}
