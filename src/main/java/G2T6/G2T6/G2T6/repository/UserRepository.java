package G2T6.G2T6.G2T6.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import G2T6.G2T6.G2T6.models.security.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  
  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);

  //findbyemail
  Optional<User> findByEmail(String email);

  Optional<User> findById(Long id);

  //findbyusername
  Optional<User> findByUsername(String username);
}
