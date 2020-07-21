package engine;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizCRUDRepository extends JpaRepository<Quiz, Long> {
}
