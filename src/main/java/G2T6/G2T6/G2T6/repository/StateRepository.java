package G2T6.G2T6.G2T6.repository;

import G2T6.G2T6.G2T6.models.CurrentState;
import G2T6.G2T6.G2T6.models.GameStats;
import G2T6.G2T6.G2T6.models.security.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<CurrentState, Long> {
    List<CurrentState> findByUserId(Long userId);
    Optional<CurrentState> findByIdAndUserId(Long id, Long userId);
    Optional<CurrentState> findTopByUserOrderByIdDesc(User currUser);
    List<CurrentState> findByGameIdAndUserId(Long gameId, Long userId);
}
