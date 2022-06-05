package alex.spring.springboot2.repositories;

import alex.spring.springboot2.domain.Anime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Conexão direta com o banco de dados
public interface AnimeRepository extends JpaRepository<Anime, Long> {
    List<Anime> findByName(String name);
}
