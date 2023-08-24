package it.alessiomatricardi.easytask.backend.errors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

// object returned by the controller advice in the HTTP response body

@Getter
@Setter
public class ErrorResponse {

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private int code;

    private String status;

    private String message;

    private boolean isTokenExpired;

    public ErrorResponse(HttpStatus httpStatus, String message) {
        this.timestamp = LocalDateTime.now();
        this.code = httpStatus.value();
        this.status = httpStatus.name();
        this.message = message;
        this.isTokenExpired = false;
    }

    public ErrorResponse(HttpStatus httpStatus, String message, boolean isTokenExpired) {
        this(httpStatus, message);
        this.isTokenExpired = isTokenExpired;
    }
}
