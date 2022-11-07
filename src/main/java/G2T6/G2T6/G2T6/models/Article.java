package G2T6.G2T6.G2T6.models;

import lombok.*;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Article {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;
    @Column(length = 100000)
    private String body;
    private String article;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "questionId", referencedColumnName = "id")
    private Question question;

    public Article(String body, String article) {
        this.body = body;
        this.article = article;
    }

    public Article(String body, String article, Question question) {
        this.body = body;
        this.article = article;
        this.question = question;
    }
}
