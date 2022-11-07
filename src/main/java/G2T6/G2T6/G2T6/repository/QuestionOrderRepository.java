package G2T6.G2T6.G2T6.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import G2T6.G2T6.G2T6.models.orders.QuestionOrder;

@Repository
public interface QuestionOrderRepository extends JpaRepository<QuestionOrder, Long> {
    List<QuestionOrder> findAll();
    Optional<QuestionOrder> findById(final Long id);
}
