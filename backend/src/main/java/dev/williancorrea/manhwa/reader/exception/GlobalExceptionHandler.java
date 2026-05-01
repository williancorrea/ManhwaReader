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

  public GlobalExceptionHandler(@Lazy MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @PostConstruct
  public void init() {
    log.info("Initializing Global Exception Handler");
  }

  private String getMessage(String messageCode, @Nullable Object[] args) {
    return messageSource.getMessage(messageCode, args, LocaleContextHolder.getLocale());
  }

  private String extractUri(WebRequest request) {
    return ((ServletWebRequest) request).getRequest().getRequestURI();
  }

  private String extractMethod(WebRequest request) {
    return ((ServletWebRequest) request).getRequest().getMethod();
  }

  private ResponseEntity<Object> singleItemResponse(Exception ex, HttpStatus status, HttpHeaders headers,
      WebRequest request, String messageCode, String message, String detail) {
    var items = new ArrayList<ApiError.ErrorItem>();
    items.add(new ApiError.ErrorItem(messageCode, message, detail));
    ApiError error = new ApiError(status, extractUri(request), extractMethod(request), items);
    return handleExceptionInternal(ex, error, headers, status, request);
  }

  public ApiError createErrorList(BindingResult bindingResult, HttpStatus httpStatus, String uri, String method) {
    ApiError error = new ApiError(httpStatus, uri, method, new ArrayList<>());
    for (FieldError fieldError : bindingResult.getFieldErrors()) {
      String message = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
      error.getItems().add(new ApiError.ErrorItem(
          fieldError.getCode() + "." + fieldError.getObjectName() + "." + fieldError.getField(),
          message,
          fieldError.toString()
      ));
    }
    return error;
  }

  @ExceptionHandler(AccessDeniedException.class)
  public void handleAccessDeniedException(AccessDeniedException ex) throws AccessDeniedException {
    throw ex;
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleUnknown(Exception ex, WebRequest request) {
    String messageCode = "exception.internal.error";
    String message = getMessage(messageCode, null);
    String detail = "Exception: " + ex.getMessage() +
        "\nCaused by: " + (ex.getCause() != null ? ex.getCause().getMessage() : "") +
        "\nStacktrace:\n" + Arrays.stream(ex.getStackTrace())
            .map(e -> e.getFileName() + ":" + e.getLineNumber() + " - " + e.getMethodName())
            .collect(Collectors.joining("\n"));
    log.error(ex.getMessage(), ex);
    return singleItemResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, new HttpHeaders(), request, messageCode, message, detail);
  }

  private ResponseEntity<Object> handleResourceNotFound(Exception ex, WebRequest request) {
    String uri = extractUri(request);
    String messageCode = "exception.resource.not-found";
    return singleItemResponse(ex, HttpStatus.BAD_REQUEST, new HttpHeaders(), request,
        messageCode, getMessage(messageCode, null),
        getMessage("exception.resource.not-found.detail", new Object[]{uri}));
  }

  @SuppressWarnings("NullableProblems")
  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
      HttpStatusCode status, WebRequest request) {
    return handleResourceNotFound(ex, request);
  }

  @SuppressWarnings("NullableProblems")
  @Override
  protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, HttpHeaders headers,
      HttpStatusCode status, WebRequest request) {
    return handleResourceNotFound(ex, request);
  }

  @ExceptionHandler({DataIntegrityViolationException.class})
  public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
      WebRequest request) {
    String messageCode = "exception.integrity-violation";
    log.error(ex.getMessage(), ex);
    return singleItemResponse(ex, HttpStatus.CONFLICT, new HttpHeaders(), request,
        messageCode, getMessage(messageCode, null), ExceptionUtils.getRootCauseMessage(ex));
  }

  @ExceptionHandler({TransactionSystemException.class})
  public ResponseEntity<Object> handleTransactionSystemException(TransactionSystemException ex, WebRequest request) {
    var items = new ArrayList<ApiError.ErrorItem>();
    if (ex.getRootCause() instanceof ConstraintViolationException constraintViolationException) {
      for (ConstraintViolation<?> violation : constraintViolationException.getConstraintViolations()) {
        items.add(new ApiError.ErrorItem(
            violation.getMessageTemplate().replace("{", "").replace("}", ""),
            violation.getMessage().replace("{0}", violation.getPropertyPath().toString()),
            ""
        ));
      }
    } else {
      log.error("NOT IMPLEMENTED --> {0}", ex.getCause());
      throw new BusinessException("exception.not-implemented", new Object[]{ex.getCause()});
    }
    ApiError error = new ApiError(HttpStatus.BAD_REQUEST, extractUri(request), extractMethod(request), items);
    log.error(ex.getMessage(), ex);
    return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex,
      WebRequest request) {
    var items = ex.getConstraintViolations()
        .stream()
        .map(m -> new ApiError.ErrorItem(
            "constraint.violation",
            m.getMessage().replace("{0}", m.getPropertyPath().toString()),
            ""))
        .toList();
    ApiError error = new ApiError(HttpStatus.BAD_REQUEST, extractUri(request), extractMethod(request), new ArrayList<>(items));
    log.error(ex.getMessage(), ex);
    return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @SuppressWarnings("NullableProblems")
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    String detail;
    String message;
    String messageCode;
    Throwable rootCause = ExceptionUtils.getRootCause(ex);

    switch (rootCause) {
      case InvalidFormatException ife -> {
        String path = ife.getPath().stream()
            .map(JsonMappingException.Reference::getFieldName).collect(Collectors.joining("."));
        messageCode = "message.invalid-format";
        message = getMessage(messageCode, null);
        messageCode += "." + path;
        var formats = ife.getTargetType().getTypeName();
        if (null != ife.getTargetType().getEnumConstants()) {
          formats = Arrays.toString(ife.getTargetType().getEnumConstants());
        }
        detail = getMessage("exception.invalid-format.detail-1",
            new Object[]{path, ife.getValue(), ife.getTargetType().getSimpleName(), formats});
      }
      case UnrecognizedPropertyException upe -> {
        messageCode = "message.property.not-found";
        message = getMessage(messageCode, null);
        detail = getMessage("exception.property.not-found.detail", new Object[]{upe.getPropertyName()});
      }
      case IgnoredPropertyException ipe -> {
        messageCode = "message.property.not-found";
        message = getMessage(messageCode, null);
        detail = getMessage("exception.property.not-found.detail", new Object[]{ipe.getPropertyName()});
      }
      case JsonParseException jpe -> {
        messageCode = "message.invalid-format";
        message = getMessage(messageCode, null);
        String campo = jpe.getProcessor().getParsingContext().getCurrentName();
        if (StringUtils.isNotBlank(campo)) {
          detail = getMessage("exception.invalid-format.detail-2",
              new Object[]{campo, rootCause.getMessage().substring(20).split("'")[0]});
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

    return singleItemResponse(ex, HttpStatus.BAD_REQUEST, headers, request, messageCode, message, detail);
  }

  @SuppressWarnings("NullableProblems")
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    ApiError apiError = createErrorList(ex.getBindingResult(), HttpStatus.BAD_REQUEST, extractUri(request), extractMethod(request));
    return handleExceptionInternal(ex, apiError, headers, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler({EmptyResultDataAccessException.class})
  public ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex,
      WebRequest request) {
    String message = getMessage("exception.not-found", null);
    log.error(ex.getMessage(), ex);
    return singleItemResponse(ex, HttpStatus.NOT_FOUND, new HttpHeaders(), request, message, message, ex.toString());
  }

  @ExceptionHandler({BindException.class})
  protected ResponseEntity<Object> handleBootBindException(BindException ex, WebRequest request) {
    String message = getMessage("exception.method-not-supported", null);
    return singleItemResponse(ex, HttpStatus.METHOD_NOT_ALLOWED, new HttpHeaders(), request, message, message, ex.toString());
  }

  @ExceptionHandler({MethodArgumentTypeMismatchException.class})
  public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex,
      WebRequest request) {
    String messageCode = "exception.attribute-type.invalid";
    String detail = getMessage("exception.attribute-type.invalid-detail",
        new Object[]{ex.getName(), ex.getValue(), Objects.requireNonNull(ex.getRequiredType()).getSimpleName()});
    log.error(ex.getMessage(), ex);
    return singleItemResponse(ex, HttpStatus.BAD_REQUEST, new HttpHeaders(), request,
        messageCode, getMessage(messageCode, null), detail);
  }

  @SuppressWarnings("NullableProblems")
  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    String messageCode = "exception.parameter.missing";
    String detail = getMessage("exception.parameter.missing.detail",
        new Object[]{ex.getParameterName(), ex.getParameterType()});
    log.error(ex.getMessage(), ex);
    return singleItemResponse(ex, HttpStatus.BAD_REQUEST, new HttpHeaders(), request,
        messageCode, getMessage(messageCode, new Object[]{ex.getParameterName()}), detail);
  }

  @ExceptionHandler({InvalidDataAccessApiUsageException.class})
  public ResponseEntity<Object> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException ex,
      WebRequest request) {
    String messageCode = "exception.object-format-incorrect";
    log.error(ex.getMessage(), ex);
    return singleItemResponse(ex, HttpStatus.BAD_REQUEST, new HttpHeaders(), request,
        messageCode, getMessage(messageCode, null), ExceptionUtils.getRootCauseMessage(ex));
  }

  @SuppressWarnings("NullableProblems")
  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    String messageCode = "exception.method-not-supported";
    return singleItemResponse(ex, HttpStatus.METHOD_NOT_ALLOWED, new HttpHeaders(), request,
        messageCode, getMessage(messageCode, null), ex.toString());
  }

  @ExceptionHandler({BusinessException.class})
  public ResponseEntity<Object> handlerBusinessException(BusinessException ex, WebRequest request) {
    String messageCode = ex.getMessageKey();
    return singleItemResponse(ex, HttpStatus.BAD_REQUEST, new HttpHeaders(), request,
        messageCode, getMessage(messageCode, ex.getMessageArgs()), null);
  }

  @ExceptionHandler({NotFoundException.class})
  public ResponseEntity<Object> handlerBusinessNotFoundException(NotFoundException ex, WebRequest request) {
    String messageCode = ex.getMessageKey();
    return singleItemResponse(ex, HttpStatus.NOT_FOUND, new HttpHeaders(), request,
        messageCode, getMessage(messageCode, ex.getMessageArgs()), null);
  }

  @ExceptionHandler({AuthenticationException.class})
  public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
    String messageCode = "auth.error.invalid-credentials";
    return singleItemResponse(ex, HttpStatus.UNAUTHORIZED, new HttpHeaders(), request,
        messageCode, getMessage(messageCode, null), null);
  }

  @ExceptionHandler({ConflictException.class})
  public ResponseEntity<Object> handlerConflictException(ConflictException ex, WebRequest request) {
    String messageCode = ex.getMessageKey();
    return singleItemResponse(ex, HttpStatus.CONFLICT, new HttpHeaders(), request,
        messageCode, getMessage(messageCode, ex.getMessageArgs()), null);
  }
}
