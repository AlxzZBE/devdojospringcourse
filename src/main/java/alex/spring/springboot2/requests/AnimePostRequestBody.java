package alex.spring.springboot2.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnimePostRequestBody {
    @NotBlank(message = "The anime name cannot be empty or null")
    @Length(min = 3, max = 100, message = "The name should be between 3 and 100 characters")
    @Schema(description = "This is the Anime's name", example = "Kung fu Panda")
    private String name;
}
