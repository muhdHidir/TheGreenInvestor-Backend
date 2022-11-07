package G2T6.G2T6.G2T6.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SimilarityRequest {
    @NotBlank
    private String input;

    @NotBlank
    private String target;
}
