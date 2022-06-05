package alex.spring.springboot2.repositories;

import alex.spring.springboot2.domain.DevDojoUser;
import org.springframework.data.jpa.repository.JpaRepository;

// Conexão direta com o banco de dados
public interface DevDojoUserRepository extends JpaRepository<DevDojoUser, Long> {
    DevDojoUser findByUsername(String username);
}
