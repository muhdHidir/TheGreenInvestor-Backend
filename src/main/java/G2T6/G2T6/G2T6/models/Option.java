package G2T6.G2T6.G2T6.models;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Option {

    // Option Id is Primary Key of Option Table
    private @Id @GeneratedValue (strategy = GenerationType.AUTO) Long id;

    // Option Body
    @NotNull(message = "Option should not be null")
    @Length(max=400)
    private String option;

    // Feedback after selecting an option
    @NotNull(message = "Feedback should not be null")
    @Length(max=400)
    private String feedback;

    // impact on GameStats
    @Min(-1000) @Max(1000) @NotNull
    private int sustainabilityImpact; //impact on Sustainability Score
    @Min(-100) @Max(100) @NotNull
    private int moraleImpact; //impact on Morale
    @Min(-100) @Max(100) @NotNull
    private int incomeImpact; //impact on Income
    @Min(0) @Max(100) @NotNull
    private int costImpact; // impact on Cash Stash

    // question that option is mapped to 
    @ManyToOne
    @JoinColumn(name="question_id", nullable=false)
    @JsonIgnore // ignore in json object to avoid infinite loop of referencing
    private Question question;

    // constructor1 that takes in option, feedback, question
    public Option(final String option, final String feedback, final Question qn) {
        this.option = option;
        this.feedback = feedback;
        this.question = qn;
    }

    // constructor2 that takes in  gameStat metrics in addiion to option, feedback, question
    public Option(final String option, final String feedback, final Question qn, final int sustainabilityImpact, final int moraleImpact ,final int incomeImpact, final int costImpact) {
        this(option, feedback, qn); // calls constructor1
        this.sustainabilityImpact = sustainabilityImpact;
        this.moraleImpact = moraleImpact;
        this.incomeImpact = incomeImpact;
        this.costImpact = costImpact;
    }

    public void replaceOption(Option newOption) {
        this.option = newOption.getOption();
        this.feedback = newOption.getFeedback();
        this.sustainabilityImpact = newOption.getSustainabilityImpact();
        this.moraleImpact = newOption.getMoraleImpact();
        this.incomeImpact = newOption.getIncomeImpact();
        this.costImpact = newOption.getCostImpact();
    }
}
