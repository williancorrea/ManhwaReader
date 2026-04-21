package dev.williancorrea.manhwa.reader.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.IgnoredPropertyException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import dev.williancorrea.manhwa.reader.exception.custom.BusinessException;
import dev.williancorrea.manhwa.reader.exception.custom.ConflictException;
import dev.williancorrea.manhwa.reader.exception.custom.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

  @Mock
  private MessageSource messageSource;

  @Mock
  private ServletWebRequest webRequest;

  @Mock
  private HttpServletRequest httpServletRequest;

  private GlobalExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler(messageSource);
    // lenient stub - used by most tests but not all
    Mockito.lenient().when(webRequest.getRequest()).thenAnswer(invocation -> httpServletRequest);
  }

  @Nested
  class Init {

    @Test
    void shouldInitializeHandler() {
      handler.init();
      // Just verify the method completes without exception
      assertThat(handler).isNotNull();
    }
  }

  @Nested
  class HandleAccessDeniedException {

    @Test
    void shouldRethrowAccessDeniedException() {
      AccessDeniedException exception = new AccessDeniedException("Access denied");

      assertThatThrownBy(() -> handler.handleAccessDeniedException(exception))
          .isInstanceOf(AccessDeniedException.class)
          .hasMessage("Access denied");
    }
  }

  @Nested
  class HandleUnknown {

    @Test
    void shouldHandleUnknownException() {
      Exception exception = new Exception("Unknown error");
      when(httpServletRequest.getRequestURI()).thenReturn("/api/test");
      when(httpServletRequest.getMethod()).thenReturn("GET");
      when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
          .thenReturn("Internal error message");

      ResponseEntity<Object> response = handler.handleUnknown(exception, webRequest);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
      assertThat(response.getBody()).isInstanceOf(ApiError.class);
      ApiError apiError = (ApiError) response.getBody();
      assertThat(apiError.getStatus().getCode()).isEqualTo(500);
    }

    @Test
    void shouldIncludeExceptionDetails() {
      Exception exception = new Exception("Test exception");
      when(httpServletRequest.getRequestURI()).thenReturn("/api/endpoint");
      when(httpServletRequest.getMethod()).thenReturn("POST");
      when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
          .thenReturn("Error message");

      ResponseEntity<Object> response = handler.handleUnknown(exception, webRequest);

      ApiError apiError = (ApiError) response.getBody();
      assertThat(apiError.getItems()).isNotEmpty();
      assertThat(apiError.getOrigin()).isEqualTo("/api/endpoint");
      assertThat(apiError.getMethod()).isEqualTo("POST");
    }
  }

  @Nested
  class HandleNoHandlerFoundException {

    @Test
    void shouldHandleNoHandlerFoundException() throws NoHandlerFoundException {
      NoHandlerFoundException exception = new NoHandlerFoundException("GET", "/not-found", null);
      when(httpServletRequest.getRequestURI()).thenReturn("/not-found");
      when(httpServletRequest.getMethod()).thenReturn("GET");
      when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
          .thenReturn("Resource not found");

      ResponseEntity<Object> response = handler.handleNoHandlerFoundException(
          exception, null, HttpStatus.NOT_FOUND, webRequest);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
      ApiError apiError = (ApiError) response.getBody();
      assertThat(apiError.getStatus().getCode()).isEqualTo(400);
    }
  }


  @Nested
  class HandleDataIntegrityViolationException {

    @Test
    void shouldHandleDataIntegrityViolationException() {
      DataIntegrityViolationException exception =
          new DataIntegrityViolationException("Foreign key constraint violation");
      when(httpServletRequest.getRequestURI()).thenReturn("/api/save");
      when(httpServletRequest.getMethod()).thenReturn("POST");
      when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
          .thenReturn("Integrity violation");

      ResponseEntity<Object> response = handler.handleDataIntegrityViolationException(exception, webRequest);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
      ApiError apiError = (ApiError) response.getBody();
      assertThat(apiError.getStatus().getCode()).isEqualTo(409);
    }
  }

  @Nested
  class HandleTransactionSystemException {

    @Test
    void shouldHandleTransactionSystemExceptionWithConstraintViolation() {
      ConstraintViolation<?> violation = mock(ConstraintViolation.class);
      when(violation.getMessageTemplate()).thenReturn("NotNull");
      when(violation.getMessage()).thenReturn("must not be null");
      when(violation.getPropertyPath()).thenReturn(mock());
      when(violation.getPropertyPath().toString()).thenReturn("field");

      Set<ConstraintViolation<?>> violations = new HashSet<>();
      violations.add(violation);
      ConstraintViolationException constraintException = new ConstraintViolationException(violations);
      TransactionSystemException exception = new TransactionSystemException("Transaction failed", constraintException);

      when(httpServletRequest.getRequestURI()).thenReturn("/api/test");
      when(httpServletRequest.getMethod()).thenReturn("POST");

      ResponseEntity<Object> response = handler.handleTransactionSystemException(exception, webRequest);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
      ApiError apiError = (ApiError) response.getBody();
      assertThat(apiError.getItems()).isNotEmpty();
    }

    @Test
    void shouldHandleTransactionSystemExceptionWithoutConstraintViolation() {
      TransactionSystemException exception = new TransactionSystemException("Unknown transaction error");

      assertThatThrownBy(() -> handler.handleTransactionSystemException(exception, webRequest))
          .isInstanceOf(BusinessException.class);
    }
  }

  @Nested
  class HandleConstraintViolationException {

    @Test
    void shouldHandleConstraintViolationException() {
      ConstraintViolation<?> violation = mock(ConstraintViolation.class);
      when(violation.getMessage()).thenReturn("field must not be blank");
      when(violation.getPropertyPath()).thenReturn(mock());
      when(violation.getPropertyPath().toString()).thenReturn("fieldName");

      Set<ConstraintViolation<?>> violations = new HashSet<>();
      violations.add(violation);
      ConstraintViolationException exception = new ConstraintViolationException(violations);

      when(httpServletRequest.getRequestURI()).thenReturn("/api/validate");
      when(httpServletRequest.getMethod()).thenReturn("POST");

      ResponseEntity<Object> response = handler.handleConstraintViolationException(exception, webRequest);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
      ApiError apiError = (ApiError) response.getBody();
      assertThat(apiError.getItems()).isNotEmpty();
    }
  }

  @Nested
  class HandleHttpMessageNotReadable {

    @Test
    void shouldHandleInvalidFormatException() {
      JsonMappingException.Reference reference = mock(JsonMappingException.Reference.class);
      when(reference.getFieldName()).thenReturn("age");

      List<JsonMappingException.Reference> path = List.of(reference);
      InvalidFormatException exception = mock(InvalidFormatException.class);
      when(exception.getPath()).thenReturn(path);
      when(exception.getValue()).thenReturn("invalid");
      when(exception.getTargetType()).thenAnswer(invocation -> Integer.class);

      Exception rootCause = exception;
      when(httpServletRequest.getRequestURI()).thenReturn("/api/user");
      when(httpServletRequest.getMethod()).thenReturn("POST");
      when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
          .thenReturn("Invalid format");

      org.springframework.http.converter.HttpMessageNotReadableException ex =
          new org.springframework.http.converter.HttpMessageNotReadableException("Not readable", exception);

      ResponseEntity<Object> response = handler.handleHttpMessageNotReadable(ex, null, null, webRequest);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldHandleUnrecognizedPropertyException() {
      UnrecognizedPropertyException exception = mock(UnrecognizedPropertyException.class);
      when(exception.getPropertyName()).thenReturn("unknownField");

      org.springframework.http.converter.HttpMessageNotReadableException ex =
          new org.springframework.http.converter.HttpMessageNotReadableException("Not readable", exception);

      when(httpServletRequest.getRequestURI()).thenReturn("/api/test");
      when(httpServletRequest.getMethod()).thenReturn("POST");
      when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
          .thenReturn("Property not found");

      ResponseEntity<Object> response = handler.handleHttpMessageNotReadable(ex, null, null, webRequest);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldHandleIgnoredPropertyException() {
      IgnoredPropertyException exception = mock(IgnoredPropertyException.class);
      when(exception.getPropertyName()).thenReturn("ignoredField");

      org.springframework.http.converter.HttpMessageNotReadableException ex =
          new org.springframework.http.converter.HttpMessageNotReadableException("Not readable", exception);

      when(httpServletRequest.getRequestURI()).thenReturn("/api/test");
      when(httpServletRequest.getMethod()).thenReturn("POST");
      when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
          .thenReturn("Property not found");

      ResponseEntity<Object> response = handler.handleHttpMessageNotReadable(ex, null, null, webRequest);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldHandleJsonParseException() {
      JsonParseException exception = mock(JsonParseException.class);
      when(exception.getProcessor()).thenReturn(mock());
      when(exception.getProcessor().getParsingContext()).thenReturn(mock());
      when(exception.getProcessor().getParsingContext().getCurrentName()).thenReturn("field");
      when(exception.getMessage()).thenReturn("Unexpected character ('}') in numeric value");

      org.springframework.http.converter.HttpMessageNotReadableException ex =
          new org.springframework.http.converter.HttpMessageNotReadableException("Not readable", exception);

      when(httpServletRequest.getRequestURI()).thenReturn("/api/test");
      when(httpServletRequest.getMethod()).thenReturn("POST");
      when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
          .thenReturn("Invalid format");

      ResponseEntity<Object> response = handler.handleHttpMessageNotReadable(ex, null, null, webRequest);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

  @Nested
  class HandleMethodArgumentNotValid {

    @Test
    void shouldHandleMethodArgumentNotValidException() {
      BindingResult bindingResult = mock(BindingResult.class);
      FieldError fieldError = mock(FieldError.class);

      when(fieldError.getCode()).thenReturn("NotBlank");
      when(fieldError.getObjectName()).thenReturn("user");
      when(fieldError.getField()).thenReturn("email");
      when(fieldError.toString()).thenReturn("Field error details");

      when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
      when(messageSource.getMessage(fieldError, Locale.getDefault()))
          .thenReturn("Email is required");

      MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
      when(exception.getBindingResult()).thenReturn(bindingResult);

      when(httpServletRequest.getRequestURI()).thenReturn("/api/users");
      when(httpServletRequest.getMethod()).thenReturn("POST");

      ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(exception, null, null, webRequest);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
      ApiError apiError = (ApiError) response.getBody();
      assertThat(apiError.getItems()).isNotEmpty();
    }
  }

  @Nested
  class HandleEmptyResultDataAccessException {

    @Test
    void shouldHandleEmptyResultDataAccessException() {
      EmptyResultDataAccessException exception = new EmptyResultDataAccessException("No result found", 1);
      when(httpServletRequest.getRequestURI()).thenReturn("/api/users/999");
      when(httpServletRequest.getMethod()).thenReturn("GET");
      when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
          .thenReturn("Not found");

      ResponseEntity<Object> response = handler.handleEmptyResultDataAccessException(exception, webRequest);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      ApiError apiError = (ApiError) response.getBody();
      assertThat(apiError.getStatus().getCode()).isEqualTo(404);
    }
  }

  @Nested
  class HandleBindException {

    @Test
    void shouldHandleBindException() {
      BindException exception = mock(BindException.class);
      when(exception.toString()).thenReturn("BindException message");
      when(httpServletRequest.getRequestURI()).thenReturn("/api/test");
      when(httpServletRequest.getMethod()).thenReturn("POST");
      when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
          .thenReturn("Method not supported");

      ResponseEntity<Object> response = handler.handleBindException(exception, null, null, webRequest);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }
  }

  @Nested
  class HandleMethodArgumentTypeMismatchException {

    @Test
    void shouldHandleMethodArgumentTypeMismatchException() {
      MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
      when(exception.getName()).thenReturn("id");
      when(exception.getValue()).thenReturn("invalid");
      when(exception.getRequiredType()).thenAnswer(invocation -> Integer.class);

      when(httpServletRequest.getRequestURI()).thenReturn("/api/users/invalid");
      when(httpServletRequest.getMethod()).thenReturn("GET");
      when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
          .thenReturn("Invalid type");

      ResponseEntity<Object> response = handler.handleMethodArgumentTypeMismatchException(exception, webRequest);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
      ApiError apiError = (ApiError) response.getBody();
      assertThat(apiError.getItems()).isNotEmpty();
    }
  }

  @Nested
  class HandleMissingServletRequestParameter {

    @Test
    void shouldHandleMissingServletRequestParameterException() {
      MissingServletRequestParameterException exception =
          new MissingServletRequestParameterException("page", "int");
      when(httpServletRequest.getRequestURI()).thenReturn("/api/items");
      when(httpServletRequest.getMethod()).thenReturn("GET");
      when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
          .thenReturn("Parameter missing");

      ResponseEntity<Object> response = handler.handleMissingServletRequestParameter(exception, null, null, webRequest);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
      ApiError apiError = (ApiError) response.getBody();
      assertThat(apiError.getItems()).isNotEmpty();
    }
  }

  @Nested
  class HandleInvalidDataAccessApiUsageException {

    @Test
    void shouldHandleInvalidDataAccessApiUsageException() {
      InvalidDataAccessApiUsageException exception = new InvalidDataAccessApiUsageException("Invalid query");
      when(httpServletRequest.getRequestURI()).thenReturn("/api/query");
      when(httpServletRequest.getMethod()).thenReturn("GET");
      when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
          .thenReturn("Object format incorrect");

      ResponseEntity<Object> response = handler.handleInvalidDataAccessApiUsageException(exception, webRequest);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

  @Nested
  class HandleHttpRequestMethodNotSupported {

    @Test
    void shouldHandleHttpRequestMethodNotSupportedException() {
      org.springframework.web.HttpRequestMethodNotSupportedException exception =
          new org.springframework.web.HttpRequestMethodNotSupportedException("PATCH");
      when(httpServletRequest.getRequestURI()).thenReturn("/api/resource");
      when(httpServletRequest.getMethod()).thenReturn("PATCH");
      when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
          .thenReturn("Method not supported");

      ResponseEntity<Object> response = handler.handleHttpRequestMethodNotSupported(exception, null, null, webRequest);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }
  }

  @Nested
  class HandleBusinessException {

    @Test
    void shouldHandleBusinessException() {
      BusinessException exception = new BusinessException("business.error", new Object[]{"value"});
      when(httpServletRequest.getRequestURI()).thenReturn("/api/action");
      when(httpServletRequest.getMethod()).thenReturn("POST");
      when(messageSource.getMessage("business.error", new Object[]{"value"}, Locale.getDefault()))
          .thenReturn("Business rule violated");

      ResponseEntity<Object> response = handler.handlerBusinessException(exception, webRequest);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
      ApiError apiError = (ApiError) response.getBody();
      assertThat(apiError.getItems()).isNotEmpty();
    }
  }

  @Nested
  class HandleNotFoundException {

    @Test
    void shouldHandleNotFoundException() {
      NotFoundException exception = new NotFoundException("resource.not-found", new Object[]{"user"});
      when(httpServletRequest.getRequestURI()).thenReturn("/api/users/999");
      when(httpServletRequest.getMethod()).thenReturn("GET");
      when(messageSource.getMessage("resource.not-found", new Object[]{"user"}, Locale.getDefault()))
          .thenReturn("User not found");

      ResponseEntity<Object> response = handler.handlerBusinessNotFoundException(exception, webRequest);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      ApiError apiError = (ApiError) response.getBody();
      assertThat(apiError.getStatus().getCode()).isEqualTo(404);
    }
  }

  @Nested
  class HandleAuthenticationException {

    @Test
    void shouldHandleAuthenticationException() {
      AuthenticationException exception = new AuthenticationException("Invalid credentials") {};
      when(httpServletRequest.getRequestURI()).thenReturn("/api/login");
      when(httpServletRequest.getMethod()).thenReturn("POST");
      when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
          .thenReturn("Invalid credentials");

      ResponseEntity<Object> response = handler.handleAuthenticationException(exception, webRequest);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
      ApiError apiError = (ApiError) response.getBody();
      assertThat(apiError.getStatus().getCode()).isEqualTo(401);
    }
  }

  @Nested
  class HandleConflictException {

    @Test
    void shouldHandleConflictException() {
      ConflictException exception = new ConflictException("resource.duplicate", new Object[]{"email"});
      when(httpServletRequest.getRequestURI()).thenReturn("/api/users");
      when(httpServletRequest.getMethod()).thenReturn("POST");
      when(messageSource.getMessage("resource.duplicate", new Object[]{"email"}, Locale.getDefault()))
          .thenReturn("Email already exists");

      ResponseEntity<Object> response = handler.handlerConflictException(exception, webRequest);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
      ApiError apiError = (ApiError) response.getBody();
      assertThat(apiError.getStatus().getCode()).isEqualTo(409);
    }
  }

  @Nested
  class CreateErrorList {

    @Test
    void shouldCreateErrorListFromBindingResult() {
      BindingResult bindingResult = mock(BindingResult.class);
      FieldError fieldError = mock(FieldError.class);

      when(fieldError.getCode()).thenReturn("NotBlank");
      when(fieldError.getObjectName()).thenReturn("createUserRequest");
      when(fieldError.getField()).thenReturn("name");
      when(fieldError.toString()).thenReturn("Field 'name' of object 'createUserRequest' rejected");

      when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
      when(messageSource.getMessage(fieldError, Locale.getDefault()))
          .thenReturn("Name is required");

      ApiError apiError = handler.createErrorList(bindingResult, HttpStatus.BAD_REQUEST, "/api/users", "POST");

      assertThat(apiError.getOrigin()).isEqualTo("/api/users");
      assertThat(apiError.getMethod()).isEqualTo("POST");
      assertThat(apiError.getStatus().getCode()).isEqualTo(400);
      assertThat(apiError.getItems()).hasSize(1);
      assertThat(apiError.getItems().getFirst().key()).contains("NotBlank");
    }

    @Test
    void shouldHandleMultipleFieldErrors() {
      BindingResult bindingResult = mock(BindingResult.class);
      FieldError fieldError1 = mock(FieldError.class);
      FieldError fieldError2 = mock(FieldError.class);

      when(fieldError1.getCode()).thenReturn("NotBlank");
      when(fieldError1.getObjectName()).thenReturn("user");
      when(fieldError1.getField()).thenReturn("email");
      when(fieldError1.toString()).thenReturn("Email error");

      when(fieldError2.getCode()).thenReturn("Size");
      when(fieldError2.getObjectName()).thenReturn("user");
      when(fieldError2.getField()).thenReturn("password");
      when(fieldError2.toString()).thenReturn("Password error");

      when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
      when(messageSource.getMessage(any(FieldError.class), any(Locale.class)))
          .thenReturn("Error message");

      ApiError apiError = handler.createErrorList(bindingResult, HttpStatus.BAD_REQUEST, "/api/users", "POST");

      assertThat(apiError.getItems()).hasSize(2);
    }

    @Test
    void shouldHandleNoFieldErrors() {
      BindingResult bindingResult = mock(BindingResult.class);
      when(bindingResult.getFieldErrors()).thenReturn(List.of());

      ApiError apiError = handler.createErrorList(bindingResult, HttpStatus.BAD_REQUEST, "/api", "POST");

      assertThat(apiError.getItems()).isEmpty();
    }
  }
}
