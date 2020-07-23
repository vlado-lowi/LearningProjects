package engine;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompletionsRepository extends JpaRepository<Completions, Long> {
    List<Completions> findAllByUserId(Long ID);
}
