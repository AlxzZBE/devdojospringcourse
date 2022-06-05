package alex.spring.springboot2.exception;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
public class ExceptionDetails {
    protected LocalDateTime timeStamp;
    protected String title;
    protected int status;
    protected String details;
    protected String developerMessage;
}
