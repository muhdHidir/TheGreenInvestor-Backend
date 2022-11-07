package G2T6.G2T6.G2T6.models;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import lombok.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Question {

    // Question Id is Primary Key of Question Table
    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;

    // Question Body
    @NotNull(message = "Question should not be null")
    @Length(max = 300)
    private String question;

    // Link to Image hosted on AWS
    @NotNull(message = "Image location should not be null")
    @URL(protocol = "https")
    private String imageLink;

    // List of options, one question to many options
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options;

    // whether question is open-ended
    @NotNull(message = "Question type should not be null")
    private boolean isOpenEnded;

    // article and its url
    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Article article;

    // constructor - pass in question, imageLink, isOpenEnded
    public Question(final String qn, final String imageLink, final boolean isOpenEnded) {
        this.question = qn;
        this.imageLink = imageLink;
        this.isOpenEnded = isOpenEnded;
    }

    // constructor - pass in question, imageLink, isOpenEnded, article
    public Question(final String qn, final String imageLink, final boolean isOpenEnded, final Article article) {
        this.question = qn;
        this.imageLink = imageLink;
        this.isOpenEnded = isOpenEnded;
        this.article = article;
    }

    // constructor - testing purposes, accepts list of options
    public Question(final String qn, final String imageLink, final List<Option> options, final boolean isOpenEnded, final Article article) {
        this(qn, imageLink, isOpenEnded);
        this.options = options;
    }

    // compare two questions, check for equality - testing purposes
    // @Override
    // public boolean equals(Object question) {
    // if (!(question instanceof Question)) {
    //     return false;
    // }
    // Question qn = (Question) question;

    // return (this.id == qn.getId()) && (this.question == qn.getQuestion()) &&
    //     (this.imageLink == qn.getImageLink()) && (this.options == qn.getOptions())
    //     && (this.isOpenEnded == qn.isOpenEnded()) && (this.article == qn.getArticle());
    // }
}
