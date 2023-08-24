package it.alessiomatricardi.easytask.backend.errors;

import it.alessiomatricardi.easytask.backend.exceptions.*;
import it.alessiomatricardi.easytask.backend.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// class which catch exceptions and handle them returning to the client
// an HTTP response with the corresponding status and an error body

@ControllerAdvice
public class CustomControllerAdvice {

    // handle entity not found exceptions

    @ExceptionHandler(value = {
            EmployeeNotFoundException.class,
            ProjectNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(Exception e) {

        HttpStatus status = HttpStatus.NOT_FOUND; // 404

        return new ResponseEntity<>(
                new ErrorResponse(status, e.getMessage()),
                status
        );
    }

    // handle already existing entity exceptions

    @ExceptionHandler(value = {
            EmployeeAlreadyExistsException.class,
            ProjectAlreadyExistsException.class
    })
    public ResponseEntity<ErrorResponse> handleAlreadyExistsExceptions(Exception e) {

        HttpStatus status = HttpStatus.CONFLICT; // 409

        return new ResponseEntity<>(
                new ErrorResponse(status, e.getMessage()),
                status
        );
    }

    // handle new password mismatch exceptions

    @ExceptionHandler(value = {
            PasswordsMismatchException.class,
            SelfInsertionException.class,
            SelfDeletionException.class,
            MemberHasAssignedTasksException.class,
            EmployeeIsNotAMemberException.class,
            ClosedProjectException.class,
            CompletedTaskException.class,
            NotApprovedTaskException.class,
            InvalidTaskStatusChangeException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestsExceptions(Exception e) {

        HttpStatus status = HttpStatus.BAD_REQUEST; // 400

        return new ResponseEntity<>(
                new ErrorResponse(status, e.getMessage()),
                status
        );
    }

    // handle forbidden exceptions

    @ExceptionHandler(value = {ForbiddenOperationException.class})
    public ResponseEntity<ErrorResponse> handleForbiddenExceptions(Exception e) {

        HttpStatus status = HttpStatus.FORBIDDEN; // 403

        return new ResponseEntity<>(
                new ErrorResponse(status, e.getMessage()),
                status
        );
    }

    // handle bad credentials exceptions

    @ExceptionHandler(value = {
            BadCredentialsException.class
    })
    public ResponseEntity<ErrorResponse> handleBadCredentialsExceptions(Exception e) {

        HttpStatus status = HttpStatus.UNAUTHORIZED; // 401

        return new ResponseEntity<>(
                new ErrorResponse(status, e.getMessage()),
                status
        );
    }

    // handle others exceptions

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 500

        return new ResponseEntity<>(
                new ErrorResponse(status, e.getMessage()),
                status
        );
    }

}
