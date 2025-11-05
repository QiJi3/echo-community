package com.echo.api;

public class ApiException extends RuntimeException {
  private final int status;
  private final int code;

  public ApiException(int status, String message) {
    this(status, defaultCode(status), message);
  }

  public ApiException(int status, int code, String message) {
    super(message);
    this.status = status;
    this.code = code;
  }

  public int getStatus() {
    return status;
  }

  public int getCode() {
    return code;
  }

  private static int defaultCode(int status) {
    if (status == 400) {
      return 400001;
    }
    if (status == 401) {
      return 401001;
    }
    if (status == 403) {
      return 403001;
    }
    if (status == 404) {
      return 404001;
    }
    if (status == 409) {
      return 409001;
    }
    return 500001;
  }
}
