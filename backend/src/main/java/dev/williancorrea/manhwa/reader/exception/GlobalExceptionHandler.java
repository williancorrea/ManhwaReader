package dev.williancorrea.manhwa.reader.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.IgnoredPropertyException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import dev.williancorrea.manhwa.reader.exception.custom.BusinessException;
import dev.williancorrea.manhwa.reader.exception.custom.ConflictException;
import dev.williancorrea.manhwa.reader.exception.custom.NotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private final MessageSource messageSource;

  @PostConstruct
  public void init() {
    log.info("Initializing Global Exception Handler");
  }

  public GlobalExceptionHandler(@Lazy MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  private String getMessage(String messageCode, @Nullable Object[] args) {
    return messageSource.getMessage(messageCode, args, LocaleContextHolder.getLocale());
  }

  /**
   * Create error list
   *
   * @param bindingResult {@link BindingResult}
   * @param httpStatus    {@link HttpStatus}
   * @param uri           {@link String}
   * @param method        {@link String}
   * @return List<ApiError>
   */
  public ApiError createErrorList(BindingResult bindingResult, HttpStatus httpStatus, String uri, String method) {
    ApiError error = new ApiError(httpStatus, uri, method, new ArrayList<>());
    for (FieldError fieldError : bindingResult.getFieldErrors()) {
      String message = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
      String detail = fieldError.toString();
      error.getItems().add(new ApiError.ErrorItem(
          fieldError.getCode() + "." + fieldError.getObjectName() + "." + fieldError.getField(),
          message,
          detail
      ));
    }
    return error;
  }

  /**
   * Exception handler for unknown exceptions.
   *
   * @param ex      The exception that occurred.
   * @param request The request that triggered the exception.
   * @return A ResponseEntity containing an ApiError object with the appropriate error details.
   */
  /**
   * Re-throw AccessDeniedException so Spring Security's ExceptionTranslationFilter
   * can handle it properly (returning 401 for unauthenticated or 403 for unauthorized).
   */
  @ExceptionHandler(AccessDeniedException.class)
  public void handleAccessDeniedException(AccessDeniedException ex) throws AccessDeniedException {
    throw ex;
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleUnknown(Exception ex, WebRequest request) {
    String messageCode = "exception.internal.error";
    String message = messageSource.getMessage(messageCode, null, LocaleContextHolder.getLocale());

    String detail =
        "Exception: " + ex.getMessage() +
            "\nCaused by: " + (ex.getCause() != null ? ex.getCause().getMessage() : "") +
            "\nStacktrace:\n" + Arrays
            .stream(ex.getStackTrace())
            .map(e -> e.getFileName() + ":" + e.getLineNumber() + " - " + e.getMethodName())
            .collect(Collectors.joining("\n")
            );

    var items = new ArrayList<ApiError.ErrorItem>();
    items.add(new ApiError.ErrorItem(messageCode, message, detail));

    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    String method = ((ServletWebRequest) request).getRequest().getMethod();
    ApiError errors = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, uri, method, items);

    log.error(ex.getMessage(), ex);
    return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  /**
   * Resource not found
   *
   * @param ex      the exception {@link NoHandlerFoundException}
   * @param headers the headers to be written to the response {@link HttpHeaders}
   * @param status  the selected response status {@link HttpStatus}
   * @param request the current request {@link WebRequest}
   * @return ResponseEntity<Object>
   */
  @SuppressWarnings({"NullableProblems", "DuplicatedCode"})
  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex,
                                                                 HttpHeaders headers,
                                                                 HttpStatusCode status,
                                                                 WebRequest request) {
    String messageCode = "exception.resource.not-found";
    String message = getMessage(messageCode, null);
    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    String detail = getMessage("exception.resource.not-found.detail", new Object[] {uri});

    var items = new ArrayList<ApiError.ErrorItem>();
    items.add(new ApiError.ErrorItem(messageCode, message, detail));

    String method = ((ServletWebRequest) request).getRequest().getMethod();
    ApiError error = new ApiError(HttpStatus.BAD_REQUEST, uri, method, items);
    return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @SuppressWarnings({"NullableProblems", "DuplicatedCode"})
  @Override
  protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
    String messageCode = "exception.resource.not-found";
    String message = getMessage(messageCode, null);
    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    String detail = getMessage("exception.resource.not-found.detail", new Object[] {uri});

    var items = new ArrayList<ApiError.ErrorItem>();
    items.add(new ApiError.ErrorItem(messageCode, message, detail));

    String method = ((ServletWebRequest) request).getRequest().getMethod();
    ApiError error = new ApiError(HttpStatus.BAD_REQUEST, uri, method, items);
    return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  /**
   * Database integrity violation - relationship between tables
   *
   * @param ex      {@link DataIntegrityViolationException}
   * @param request {@link WebRequest}
   * @return ResponseEntity<Object>
   */
  @SuppressWarnings({"DuplicatedCode"})
  @ExceptionHandler({DataIntegrityViolationException.class})
  public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
                                                                      WebRequest request) {
    String messageCode = "exception.integrity-violation";
    String message = getMessage(messageCode, null);
    String detail = ExceptionUtils.getRootCauseMessage(ex);

    var items = new ArrayList<ApiError.ErrorItem>();
    items.add(new ApiError.ErrorItem(messageCode, message, detail));

    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    String method = ((ServletWebRequest) request).getRequest().getMethod();
    ApiError error = new ApiError(HttpStatus.CONFLICT, uri, method, items);

    log.error(ex.getMessage(), ex);
    return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.CONFLICT, request);
  }

  @SuppressWarnings({"DuplicatedCode"})
  @ExceptionHandler({TransactionSystemException.class})
  public ResponseEntity<Object> handleTransactionSystemException(TransactionSystemException ex, WebRequest request) {
    var items = new ArrayList<ApiError.ErrorItem>();
    if (ex.getRootCause() instanceof ConstraintViolationException constraintViolationException) {
      for (ConstraintViolation<?> violation : constraintViolationException.getConstraintViolations()) {
        items.add(new ApiError.ErrorItem(
                violation.getMessageTemplate()
                    .replace("{", "")
                    .replace("}", ""),
                violation.getMessage().replace("{0}", violation.getPropertyPath().toString()),
                ""
            )
        );
      }
    } else {
      log.error("NOT IMPLEMENTED --> {0}", ex.getCause());
      throw new BusinessException("exception.not-implemented", new Object[]{ex.getCause()});
    }

    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    String method = ((ServletWebRequest) request).getRequest().getMethod();
    ApiError error = new ApiError(HttpStatus.BAD_REQUEST, uri, method, items);

    log.error(ex.getMessage(), ex);
    return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  /**
   * constrain Violation
   *
   * @param ex      {@link ConstraintViolationException}
   * @param request {@link WebRequest}
   * @return ResponseEntity<Object>
   */
  @SuppressWarnings({"DuplicatedCode"})
  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex,
                                                                   WebRequest request) {
    var items = ex.getConstraintViolations()
        .stream()
        .map(m -> new ApiError.ErrorItem(
            "constraint.violation",
            m.getMessage().replace("{0}", m.getPropertyPath().toString()),
            "")
        )
        .toList();

    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    String method = ((ServletWebRequest) request).getRequest().getMethod();
    ApiError error = new ApiError(HttpStatus.BAD_REQUEST, uri, method, items);

    log.error(ex.getMessage(), ex);
    return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  /**
   * Handles unreadable error messages
   *
   * @param ex      the exception {@link HttpMessageNotReadableException}
   * @param headers the headers to be written to the response {@link HttpHeaders}
   * @param status  the selected response status {@link HttpStatus}
   * @param request the current request {@link WebRequest}
   * @return ResponseEntity<Object>
   */
  @SuppressWarnings("NullableProblems")
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                HttpHeaders headers,
                                                                HttpStatusCode status,
                                                                WebRequest request) {

    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    String method = ((ServletWebRequest) request).getRequest().getMethod();

    String detail;
    String message;
    String messageCode;
    Throwable rootCause = ExceptionUtils.getRootCause(ex);

    switch (rootCause) {
      case InvalidFormatException invalidFormatException -> {
        String path = invalidFormatException.getPath().stream()
            .map(JsonMappingException.Reference::getFieldName).collect(Collectors.joining("."));
        messageCode = "message.invalid-format";
        message = getMessage(messageCode, null);
        messageCode += "." + path;

        var formats = invalidFormatException.getTargetType().getTypeName();
        if (null != invalidFormatException.getTargetType().getEnumConstants()) {
          formats = Arrays.toString(invalidFormatException.getTargetType().getEnumConstants());
        }
        detail = getMessage(
            "exception.invalid-format.detail-1",
            new Object[] {
                path,
                invalidFormatException.getValue(),
                invalidFormatException.getTargetType().getSimpleName(),
                formats
            }
        );
      }
      case UnrecognizedPropertyException unrecognizedpropertyexception -> {
        messageCode = "message.property.not-found";
        message = getMessage(messageCode, null);
        detail = getMessage("exception.property.not-found.detail",
            new Object[] {unrecognizedpropertyexception.getPropertyName()});
      }
      case IgnoredPropertyException ignoredPropertyException -> {
        messageCode = "message.property.not-found";
        message = getMessage(messageCode, null);

        detail = getMessage("exception.property.not-found.detail",
            new Object[] {ignoredPropertyException.getPropertyName()});
      }
      case JsonParseException jsonParseException -> {
        messageCode = "message.invalid-format";
        message = getMessage(messageCode, null);
        String campo = jsonParseException.getProcessor().getParsingContext().getCurrentName();
        if (StringUtils.isNotBlank(campo)) {
          detail = getMessage("exception.invalid-format.detail-2",
              new Object[] {campo, rootCause.getMessage().substring(20).split("'")[0]});
        } else {
          detail = getMessage("exception.invalid-request.detail", null);
        }
      }
      case null, default -> {
        messageCode = "message.invalid-request.detail";
        message = getMessage(messageCode, null);
        detail = ex.getCause() != null ? ex.getCause().toString() : ex.toString();
      }
    }

    var items = new ArrayList<ApiError.ErrorItem>();
    items.add(new ApiError.ErrorItem(messageCode, message, detail));

    ApiError error = new ApiError(HttpStatus.BAD_REQUEST, uri, method, items);
    return handleExceptionInternal(ex, error, headers, HttpStatus.BAD_REQUEST, request);
  }

  /**
   * Handles validation error messages for object attributes
   *
   * @param ex      the exception {@link MethodArgumentNotValidException}
   * @param headers the headers to be written to the response {@link HttpHeaders}
   * @param status  the selected response status {@link HttpStatus}
   * @param request the current request {@link WebRequest}
   * @return ResponseEntity<Object>
   */
  @SuppressWarnings("NullableProblems")
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                HttpHeaders headers,
                                                                HttpStatusCode status,
                                                                WebRequest request) {
    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    String method = ((ServletWebRequest) request).getRequest().getMethod();
    ApiError apiError = createErrorList(ex.getBindingResult(), HttpStatus.BAD_REQUEST, uri, method);
    return handleExceptionInternal(ex, apiError, headers, HttpStatus.BAD_REQUEST, request);
  }

  /**
   * Handles error messages when resource is not found
   *
   * @param ex      {@link EmptyResultDataAccessException}
   * @param request {@link WebRequest}
   * @return ResponseEntity<Object>
   */
  @SuppressWarnings({"DuplicatedCode"})
  @ExceptionHandler({EmptyResultDataAccessException.class})
  public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex,
                                                                     WebRequest request) {
    String messageCode = "exception.not-found";
    String message = getMessage(messageCode, null);

    var items = new ArrayList<ApiError.ErrorItem>();
    items.add(new ApiError.ErrorItem(message, message, ex.toString()));

    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    String method = ((ServletWebRequest) request).getRequest().getMethod();
    ApiError errors = new ApiError(HttpStatus.NOT_FOUND, uri, method, items);

    log.error(ex.getMessage(), ex);
    return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
  }

  /**
   * It handles parameters entered incorrectly
   *
   * @param ex      the exception {@link BindException}
   * @param ignoredHeaders the headers to be written to the response {@link HttpHeaders}
   * @param ignoredStatus  the selected response status {@link HttpStatus}
   * @param request the current request {@link WebRequest}
   * @return ResponseEntity<Object>
   */
  @SuppressWarnings({"DuplicatedCode"})
  @ExceptionHandler({BindException.class})
  protected ResponseEntity<Object> handleBindException(BindException ex,
                                                       HttpHeaders ignoredHeaders,
                                                       HttpStatus ignoredStatus,
                                                       WebRequest request) {
    String messageCode = "exception.method-not-supported";
    String message = getMessage(messageCode, null);

    var items = new ArrayList<ApiError.ErrorItem>();
    items.add(new ApiError.ErrorItem(message, message, ex.toString()));

    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    String method = ((ServletWebRequest) request).getRequest().getMethod();
    ApiError errors = new ApiError(HttpStatus.METHOD_NOT_ALLOWED, uri, method, items);

    return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED, request);
  }

  /**
   * Handles type conversion error messages
   *
   * @param ex      {@link MethodArgumentTypeMismatchException}
   * @param request {@link WebRequest}
   * @return ResponseEntity<Object>
   */
  @SuppressWarnings({"DuplicatedCode"})
  @ExceptionHandler({MethodArgumentTypeMismatchException.class})
  public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex,
                                                                          WebRequest request) {
    String messageCode = "exception.attribute-type.invalid";
    String message = getMessage(messageCode, null);
    String detail = getMessage("exception.attribute-type.invalid-detail",
        new Object[] {
            ex.getName(),
            ex.getValue(),
            Objects.requireNonNull(ex.getRequiredType()).getSimpleName()
        });

    var items = new ArrayList<ApiError.ErrorItem>();
    items.add(new ApiError.ErrorItem(messageCode, message, detail));

    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    String method = ((ServletWebRequest) request).getRequest().getMethod();
    ApiError error = new ApiError(HttpStatus.BAD_REQUEST, uri, method, items);

    log.error(ex.getMessage(), ex);
    return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @SuppressWarnings({"NullableProblems", "DuplicatedCode"})
  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                        HttpHeaders headers, HttpStatusCode status,
                                                                        WebRequest request) {

    String messageCode = "exception.parameter.missing";

    String message = getMessage(messageCode, new Object[] {ex.getParameterName()});
    String detail = getMessage("exception.parameter.missing.detail",
        new Object[] {
            ex.getParameterName(),
            ex.getParameterType()
        });

    var items = new ArrayList<ApiError.ErrorItem>();
    items.add(new ApiError.ErrorItem(messageCode, message, detail));

    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    String method = ((ServletWebRequest) request).getRequest().getMethod();
    ApiError error = new ApiError(HttpStatus.BAD_REQUEST, uri, method, items);

    log.error(ex.getMessage(), ex);
    return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  /**
   * Error handling object
   *
   * @param ex      {@link InvalidDataAccessApiUsageException}
   * @param request {@link WebRequest}
   * @return ResponseEntity<Object>
   */
  @SuppressWarnings({"DuplicatedCode"})
  @ExceptionHandler({InvalidDataAccessApiUsageException.class})
  public ResponseEntity<Object> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException ex,
                                                                         WebRequest request) {
    String messageCode = "exception.object-format-incorrect";
    String message = getMessage(messageCode, null);
    String detail = ExceptionUtils.getRootCauseMessage(ex);

    var items = new ArrayList<ApiError.ErrorItem>();
    items.add(new ApiError.ErrorItem(messageCode, message, detail));

    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    String method = ((ServletWebRequest) request).getRequest().getMethod();
    ApiError error = new ApiError(HttpStatus.BAD_REQUEST, uri, method, items);

    log.error(ex.getMessage(), ex);
    return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }


  /**
   * Unsupported method
   *
   * @param ex      the exception {@link HttpRequestMethodNotSupportedException}
   * @param headers the headers to be written to the response {@link HttpHeaders}
   * @param status  the selected response status {@link HttpStatus}
   * @param request the current request {@link WebRequest}
   * @return ResponseEntity<Object>
   */
  @SuppressWarnings("NullableProblems")
  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                       HttpHeaders headers,
                                                                       HttpStatusCode status,
                                                                       WebRequest request) {
    String messageCode = "exception.method-not-supported";
    String message = getMessage(messageCode, null);
    String detail = ex.toString();

    var items = new ArrayList<ApiError.ErrorItem>();
    items.add(new ApiError.ErrorItem(messageCode, message, detail));

    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    String method = ((ServletWebRequest) request).getRequest().getMethod();
    ApiError error = new ApiError(HttpStatus.METHOD_NOT_ALLOWED, uri, method, items);
    return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED,
        request);
  }

  /**
   * Application business rule
   *
   * @param ex      {@link BusinessException}
   * @param request {@link WebRequest}
   * @return ResponseEntity<Object>
   */
  @SuppressWarnings({"DuplicatedCode"})
  @ExceptionHandler({BusinessException.class})
  public ResponseEntity<Object> handlerBusinessException(BusinessException ex, WebRequest request) {

    String messageCode = ex.getMessageKey();
    String message = getMessage(messageCode, ex.getMessageArgs());

    var items = new ArrayList<ApiError.ErrorItem>();
    items.add(new ApiError.ErrorItem(messageCode, message, null));

    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    String method = ((ServletWebRequest) request).getRequest().getMethod();
    ApiError errors = new ApiError(HttpStatus.BAD_REQUEST, uri, method, items);
    return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }


  /**
   * Exception handler for NotFoundException.
   *
   * @param ex      The NotFoundException that occurred.
   * @param request The request that triggered the exception.
   * @return A ResponseEntity containing an ApiError object with the appropriate error details.
   */
  @SuppressWarnings({"DuplicatedCode"})
  @ExceptionHandler({NotFoundException.class})
  public ResponseEntity<Object> handlerBusinessNotFoundException(NotFoundException ex, WebRequest request) {
    String messageCode = ex.getMessageKey();
    String message = getMessage(messageCode, ex.getMessageArgs());

    var items = new ArrayList<ApiError.ErrorItem>();
    items.add(new ApiError.ErrorItem(messageCode, message, null));

    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    String method = ((ServletWebRequest) request).getRequest().getMethod();
    ApiError errors = new ApiError(HttpStatus.NOT_FOUND, uri, method, items);
    return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler({AuthenticationException.class})
  public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
    String messageCode = "auth.error.invalid-credentials";
    String message = getMessage(messageCode, null);

    var items = new ArrayList<ApiError.ErrorItem>();
    items.add(new ApiError.ErrorItem(messageCode, message, null));

    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    String method = ((ServletWebRequest) request).getRequest().getMethod();
    ApiError errors = new ApiError(HttpStatus.UNAUTHORIZED, uri, method, items);
    return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
  }

  @SuppressWarnings({"DuplicatedCode"})
  @ExceptionHandler({ConflictException.class})
  public ResponseEntity<Object> handlerConflictException(ConflictException ex, WebRequest request) {
    String messageCode = ex.getMessageKey();
    String message = getMessage(messageCode, ex.getMessageArgs());

    var items = new ArrayList<ApiError.ErrorItem>();
    items.add(new ApiError.ErrorItem(messageCode, message, null));

    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    String method = ((ServletWebRequest) request).getRequest().getMethod();
    ApiError errors = new ApiError(HttpStatus.CONFLICT, uri, method, items);
    return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.CONFLICT, request);
  }
}
