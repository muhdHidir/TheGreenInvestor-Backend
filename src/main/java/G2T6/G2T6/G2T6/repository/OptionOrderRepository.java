package G2T6.G2T6.G2T6.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import G2T6.G2T6.G2T6.models.orders.OptionOrder;

@Repository
public interface OptionOrderRepository extends JpaRepository<OptionOrder, Long> {
    List<OptionOrder> findAll();
    Optional<OptionOrder> findById(final Long id);
}
	
