package dev.williancorrea.manhwa.reader.exception;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApiErrorTest {

  @Nested
  class Constructor {

    @Test
    void shouldCreateApiErrorWithAllFields() {
      String origin = "/api/test";
      String method = "POST";
      List<ApiError.ErrorItem> errors = List.of(
          new ApiError.ErrorItem("key", "message", "detail")
      );

      ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, origin, method, errors);

      assertThat(apiError.getOrigin()).isEqualTo(origin);
      assertThat(apiError.getMethod()).isEqualTo(method);
      assertThat(apiError.getItems()).isEqualTo(errors);
      assertThat(apiError.getStatus()).isNotNull();
    }

    @Test
    void shouldSetDateTimeToNow() {
      OffsetDateTime before = OffsetDateTime.now();
      ApiError apiError = new ApiError(HttpStatus.OK, "/api", "GET", new ArrayList<>());
      OffsetDateTime after = OffsetDateTime.now();

      assertThat(apiError.getDateTime()).isNotNull();
      assertThat(apiError.getDateTime()).isAfterOrEqualTo(before);
      assertThat(apiError.getDateTime()).isBeforeOrEqualTo(after);
    }

    @Test
    void shouldHandleEmptyErrorList() {
      ApiError apiError = new ApiError(HttpStatus.OK, "/api", "GET", new ArrayList<>());

      assertThat(apiError.getItems()).isEmpty();
    }

    @Test
    void shouldHandleMultipleErrors() {
      List<ApiError.ErrorItem> errors = List.of(
          new ApiError.ErrorItem("key1", "message1", "detail1"),
          new ApiError.ErrorItem("key2", "message2", "detail2"),
          new ApiError.ErrorItem("key3", "message3", "detail3")
      );

      ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "/api", "POST", errors);

      assertThat(apiError.getItems()).hasSize(3);
      assertThat(apiError.getItems()).containsAll(errors);
    }
  }

  @Nested
  class GetOrigin {

    @Test
    void shouldReturnOriginPath() {
      String origin = "/api/users";
      ApiError apiError = new ApiError(HttpStatus.OK, origin, "GET", new ArrayList<>());

      assertThat(apiError.getOrigin()).isEqualTo(origin);
    }
  }

  @Nested
  class GetMethod {

    @Test
    void shouldReturnHttpMethod() {
      ApiError apiError = new ApiError(HttpStatus.CREATED, "/api", "POST", new ArrayList<>());

      assertThat(apiError.getMethod()).isEqualTo("POST");
    }

    @Test
    void shouldHandleDifferentHttpMethods() {
      assertThat(new ApiError(HttpStatus.OK, "/api", "GET", new ArrayList<>()).getMethod()).isEqualTo("GET");
      assertThat(new ApiError(HttpStatus.OK, "/api", "PUT", new ArrayList<>()).getMethod()).isEqualTo("PUT");
      assertThat(new ApiError(HttpStatus.OK, "/api", "DELETE", new ArrayList<>()).getMethod()).isEqualTo("DELETE");
      assertThat(new ApiError(HttpStatus.OK, "/api", "PATCH", new ArrayList<>()).getMethod()).isEqualTo("PATCH");
    }
  }

  @Nested
  class GetStatus {

    @Test
    void shouldReturnStatusWithCodeAndDescription() {
      ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, "/api", "GET", new ArrayList<>());

      ApiError.Status status = apiError.getStatus();
      assertThat(status).isNotNull();
      assertThat(status.getCode()).isEqualTo(404);
      assertThat(status.getDescription()).isEqualTo("Not Found");
    }

    @Test
    void shouldMapHttpStatusCorrectly() {
      assertThat(new ApiError(HttpStatus.BAD_REQUEST, "/api", "GET", new ArrayList<>())
          .getStatus().getCode()).isEqualTo(400);
      assertThat(new ApiError(HttpStatus.UNAUTHORIZED, "/api", "GET", new ArrayList<>())
          .getStatus().getCode()).isEqualTo(401);
      assertThat(new ApiError(HttpStatus.FORBIDDEN, "/api", "GET", new ArrayList<>())
          .getStatus().getCode()).isEqualTo(403);
      assertThat(new ApiError(HttpStatus.CONFLICT, "/api", "GET", new ArrayList<>())
          .getStatus().getCode()).isEqualTo(409);
      assertThat(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "/api", "GET", new ArrayList<>())
          .getStatus().getCode()).isEqualTo(500);
    }

    @Test
    void shouldMapHttpStatusReasonPhraseCorrectly() {
      assertThat(new ApiError(HttpStatus.BAD_REQUEST, "/api", "GET", new ArrayList<>())
          .getStatus().getDescription()).isEqualTo("Bad Request");
      assertThat(new ApiError(HttpStatus.UNAUTHORIZED, "/api", "GET", new ArrayList<>())
          .getStatus().getDescription()).isEqualTo("Unauthorized");
      assertThat(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "/api", "GET", new ArrayList<>())
          .getStatus().getDescription()).isEqualTo("Internal Server Error");
    }
  }

  @Nested
  class GetDateTime {

    @Test
    void shouldReturnCurrentDateTime() {
      OffsetDateTime before = OffsetDateTime.now();
      ApiError apiError = new ApiError(HttpStatus.OK, "/api", "GET", new ArrayList<>());
      OffsetDateTime after = OffsetDateTime.now();

      OffsetDateTime dateTime = apiError.getDateTime();
      assertThat(dateTime).isNotNull();
      assertThat(dateTime).isAfterOrEqualTo(before);
      assertThat(dateTime).isBeforeOrEqualTo(after);
    }
  }

  @Nested
  class GetItems {

    @Test
    void shouldReturnErrorItems() {
      ApiError.ErrorItem item1 = new ApiError.ErrorItem("key1", "message1", "detail1");
      ApiError.ErrorItem item2 = new ApiError.ErrorItem("key2", "message2", "detail2");
      List<ApiError.ErrorItem> errors = List.of(item1, item2);

      ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "/api", "POST", errors);

      assertThat(apiError.getItems()).containsExactly(item1, item2);
    }
  }

  @Nested
  class StatusClass {

    @Nested
    class Constructor {

      @Test
      void shouldCreateStatusWithCodeAndDescription() {
        Integer code = 400;
        ApiError.Status status = new ApiError.Status(code, "Bad Request");

        assertThat(status.getCode()).isEqualTo(400);
        assertThat(status.getDescription()).isEqualTo("Bad Request");
      }

      @Test
      void shouldAcceptIntegerCode() {
        Integer code = 500;
        ApiError.Status status = new ApiError.Status(code, "Internal Server Error");

        assertThat(status.getCode()).isEqualTo(500);
      }
    }

    @Nested
    class GetCode {

      @Test
      void shouldReturnStatusCode() {
        ApiError.Status status = new ApiError.Status(404, "Not Found");

        assertThat(status.getCode()).isEqualTo(404);
      }
    }

    @Nested
    class GetDescription {

      @Test
      void shouldReturnStatusDescription() {
        ApiError.Status status = new ApiError.Status(403, "Forbidden");

        assertThat(status.getDescription()).isEqualTo("Forbidden");
      }
    }
  }

  @Nested
  class ErrorItemRecord {

    @Nested
    class Constructor {

      @Test
      void shouldCreateErrorItemWithAllFields() {
        ApiError.ErrorItem item = new ApiError.ErrorItem("key", "message", "detail");

        assertThat(item.key()).isEqualTo("key");
        assertThat(item.message()).isEqualTo("message");
        assertThat(item.detail()).isEqualTo("detail");
      }

      @Test
      void shouldConvertNullKeyToEmptyString() {
        ApiError.ErrorItem item = new ApiError.ErrorItem(null, "message", "detail");

        assertThat(item.key()).isEqualTo("");
      }

      @Test
      void shouldConvertNullDetailToEmptyString() {
        ApiError.ErrorItem item = new ApiError.ErrorItem("key", "message", null);

        assertThat(item.detail()).isEqualTo("");
      }

      @Test
      void shouldKeepMessageAsIs() {
        ApiError.ErrorItem item = new ApiError.ErrorItem("key", "error message", "detail");

        assertThat(item.message()).isEqualTo("error message");
      }

      @Test
      void shouldHandleNullKeyAndNullDetail() {
        ApiError.ErrorItem item = new ApiError.ErrorItem(null, "message", null);

        assertThat(item.key()).isEqualTo("");
        assertThat(item.message()).isEqualTo("message");
        assertThat(item.detail()).isEqualTo("");
      }

      @Test
      void shouldHandleEmptyStringKey() {
        ApiError.ErrorItem item = new ApiError.ErrorItem("", "message", "detail");

        assertThat(item.key()).isEqualTo("");
      }

      @Test
      void shouldHandleEmptyStringDetail() {
        ApiError.ErrorItem item = new ApiError.ErrorItem("key", "message", "");

        assertThat(item.detail()).isEqualTo("");
      }
    }

    @Nested
    class Key {

      @Test
      void shouldAccessKeyField() {
        ApiError.ErrorItem item = new ApiError.ErrorItem("error.key", "message", "detail");

        assertThat(item.key()).isEqualTo("error.key");
      }
    }

    @Nested
    class Message {

      @Test
      void shouldAccessMessageField() {
        ApiError.ErrorItem item = new ApiError.ErrorItem("key", "This is an error message", "detail");

        assertThat(item.message()).isEqualTo("This is an error message");
      }
    }

    @Nested
    class Detail {

      @Test
      void shouldAccessDetailField() {
        ApiError.ErrorItem item = new ApiError.ErrorItem("key", "message", "Additional detail info");

        assertThat(item.detail()).isEqualTo("Additional detail info");
      }
    }

    @Nested
    class Equality {

      @Test
      void shouldTwoErrorItemsBeEqual() {
        ApiError.ErrorItem item1 = new ApiError.ErrorItem("key", "message", "detail");
        ApiError.ErrorItem item2 = new ApiError.ErrorItem("key", "message", "detail");

        assertThat(item1).isEqualTo(item2);
      }

      @Test
      void shouldTwoErrorItemsNotBeEqual() {
        ApiError.ErrorItem item1 = new ApiError.ErrorItem("key", "message", "detail");
        ApiError.ErrorItem item2 = new ApiError.ErrorItem("key", "message", "different");

        assertThat(item1).isNotEqualTo(item2);
      }
    }
  }
}
