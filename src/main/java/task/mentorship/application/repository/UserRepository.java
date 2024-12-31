package task.mentorship.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import task.mentorship.application.entity.*;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); 
    boolean existsByEmail(String email);      
}
