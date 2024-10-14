package project.allmuniz.picpaysimplificado.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.allmuniz.picpaysimplificado.domain.transaction.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
