package org.apache.coyote.http11.model.response;

public enum Status {

    OK(200),
    CREATED(201),
    FOUND(302),
    UNAUTHORIZED(401),
    INTERNAL_SERVER_ERROR(500),
    ;

    private final int code;

    Status(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
