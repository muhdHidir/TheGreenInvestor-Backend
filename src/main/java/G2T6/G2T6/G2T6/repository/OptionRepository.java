package G2T6.G2T6.G2T6.repository;

import java.util.List;
import java.util.Optional;

import G2T6.G2T6.G2T6.models.Option;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository <Option, Long> {
    List<Option> findByQuestionId(final Long questionId);
    Optional<Option> findByIdAndQuestionId(final Long id, final Long questionId);
    void deleteById(final Long id);
}
