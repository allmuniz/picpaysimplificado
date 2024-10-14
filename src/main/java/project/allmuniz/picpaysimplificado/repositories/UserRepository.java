package project.allmuniz.picpaysimplificado.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.allmuniz.picpaysimplificado.domain.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByDocument(String document);
}
