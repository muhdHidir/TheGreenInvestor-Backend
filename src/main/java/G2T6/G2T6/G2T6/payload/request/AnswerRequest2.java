package G2T6.G2T6.G2T6.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerRequest2 {

    @NotBlank
    private String answer;

}
