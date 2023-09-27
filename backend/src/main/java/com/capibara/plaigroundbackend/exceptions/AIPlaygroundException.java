package com.capibara.plaigroundbackend.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public class AIPlaygroundException extends RuntimeException {

    private HttpStatusCode httpStatusCode = HttpStatusCode.valueOf(500);
    private String errorLocation;


    public AIPlaygroundException(String message) {
        super(message);
    }

    public AIPlaygroundException(String message, HttpStatusCode httpStatusCode) {
        super(message);
        this.httpStatusCode = HttpStatusCode.valueOf(httpStatusCode.value());
    }

    public String getErrorLocation() {

        StackTraceElement[] stackTrace = this.getStackTrace();

        if (stackTrace != null && stackTrace.length > 0) {
            StackTraceElement topStackTraceElement = stackTrace[0];
            String className = topStackTraceElement.getClassName();
            String methodName = topStackTraceElement.getMethodName();
            int lineNumber = topStackTraceElement.getLineNumber();
            return className + "." + methodName + " (line" + lineNumber + ")";
        }

        return "Unknown Location";
    }
}
