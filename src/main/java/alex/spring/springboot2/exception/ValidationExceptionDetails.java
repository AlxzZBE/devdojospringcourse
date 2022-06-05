package alex.spring.springboot2.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class ValidationExceptionDetails extends ExceptionDetails {
    private List<FieldMessage> fieldsErrors = new ArrayList<>();
}


