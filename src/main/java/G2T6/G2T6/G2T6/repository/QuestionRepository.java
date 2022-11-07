package G2T6.G2T6.G2T6.repository;

import java.util.List;
import java.util.Optional;

import G2T6.G2T6.G2T6.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByQuestion(final String question);

    Optional<Question> findById(Long id);
}
