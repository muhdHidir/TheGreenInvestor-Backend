// package G2T6.G2T6.G2T6.models;

// import java.util.List;

// import javax.persistence.*;

// import org.springframework.beans.factory.annotation.Autowired;

// import G2T6.G2T6.G2T6.services.QuestionService;
// import lombok.*;
// @Entity
// @Getter
// @Setter
// @ToString
// @EqualsAndHashCode
// public class QuestionSet {

//     @Autowired
//     private QuestionService questionService;
    
//     // QuestionSet Id is Primary Key of QuestionSet Table
//     private @Id @GeneratedValue (strategy = GenerationType.IDENTITY) Long id;

//     @OneToOne
//     @JoinColumn(name = "current_state_id")
//     private CurrentState currentState;

//     // List of questions, one questionSet to many questions
//     @OneToMany(mappedBy = "questionSet")
//     private List<Question> questions;

//     // constructor - default question set
//     public QuestionSet() {
//         this.questions = questionService.listQuestions();
//     }

// }
