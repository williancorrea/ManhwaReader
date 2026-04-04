package dev.williancorrea.manhwa.reader.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.OffsetDateTime;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;

/**
 * Represents an API error response with detailed error information including origin,
 * method, HTTP status, timestamp, and a list of error details.
 * This class is designed to encapsulate all relevant information needed 
 * for communicating error responses in a standardized format.
 */
@Getter
@Validated
public class ApiError {
  private final String origin;
  private final String method;
  private final Status status;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
  private final OffsetDateTime dateTime;
  private final List<ErrorItem> items;

  public ApiError(HttpStatus httpStatus, String origin, String method, List<ErrorItem> errors) {
    this.origin = origin;
    this.method = method;
    this.status = new Status(httpStatus.value(), httpStatus.getReasonPhrase());
    this.dateTime = OffsetDateTime.now();
    this.items = errors;
  }

  /**
   * Represents the status information associated with an API error.
   * This class provides a structured representation of a status 
   * that includes a code and a description.
   */
  @Getter
  public static class Status {
    private final int code;
    private final String description;

    public Status(Integer code, String description) {
      this.code = code;
      this.description = description;
    }
  }

  /**
   * Represents a detailed error item that is part of an API error response.
   * This record is used to encapsulate specific error details, including 
   * a key, a message, and optional additional information.
   * <p>
   * The key identifies the specific error instance or error type.
   * The message provides a user-readable description of the error.
   * The detail offers optional additional context or explanation about the error.
   */
  public record ErrorItem(@JsonInclude(JsonInclude.Include.NON_EMPTY) String key,
                          @NotBlank String message,
                          @JsonInclude(JsonInclude.Include.NON_EMPTY) String detail) {
    public ErrorItem(String key, String message, String detail) {
      this.key = key == null ? "" : key;
      this.message = message;
      this.detail = detail == null ? "" : detail;
    }
  }
}
